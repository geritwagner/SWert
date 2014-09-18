package datenbank_zugriff;

import java.sql.SQLException;
import java.util.LinkedList;

import model.Athlet;
import model.Leistung;
import datenbank_kommunikation.DBTableAthlet;

public class DBAthlet {
	
	DBTableAthlet tableAthlet;
	DBLeistung dbLeistung;

	public DBAthlet() {
		tableAthlet = new DBTableAthlet();
		dbLeistung = new DBLeistung();
	}
	
	public int neuerAthlet(Athlet athlet) {
		String name = athlet.getName();
		int athlet_id = -1;
		try {
			athlet_id = tableAthlet.einfuegen(name);
			LinkedList<Leistung> alleLeistungen = athlet.getLeistungen();
			for(Leistung leistung : alleLeistungen) {
				leistung.setAthletID(athlet_id);
				dbLeistung.neueLeistung(leistung);
			}
			return athlet_id;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return athlet_id;
	}
	
	public void aendereAthlet(int athlet_id, String name) {
		try {
			tableAthlet.aendern(athlet_id, name);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Athlet holeAthlet(int athlet_id) {
		Athlet athlet = null;
		try {
			athlet = tableAthlet.abrufen(athlet_id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return athlet;
	}
	
	public LinkedList<Athlet> holeAlleAthleten() {
		LinkedList<Athlet> alleAthleten = new LinkedList<Athlet>();
		try {
			alleAthleten = tableAthlet.alleAbrufen();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return alleAthleten;
	}
	
	public void loescheAthlet(int athlet_id) {
		try {
			tableAthlet.loeschen(athlet_id);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
