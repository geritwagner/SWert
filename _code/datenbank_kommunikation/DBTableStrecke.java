package datenbank_kommunikation;

import java.sql.ResultSet;
import java.sql.SQLException;
import model.Strecke;

public class DBTableStrecke extends DBTableAbstract{

	/**
	 * Konstruktor, der den Super-Konstruktor der Klasse DBTableAbstract aufruft
	 */
	public DBTableStrecke() {
		super();
	}

	/**
	 * Installiert die Tabelle 'Strecke' für die Datenbank-Schema-Version 1
	 */
	public void installiereSchemaVersion1() throws SQLException {
		String createStrecke = "CREATE TABLE IF NOT EXISTS Strecke "
				+ "(strecke_id INT PRIMARY KEY AUTO_INCREMENT(1,1) NOT NULL, "
				+ "bezeichnung VARCHAR(255) NOT NULL,"
				+ "laenge INT NOT NULL)";
		stmt.executeUpdate(createStrecke);
		String insertStrecke = "INSERT INTO Strecke (strecke_id, bezeichnung, laenge)"
				+ "VALUES (1, '400m', 400), "
				+ "(2, '800m', 800),"
				+ "(3, '1.000m', 1000), "
				+ "(4, '1.500m', 1500), "
				+ "(5, '2.000m', 2000), "
				+ "(6, '3.000m', 3000), "
				+ "(7, '5.000m', 5000), "
				+ "(8, '10.000m', 10000), "
				+ "(9, '15km', 15000), "
				+ "(10, 'Halbmarathon', 21098), "
				+ "(11, '25km', 25000), "
				+ "(12, 'Marathon', 42195),";
		stmt.executeUpdate(insertStrecke);
	}
	
	/**
	 * Fügt in die 'Strecken'-Tabelle der Datenbank eine neue Zeile ein
	 * @param bezeichnung: Bezeichnung der Strecke
	 * @param laenge: Laenge der Strecke
	 * @return ID der eingefügten Strecke; 0 falls die Strecke nicht eingefügt werden konnte
	 * @throws SQLException
	 */
	public int einfuegen (String bezeichnung, int laenge) throws SQLException {
		String insertStrecke = "INSERT INTO Strecke (bezeichnung, laenge) "
				+ "VALUES ('"+bezeichnung+"', "+laenge+")";
		stmt.executeUpdate(insertStrecke);
		ResultSet result_id = stmt.getGeneratedKeys();
		if (result_id.next()) {
			return result_id.getInt(1);
		}
		return -1;
	}
	
	/**
	 * Setzt Bezeichung und Länge einer Zeile in der 'Strecke'-Tabelle neu
	 * @param strecke_id: ID zum Identifiziern der zu verändernden Zeile
	 * @param bezeichnung: Neue Bezeichnung der Strecke
	 * @param laenge: Neue Streckenlänge
	 * @throws SQLException
	 */
	public void aendern (int strecke_id, String bezeichnung, int laenge) throws SQLException {
		String updateStrecke = "UPDATE Strecke SET bezeichnung='"+bezeichnung+"', laenge="+laenge+" "
				+ "WHERE strecke_id="+strecke_id;
		stmt.executeUpdate(updateStrecke);
	}
	
	/**
	 * Ruft eine Zeile aus der 
	 * @param strecke_id
	 * @return
	 * @throws SQLException
	 */
	public Strecke abrufen (int strecke_id) throws SQLException {	
		Strecke strecke = null;
		String selectStrecke = "SELECT * FROM Strecke WHERE strecke_id="+strecke_id+" LIMIT 1";
		ResultSet result = stmt.executeQuery(selectStrecke);
		while(result.next()) {
			strecke_id = result.getInt(1);
			String bezeichnung = result.getString(2);
			int laenge = result.getInt(3);
			strecke = new Strecke(strecke_id, bezeichnung, laenge);
		}
		return strecke;
	}
	
	/**
	 * Löscht eine Zeile aus der 'Strecke'-Tabelle
	 * @param strecke_id: ID der zu löschenden Zeile
	 * @throws SQLException
	 */
	public void loeschen (int strecke_id) throws SQLException {
		String deleteStrecke = "DELETE FROM Strecke WHERE strecke_id="+strecke_id;
		stmt.executeUpdate(deleteStrecke);
	}
}
