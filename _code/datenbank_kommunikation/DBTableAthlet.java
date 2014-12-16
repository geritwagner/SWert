package datenbank_kommunikation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import datenbank_zugriff.DBLeistung;
import model.Athlet;
import model.Leistung;

public class DBTableAthlet implements DBTableInterface{

	private PreparedStatement stmt;
	private DBLeistung dbLeistung;
	
	public DBTableAthlet() {
		super();
		dbLeistung = new DBLeistung();
	}

	public void installiereSchemaVersion1() throws SQLException {
		String createAthlet = "CREATE TABLE IF NOT EXISTS Athlet "
				+ "(athlet_id INT PRIMARY KEY AUTO_INCREMENT(1,1) NOT NULL, "
				+ "name VARCHAR(255) NOT NULL,"
				+ "geoeffnet BOOLEAN DEFAULT 0)";
		stmt = DBVerbindung.getStatement(createAthlet);
		stmt.executeUpdate();
		
		String insertTestAthlets = "INSERT INTO Athlet (name)"
				+ "VALUES ('Dummy Athlet1'),"
				+ "('Dummy Athlet2')";
		stmt = DBVerbindung.getStatement(insertTestAthlets);
		stmt.executeUpdate();
	}
	
	public int einfuegen (String name) throws SQLException {
		String insertAthlet = "INSERT INTO athlet (name)"
				+ "VALUES (?)";
		stmt = DBVerbindung.getStatement(insertAthlet);
		stmt.setString(1, name);
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
	
	public void name_aendern (int athlet_id, String name) throws SQLException {
		String updateAthlet = "UPDATE Athlet SET name=? WHERE athlet_id=?";
		stmt = DBVerbindung.getStatement(updateAthlet);
		stmt.setString(1, name);
		stmt.setInt(2, athlet_id);
		stmt.executeUpdate();
	}
	
	public void status_aendern (int athlet_id, boolean status) throws SQLException {
		String updateAthlet = "UPDATE Athlet SET geoeffnet=? WHERE athlet_id=?";
		stmt = DBVerbindung.getStatement(updateAthlet);
		stmt.setBoolean(1, status);
		stmt.setInt(2, athlet_id);
		stmt.executeUpdate();
	}
	
	public Athlet abrufen (int athlet_id) throws SQLException {
		Athlet athlet = null;
		String abrufenAthlet = "SELECT * FROM Athlet WHERE athlet_id=? LIMIT 1";
		stmt = DBVerbindung.getStatement(abrufenAthlet);
		stmt.setInt(1, athlet_id);
		ResultSet rs = stmt.executeQuery();
		if (rs.next()) {
			long id = rs.getInt(1);
			String name = rs.getString(2);
			boolean geoeffnet = rs.getBoolean(3);
			LinkedList<Leistung> alleLeistungen = null;
			alleLeistungen = dbLeistung.holeAlleLeistungen(athlet_id);
			athlet = new Athlet(id, name, alleLeistungen);
			athlet.setGeoeffnet(geoeffnet);
		}
		rs.close();
		return athlet;
	}
	
	public LinkedList<Athlet> alleAbrufen () throws SQLException {
		LinkedList<Athlet> alleAthleten = new LinkedList<Athlet>();
		String abrufenAthleten = "SELECT * FROM Athlet ORDER BY athlet_id";
		stmt = DBVerbindung.getStatement(abrufenAthleten);
		ResultSet athleten_rs = stmt.executeQuery();
		while (athleten_rs.next()) {
			long id = athleten_rs.getInt(1);
			String name = athleten_rs.getString(2);
			boolean geoeffnet = athleten_rs.getBoolean(3);
			LinkedList<Leistung> alleLeistungen = dbLeistung.holeAlleLeistungen((int) (id));
			Athlet athlet = new Athlet(id, name, alleLeistungen);
			athlet.setGeoeffnet(geoeffnet);
			alleAthleten.add(athlet);
		}
		athleten_rs.close();
		return alleAthleten;
	}
	
	public void loeschen (int athlet_id) throws SQLException {
		LinkedList<Leistung> alleLeistungen = dbLeistung.holeAlleLeistungen(athlet_id);
		for (Leistung leistung : alleLeistungen) {
			dbLeistung.loescheLeistung((int) leistung.getId()); 
		}
		String deleteAthlet = "DELETE FROM Athlet WHERE athlet_id=?";
		stmt = DBVerbindung.getStatement(deleteAthlet);
		stmt.setInt(1, athlet_id);
		stmt.executeUpdate();
	}
	
	public void freeTable() throws SQLException {
		if (stmt != null) {
			stmt.close();			
		}
	}
}
