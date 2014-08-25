package datenbank_zugriff;

import java.sql.SQLException;
import java.util.LinkedList;

import model.Leistung;
import datenbank_kommunikation.DBTableLeistung;

public class DBLeistung {

	DBTableLeistung tableLeistung;
	
	public DBLeistung() {
		tableLeistung = new DBTableLeistung();
	}
	
	public int neueLeistung(Leistung leistung) {
		String beschreibung = leistung.getBezeichnung();
		String datum = leistung.getDatum();
		double geschwindigkeit = leistung.getGeschwindigkeit();
		boolean selectedForSlopeFaktor = leistung.getSelectedForSlopeFaktor();
		int athlet_id = (int) leistung.getId_athlet();
		int strecke_id = leistung.getId_strecke();
		int strecke_laenge = leistung.getStrecke();
		try {
			return tableLeistung.einfuegen(beschreibung, datum, geschwindigkeit, selectedForSlopeFaktor, athlet_id, strecke_id, strecke_laenge);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	
	public void aendereLeistung(int leistung_id, Leistung leistung) {
		String beschreibung = leistung.getBezeichnung();
		String datum = leistung.getDatum();
		double geschwindigkeit = leistung.getGeschwindigkeit();
		boolean selectedForSlopeFaktor = leistung.getSelectedForSlopeFaktor();
		int athlet_id = (int) leistung.getId_athlet();
		int strecke_id = leistung.getId_strecke();
		int strecke_laenge = leistung.getStrecke();
		try {
			tableLeistung.aendernBeschreibung(leistung_id, beschreibung);
			tableLeistung.aendernDatum(leistung_id, datum);
			tableLeistung.aendernGeschwindigkeit(leistung_id, geschwindigkeit);
			tableLeistung.aendernSelectedForCalculatingSlopeFaktor(leistung_id, selectedForSlopeFaktor);
			tableLeistung.aendernAthletId(leistung_id, athlet_id);
			tableLeistung.aendernStreckeId(leistung_id, strecke_id);
			tableLeistung.aendernStreckeLaenge(strecke_id, strecke_laenge);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Leistung holeLeistung(int leistung_id) {
		Leistung leistung = null;
		try {
			leistung = tableLeistung.abrufen(leistung_id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return leistung;
	}
	
	public LinkedList<Leistung> holeAlleLeistungen(int athlet_id) {
		LinkedList<Leistung> alleLeistungen = new LinkedList<Leistung>();
		try {
			alleLeistungen = tableLeistung.alleAbrufen(athlet_id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return alleLeistungen;
	}
	
	public void loescheLeistung(int leistung_id) {
		try {
			tableLeistung.loeschen(leistung_id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
