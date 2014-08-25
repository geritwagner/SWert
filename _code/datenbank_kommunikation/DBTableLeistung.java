package datenbank_kommunikation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import model.Leistung;
import model.Strecke;
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
				+ "geschwindigkeit DOUBLE NOT NULL,"
				+ "selectedForCalculatingSlopeFaktor BOOLEAN,"
				+ "athlet_id INT,"
				+ "strecke_id INT,"
				+ "FOREIGN KEY(strecke_id) REFERENCES STRECKE(STRECKE_ID),"
				+ "FOREIGN KEY(athlet_id) REFERENCES ATHLET(ATHLET_ID))";
		stmt.executeUpdate(createLeistung);
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
		String updateLeistung = "UPDATE Leistung SET beschreibung='"+beschreibung+"' "
				+ "WHERE leistung_id="+leistung_id;
		stmt.executeUpdate(updateLeistung);
	}
	
	public void aendernDatum(int leistung_id, String datum) throws SQLException {
		String updateLeistung = "UPDATE Leistung SET datum='"+datum+"' "
				+ "WHERE leistung_id="+leistung_id;
		stmt.executeUpdate(updateLeistung);
	}
	
	public void aendernGeschwindigkeit(int leistung_id, double geschwindigkeit) throws SQLException {
		String updateLeistung = "UPDATE Leistung SET geschwindigkeit="+geschwindigkeit+" "
				+ "WHERE leistung_id="+leistung_id;
		stmt.executeUpdate(updateLeistung);
	}
	
	public void aendernSelectedForCalculatingSlopeFaktor(int leistung_id, boolean selectedForSlopeFaktor) throws SQLException {
		String updateLeistung = "UPDATE Leistung SET selectedForCalculatingSlopeFaktor="+selectedForSlopeFaktor+" "
				+ "WHERE leistung_id="+leistung_id;
		stmt.executeUpdate(updateLeistung);
	}
	
	public void aendernAthletId(int leistung_id, int athlet_id) throws SQLException {
		String updateLeistung = "UPDATE Leistung SET athlet_id="+athlet_id+" "
				+ "WHERE leistung_id="+leistung_id;
		stmt.executeUpdate(updateLeistung);
	}
	
	public void aendernStreckeId(int leistung_id, int strecke_id) throws SQLException {
		String updateLeistung = "UPDATE Leistung SET strecke_id="+strecke_id+" "
				+ "WHERE leistung_id="+leistung_id;
		stmt.executeUpdate(updateLeistung);
	}
	
	public void aendernStreckeLaenge(int strecke_id, int strecke_laenge) throws SQLException {
		DBStrecke dbStrecke = new DBStrecke();
		dbStrecke.aendereStrecke(strecke_id, strecke_laenge);
	}
	
	public Leistung abrufen(int leistung_id) throws SQLException {
		Leistung leistung = null;
		String selectLeistung = "SELECT * FROM Leistung WHERE leistung_id="+leistung_id+" LIMIT 1";
		ResultSet selectResult = stmt.executeQuery(selectLeistung);
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
		return leistung;
	}
	
	public LinkedList<Leistung> alleAbrufen(int athlet_id) throws SQLException {
		LinkedList<Leistung> alleLeistungen = new LinkedList<Leistung>();
		String selectLeistungen = "SELECT * FROM Leistung WHERE athlet_id="+athlet_id;
		ResultSet rs = stmt.executeQuery(selectLeistungen);
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
		return alleLeistungen;
	}
	
	public void loeschen(int leistung_id) throws SQLException{
		String loescheLeistung = "DELETE FROM Leistung WHERE leistung_id="+leistung_id;
		stmt.executeUpdate(loescheLeistung);
	}
	
	private int leistungEinfuegen (String beschreibung, String datum, double geschwindigkeit, boolean selectedForCalculatingSlopeFaktor, int athlet_id, int strecke_id) throws SQLException {
		String insertLeistung = "INSERT INTO Leistung(beschreibung, datum, geschwindigkeit, selectedForCalculatingSlopeFaktor, athlet_id, strecke_id)"
				+ "VALUES ('"+beschreibung+"', '"+datum+"', "+geschwindigkeit+", "+selectedForCalculatingSlopeFaktor+", "+athlet_id+", "+strecke_id+")";
		stmt.executeUpdate(insertLeistung);
		ResultSet result_id = stmt.getGeneratedKeys();
		if (result_id.next()) {
			return result_id.getInt(1);
		}
		return -1;
	}
}
