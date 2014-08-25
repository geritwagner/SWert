package datenbank_zugriff;

import java.sql.SQLException;

import model.Strecke;
import datenbank_kommunikation.DBTableStrecke;

public class DBStrecke {

	private DBTableStrecke tableStrecke;
	
	public DBStrecke () {
		tableStrecke = new DBTableStrecke();
	}
	
	public int neueStrecke (Strecke strecke) {
		int laenge = strecke.getLaenge();
		String bezeichnung = strecke.getBezeichnung();
		try {
			return tableStrecke.einfuegen(bezeichnung, laenge);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return 0;
	}
	
	public void aendereStrecke (int strecke_id, int neue_laenge) {
		try {
			String bezeichnung = neue_laenge+"m";
			tableStrecke.aendernLaenge(strecke_id, neue_laenge);
			tableStrecke.aendernBezeichnung(strecke_id, bezeichnung);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Strecke holeStrecke (int strecke_id) {
		Strecke strecke = null;
		try {
			strecke = tableStrecke.abrufen(strecke_id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return strecke;
	}
	
	public void loescheStrecke (int strecke_id) {
		try {
			tableStrecke.loeschen(strecke_id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
