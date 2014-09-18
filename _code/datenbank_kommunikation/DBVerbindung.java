package datenbank_kommunikation;

import java.sql.*;

public class DBVerbindung {
	
	private final String DB_PFAD = "~/.swert/swert";
	private final int DB_VERSION_NEEDED = 1;
	
	private static Connection con = null;
	private DBTableConfig config;
	private DBTableAthlet athlet;
	private DBTableLeistung leistung;
	private DBTableStrecke strecke;
	private int db_version_existing;
	
	public DBVerbindung () {
		try {
			con = DriverManager.getConnection("jdbc:h2:"+DB_PFAD, "", "");
			config = new DBTableConfig();
			athlet = new DBTableAthlet();
			leistung = new DBTableLeistung();
			strecke = new DBTableStrecke();
			aktualisiereDbVersion();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Beendet die Verbindung zur H2-Datenbank und schlie�t das entsprechende Statement-Objekt
	 */
	public void verbindungBeenden () {
		try {
			if (con != null) {
				con.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected static PreparedStatement getStatement(String query) {
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(query);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return stmt;
	}
	
	public DBTableConfig getDBTableConfig() {
		return config;
	}
	
	public DBTableAthlet getDBTableAthlet() {
		return athlet;
	}
	
	public DBTableLeistung getDBTableLeistung() {
		return leistung;
	}
	
	public DBTableStrecke getDBTableStrecke() {
		return strecke;
	}
	
	/**
	 * Vergleicht die aktuelle installiert DB-Version mit der ben�tigten DB-Version.
	 * Falls n�tig werden inkrementelle Updates durchgef�hrt, bis die ben�tigte DB-Version installiert ist.
	 * @throws SQLException
	 */
	private void aktualisiereDbVersion () throws SQLException {
		db_version_existing = getAktuelleDbVersion();
		if (db_version_existing == DB_VERSION_NEEDED) {
			return;
		}
		if (db_version_existing > DB_VERSION_NEEDED){
			// to do: notify user
			System.out.println ("Bitte neue Applikation verwenden/installieren!");
			return;
		}

		System.out.println("Update notwendig");
		switch (db_version_existing) {
			case 0: // kein Schema installiert
				installiereSchemaVersion_1();
			case 1:
				System.out.println("Passt, solage DB-Version 1 von der Applikation ben�tigt wird " +
						"alternativ m�sste hier ein update aufgerufen werden.");
		}
	}
	
	/**
	 * Abrufen der Version der aktuellen, lokalen Datenbank
	 * @return Versionsnummer der Datenbank (0 falls keine lokale DB installiert ist)
	 * @throws SQLException
	 */
	private int getAktuelleDbVersion () throws SQLException {
		int db_version = 0;
		if (pruefeTabelle ("Config")) {
			String query = "SELECT db_version FROM Config LIMIT 1";
			ResultSet versionRS = getStatement(query).executeQuery();
			while (versionRS.next()) {
				db_version = versionRS.getInt(1);
			}			
			versionRS.close();
		}
		return db_version;
	}
	
	/**
	 * Pr�ft, ob eine bestimmte Tabelle in der Datenbank vorhanden ist.
	 * @param table_name: Zu suchender Tabellen-Name
	 * @return TRUE, wenn Tabelle vorhanden ist | FALSE, wenn Tabelle nicht vorhanden ist
	 * @throws SQLException
	 */
	private boolean pruefeTabelle (String table_name) throws SQLException {
		String tablesQ = "SELECT TABLE_NAME "
				+ "FROM INFORMATION_SCHEMA.TABLES "
				+ "WHERE TABLE_SCHEMA='PUBLIC' AND TABLE_NAME=?";
        PreparedStatement stmt = getStatement(tablesQ);
        stmt.setString(1, table_name.toUpperCase());
		ResultSet tablesRS = stmt.executeQuery();
        while (tablesRS.next()) {
        	tablesRS.close();
        	stmt.close();
        	return true;
        }
        tablesRS.close();
    	stmt.close();
		return false;
	}
	
	/**
	 * Installiert die Datenbank-Version 1.
	 * Diese Methode wird nur aufgerufen, wenn noch keine lokale Datenbank "swert" verf�gbar ist.
	 * @throws SQLException 
	 */
	private void installiereSchemaVersion_1 () throws SQLException {
		System.out.println("Schema V1 wird installiert");
		config.installiereSchemaVersion1();
		athlet.installiereSchemaVersion1();
		strecke.installiereSchemaVersion1();
		leistung.installiereSchemaVersion1();
	}
}
