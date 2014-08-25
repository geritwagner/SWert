package datenbank_kommunikation;

import java.sql.ResultSet;
import java.sql.SQLException;
import model.Strecke;

public class DBTableStrecke extends DBTableAbstract{

	public DBTableStrecke() {
		super();
	}

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

	public int einfuegen (String bezeichnung, int laenge) throws SQLException {
		String checkStreckenlaenge = "SELECT strecke_id FROM STRECKE WHERE laenge="+laenge;
		ResultSet checkResult = stmt.executeQuery(checkStreckenlaenge);
		if(checkResult.next()) {
			return checkResult.getInt(1);
		}
		String insertStrecke = "INSERT INTO Strecke (bezeichnung, laenge) "
				+ "VALUES ('"+bezeichnung+"', "+laenge+")";
		stmt.executeUpdate(insertStrecke);
		ResultSet result_id = stmt.getGeneratedKeys();
		if (result_id.next()) {
			return result_id.getInt(1);
		}
		return -1;
	}
	
	public void aendernLaenge (int strecke_id, int laenge) throws SQLException {
		String updateStrecke = "UPDATE Strecke SET laenge="+laenge+" "
				+ "WHERE strecke_id="+strecke_id;
		stmt.executeUpdate(updateStrecke);
	}
	
	public void aendernBezeichnung (int strecke_id, String bezeichnung) throws SQLException {
		String updateStrecke = "UPDATE Strecke SET bezeichnung='"+bezeichnung+"' "
				+ "WHERE strecke_id="+strecke_id;
		stmt.executeUpdate(updateStrecke);
	}

	public Strecke abrufen (int strecke_id) throws SQLException {	
		Strecke strecke = null;
		String selectStrecke = "SELECT * FROM Strecke WHERE strecke_id="+strecke_id+" LIMIT 1";
		ResultSet result = stmt.executeQuery(selectStrecke);
		if(result.next()) {
			strecke_id = result.getInt(1);
			String bezeichnung = result.getString(2);
			int laenge = result.getInt(3);
			strecke = new Strecke(strecke_id, bezeichnung, laenge);
		}
		return strecke;
	}

	public void loeschen (int strecke_id) throws SQLException {
		String deleteStrecke = "DELETE FROM Strecke WHERE strecke_id="+strecke_id;
		stmt.executeUpdate(deleteStrecke);
	}
}
