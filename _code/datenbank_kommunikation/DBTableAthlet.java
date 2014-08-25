package datenbank_kommunikation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import datenbank_zugriff.DBLeistung;
import model.Athlet;
import model.Leistung;

public class DBTableAthlet extends DBTableAbstract{

	private DBLeistung dbLeistung;
	
	public DBTableAthlet() {
		super();
		dbLeistung = new DBLeistung();
	}

	public void installiereSchemaVersion1() throws SQLException {
		String createAthlet = "CREATE TABLE IF NOT EXISTS Athlet "
				+ "(athlet_id INT PRIMARY KEY AUTO_INCREMENT(1,1) NOT NULL, "
				+ "name VARCHAR(255) NOT NULL)";
		stmt.executeUpdate(createAthlet);
	}
	
	public int einfuegen (String name) throws SQLException {
		String insertAthlet = "INSERT INTO athlet (name)"
				+ "VALUES ('"+name+"')";
		stmt.executeUpdate(insertAthlet);
		ResultSet result_id = stmt.getGeneratedKeys();
		if (result_id.next()) {
			return result_id.getInt(1);
		}
		return -1;
	}
	
	public void aendern (int athlet_id, String name) throws SQLException {
		String updateAthlet = "UPDATE Athlet SET name='"+name+"' "
				+ "WHERE athlet_id="+athlet_id;
		stmt.executeUpdate(updateAthlet);
	}
	
	public Athlet abrufen (int athlet_id) throws SQLException {
		Athlet athlet = null;
		String abrufenAthlet = "SELECT * FROM Athlet WHERE athlet_id="+athlet_id+" LIMIT 1";
		ResultSet rs = stmt.executeQuery(abrufenAthlet);
		if (rs.next()) {
			long id = rs.getInt(1);
			String name = rs.getString(2);
			LinkedList<Leistung> alleLeistungen = null;
			alleLeistungen = dbLeistung.holeAlleLeistungen(athlet_id);
			athlet = new Athlet(id, name, alleLeistungen);
		}
		return athlet;
	}
	
	public void loeschen (int athlet_id) throws SQLException {
		LinkedList<Leistung> alleLeistungen = dbLeistung.holeAlleLeistungen(athlet_id);
		for (Leistung leistung : alleLeistungen) {
			dbLeistung.loescheLeistung((int) leistung.getId()); 
		}
		String deleteAthlet = "DELETE FROM Athlet WHERE athlet_id="+athlet_id;
		stmt.executeUpdate(deleteAthlet);
	}
}
