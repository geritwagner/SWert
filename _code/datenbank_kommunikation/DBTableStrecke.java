package datenbank_kommunikation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Strecke;

public class DBTableStrecke implements DBTableInterface{

	private PreparedStatement stmt;
	
	public DBTableStrecke() {
		super();
	}

	public void installiereSchemaVersion1() throws SQLException {
		String createStrecke = "CREATE TABLE IF NOT EXISTS Strecke "
				+ "(strecke_id INT PRIMARY KEY AUTO_INCREMENT(1,1) NOT NULL, "
				+ "bezeichnung VARCHAR(255) NOT NULL,"
				+ "laenge INT NOT NULL)";
		stmt = DBVerbindung.getStatement(createStrecke);
		stmt.executeUpdate();
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
		stmt = DBVerbindung.getStatement(insertStrecke);
		stmt.executeUpdate();
	}

	public int einfuegen (String bezeichnung, int laenge) throws SQLException {
		String checkStreckenlaenge = "SELECT strecke_id FROM STRECKE WHERE laenge=?";
		stmt = DBVerbindung.getStatement(checkStreckenlaenge);
		stmt.setInt(1, laenge);
		ResultSet checkResult = stmt.executeQuery();
		if(checkResult.next()) {
			int id = checkResult.getInt(1);
			checkResult.close();
			return id;
		}
		checkResult.close();
		String insertStrecke = "INSERT INTO Strecke (bezeichnung, laenge) "
				+ "VALUES (?, ?)";
		stmt = DBVerbindung.getStatement(insertStrecke);
		stmt.setString(1, bezeichnung);
		stmt.setInt(2, laenge);
		stmt.executeUpdate();
		ResultSet result_id = stmt.getGeneratedKeys();
		if (result_id.next()) {
			int id = result_id.getInt(1);
			result_id.close();
			return id;
		}
		result_id.close();
		return -1;
	}
	
	public void aendernLaenge (int strecke_id, int laenge) throws SQLException {
		String updateStrecke = "UPDATE Strecke SET laenge=? WHERE strecke_id=?";
		stmt = DBVerbindung.getStatement(updateStrecke);
		stmt.setInt(1, laenge);
		stmt.setInt(2, strecke_id);
		stmt.executeUpdate();
	}
	
	public void aendernBezeichnung (int strecke_id, String bezeichnung) throws SQLException {
		String updateStrecke = "UPDATE Strecke SET bezeichnung=? WHERE strecke_id=?";
		stmt = DBVerbindung.getStatement(updateStrecke);
		stmt.setString(1, bezeichnung);
		stmt.setInt(2, strecke_id);
		stmt.executeUpdate();
	}

	public Strecke abrufen (int strecke_id) throws SQLException {	
		Strecke strecke = null;
		String selectStrecke = "SELECT * FROM Strecke WHERE strecke_id=? LIMIT 1";
		stmt = DBVerbindung.getStatement(selectStrecke);
		stmt.setInt(1, strecke_id);
		ResultSet result = stmt.executeQuery();
		if(result.next()) {
			strecke_id = result.getInt(1);
			String bezeichnung = result.getString(2);
			int laenge = result.getInt(3);
			strecke = new Strecke(strecke_id, bezeichnung, laenge);
		}
		return strecke;
	}

	public void loeschen (int strecke_id) throws SQLException {
		String deleteStrecke = "DELETE FROM Strecke WHERE strecke_id=?";
		stmt = DBVerbindung.getStatement(deleteStrecke);
		stmt.setInt(1, strecke_id);
		stmt.executeUpdate();
	}
	
	public void freeTable() throws SQLException {
		if (stmt != null) {
			stmt.close();			
		}
	}
}
