package datenbank_kommunikation;

import java.sql.ResultSet;
import java.sql.SQLException;

import datenbank_zugriff.DBStrecke;

public class DBTableLeistung extends DBTableAbstract {

	private DBStrecke dbStrecke;
	
	public DBTableLeistung() {
		super();
		dbStrecke = new DBStrecke();
	}

	public void installiereSchemaVersion1() throws SQLException {
		String createLeistung = "CREATE TABLE IF NOT EXISTS Leistung "
				+ "(leistung_id INT PRIMARY KEY AUTO_INCREMENT(1,1) NOT NULL, "
				+ "beschreibung VARCHAR(255) NOT NULL,"
				+ "datum VARCHAR(100) NOT NULL,"
				+ "zeit DOUBLE NOT NULL,"
				+ "selectedForCalculatingSlopeFaktor BOOLEAN,"
				+ "athlet_id INT,"
				+ "strecke_id INT,"
				+ "FOREIGN KEY(strecke_id) REFERENCES STRECKE(STRECKE_ID),"
				+ "FOREIGN KEY(athlet_id) REFERENCES ATHLET(ATHLET_ID))";
		stmt.executeUpdate(createLeistung);
	}

	public int einfuegen (String beschreibung, String datum, double zeit, boolean selectedForCalculatingSlopeFaktor, int athlet_id,	int strecke_id,	int strecke_laenge) throws SQLException {
		int leistung_id = 0;
		if (strecke_id == -1) {
			leistung_id = einfuegenMitNeueStrecke(beschreibung, datum, zeit, selectedForCalculatingSlopeFaktor, athlet_id, strecke_laenge);
		} else {
			leistung_id = einfuegenOhneNeueStrecke(beschreibung, datum, zeit, selectedForCalculatingSlopeFaktor, athlet_id, strecke_id);
		}
		return leistung_id;
	}
	
	
	
	private int einfuegenMitNeueStrecke (String beschreibung, String datum, double zeit, boolean selectedForCalculatingSlopeFaktor, int athlet_id,	int strecke_laenge) throws SQLException {
		int strecke_id = dbStrecke.neueStrecke(strecke_laenge);
		return einfuegenOhneNeueStrecke(beschreibung, datum, zeit, selectedForCalculatingSlopeFaktor, athlet_id, strecke_id);
	}
	
	private int einfuegenOhneNeueStrecke (String beschreibung, String datum, double zeit, boolean selectedForCalculatingSlopeFaktor, int athlet_id, int strecke_id) throws SQLException {
		String insertLeistung = "INSERT INTO Leistung(beschreibung, datum, zeit, selectedForCalculatingSlopeFaktor, athlet_id, strecke_id)"
				+ "VALUES ('"+beschreibung+"', '"+datum+"', "+zeit+", "+selectedForCalculatingSlopeFaktor+", "+athlet_id+", "+strecke_id+")";
		stmt.executeUpdate(insertLeistung);
		ResultSet result_id = stmt.getGeneratedKeys();
		if (result_id.next()) {
			return result_id.getInt(1);
		}
		return -1;
	}
}
