package datenbank_kommunikation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import model.Leistung;
import model.Strecke;
import datenbank_zugriff.DBStrecke;

public class DBTableLeistung implements DBTableInterface {

	private DBStrecke dbStrecke;
	private PreparedStatement stmt;
	
	public DBTableLeistung() {
		super();
		dbStrecke = new DBStrecke();
	}

	public void installiereSchemaVersion1() throws SQLException {
		String createLeistung = "CREATE TABLE IF NOT EXISTS Leistung "
				+ "(leistung_id INT PRIMARY KEY AUTO_INCREMENT(1,1) NOT NULL, "
				+ "beschreibung VARCHAR(255) NOT NULL,"
				+ "datum VARCHAR(100) NOT NULL,"
				+ "geschwindigkeit DOUBLE NOT NULL,"
				+ "selectedForCalculatingSlopeFaktor BOOLEAN,"
				+ "athlet_id INT,"
				+ "strecke_id INT,"
				+ "FOREIGN KEY(strecke_id) REFERENCES STRECKE(STRECKE_ID),"
				+ "FOREIGN KEY(athlet_id) REFERENCES ATHLET(ATHLET_ID))";
		stmt = DBVerbindung.getStatement(createLeistung);
		stmt.executeUpdate();
	}

	public int einfuegen (String beschreibung, String datum, double geschwindigkeit, boolean selectedForCalculatingSlopeFaktor, int athlet_id,	int strecke_id,	int strecke_laenge) throws SQLException {
		int leistung_id = 0;
		if (strecke_id == -1) {
			Strecke strecke = new Strecke(strecke_laenge);
			strecke_id = dbStrecke.neueStrecke(strecke);			
		}
		leistung_id = leistungEinfuegen(beschreibung, datum, geschwindigkeit, selectedForCalculatingSlopeFaktor, athlet_id, strecke_id);
		return leistung_id;
	}
	
	public void aendernBeschreibung(int leistung_id, String beschreibung) throws SQLException {
		String updateLeistung = "UPDATE Leistung SET beschreibung=? WHERE leistung_id=?";
		stmt = DBVerbindung.getStatement(updateLeistung);
		stmt.setString(1, beschreibung);
		stmt.setInt(2, leistung_id);
		stmt.executeUpdate();
	}
	
	public void aendernDatum(int leistung_id, String datum) throws SQLException {
		String updateLeistung = "UPDATE Leistung SET datum=? WHERE leistung_id=?";
		stmt = DBVerbindung.getStatement(updateLeistung);
		stmt.setString(1, datum);
		stmt.setInt(2, leistung_id);
		stmt.executeUpdate();
	}
	
	public void aendernGeschwindigkeit(int leistung_id, double geschwindigkeit) throws SQLException {
		String updateLeistung = "UPDATE Leistung SET geschwindigkeit=? WHERE leistung_id=?";
		stmt = DBVerbindung.getStatement(updateLeistung);
		stmt.setDouble(1, geschwindigkeit);
		stmt.setInt(2, leistung_id);
		stmt.executeUpdate();
	}
	
	public void aendernSelectedForCalculatingSlopeFaktor(int leistung_id, boolean selectedForSlopeFaktor) throws SQLException {
		String updateLeistung = "UPDATE Leistung SET selectedForCalculatingSlopeFaktor=? WHERE leistung_id=?";
		stmt = DBVerbindung.getStatement(updateLeistung);
		stmt.setBoolean(1, selectedForSlopeFaktor);
		stmt.setInt(2, leistung_id);
		stmt.executeUpdate();
	}
	
	public void aendernAthletId(int leistung_id, int athlet_id) throws SQLException {
		String updateLeistung = "UPDATE Leistung SET athlet_id=? WHERE leistung_id=?";
		stmt = DBVerbindung.getStatement(updateLeistung);
		stmt.setInt(1, athlet_id);
		stmt.setInt(2, leistung_id);
		stmt.executeUpdate();
	}
	
	public void aendernStreckeId(int leistung_id, int strecke_id) throws SQLException {
		String updateLeistung = "UPDATE Leistung SET strecke_id=? WHERE leistung_id=?";
		stmt = DBVerbindung.getStatement(updateLeistung);
		stmt.setInt(1, strecke_id);
		stmt.setInt(2, leistung_id);
		stmt.executeUpdate();
	}
	
	public void aendernStreckeLaenge(int strecke_id, int strecke_laenge) throws SQLException {
		DBStrecke dbStrecke = new DBStrecke();
		dbStrecke.aendereStrecke(strecke_id, strecke_laenge);
	}
	
	public Leistung abrufen(int leistung_id) throws SQLException {
		Leistung leistung = null;
		String selectLeistung = "SELECT * FROM Leistung WHERE leistung_id=? LIMIT 1";
		stmt = DBVerbindung.getStatement(selectLeistung);
		stmt.setInt(1, leistung_id);
		ResultSet selectResult = stmt.executeQuery();
		while(selectResult.next()) {
			String bezeichnung = selectResult.getString(2);
			String datum = selectResult.getString(3);
			double geschwindigkeit = selectResult.getDouble(4);
			boolean selectedForSlopeFaktor = selectResult.getBoolean(5);
			int athlet_id = selectResult.getInt(6);
			int strecke_id = selectResult.getInt(7);
			long longAthlet_id = (long) athlet_id;
			leistung = new Leistung(strecke_id, longAthlet_id, bezeichnung, datum, geschwindigkeit);
			leistung.setIsUsedForSlopeFaktor(selectedForSlopeFaktor);
		}
		selectResult.close();
		return leistung;
	}
	
	public LinkedList<Leistung> alleAbrufen(int athlet_id) throws SQLException {
		LinkedList<Leistung> alleLeistungen = new LinkedList<>();
		String selectLeistungen = "SELECT * FROM Leistung WHERE athlet_id=?";
		stmt = DBVerbindung.getStatement(selectLeistungen);
		stmt.setInt(1, athlet_id);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			long id = rs.getInt(1); 
			String bezeichnung = rs.getString(2);
			String datum = rs.getString(3);
			double geschwindigkeit = rs.getDouble(4);
			boolean selectedForSlopeFaktor = rs.getBoolean(5);
			int strecke_id = rs.getInt(7);
			long longAthlet_id = (long) athlet_id;
			Leistung leistung = new Leistung(strecke_id, longAthlet_id, bezeichnung, datum, geschwindigkeit);
			leistung.setLeistungID(id);
			leistung.setIsUsedForSlopeFaktor(selectedForSlopeFaktor);
			alleLeistungen.add(leistung);
		}
		rs.close();
		return alleLeistungen;
	}
	
	public void loeschen(int leistung_id) throws SQLException{
		String loescheLeistung = "DELETE FROM Leistung WHERE leistung_id=?";
		stmt = DBVerbindung.getStatement(loescheLeistung);
		stmt.setInt(1, leistung_id);
		stmt.executeUpdate();
	}
	
	public void freeTable() throws SQLException {
		if (stmt != null) {
			stmt.close();			
		}
	}
	
	private int leistungEinfuegen (String beschreibung, String datum, double geschwindigkeit, boolean selectedForCalculatingSlopeFaktor, int athlet_id, int strecke_id) throws SQLException {
		String insertLeistung = "INSERT INTO Leistung(beschreibung, datum, geschwindigkeit, selectedForCalculatingSlopeFaktor, athlet_id, strecke_id)"
				+ "VALUES (?, ?, ?, ?, ?, ?)";
		stmt = DBVerbindung.getStatement(insertLeistung);
		stmt.setString(1, beschreibung);
		stmt.setString(2, datum);
		stmt.setDouble(3, geschwindigkeit);
		stmt.setBoolean(4, selectedForCalculatingSlopeFaktor);
		stmt.setInt(5, athlet_id);
		stmt.setInt(6, strecke_id);
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
	
}
