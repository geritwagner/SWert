package datenbank;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.Test;

import model.Athlet;
import model.Leistung;
import model.Strecke;
import datenbank_kommunikation.DBVerbindung;
import datenbank_zugriff.*;

public class DBTest {

	@SuppressWarnings("unused")
	private DBVerbindung verbindung;
	
	public DBTest() {
		verbindung = new DBVerbindung();
	}
	
	@Test
	public void testDBVerbindung() {
		DBVerbindung verbindung = new DBVerbindung();	
		verbindung.verbindungBeenden();
	}
	
	@Test
	public void testDBStrecke() {
		DBStrecke dbStrecke = new DBStrecke();
		
		int laenge_1 = 50;
		String bezeichnung_1 = "50m";
		Strecke testStrecke = new Strecke(laenge_1);
		
		int strecke_1_id = dbStrecke.neueStrecke(testStrecke);
		testStrecke = new Strecke(strecke_1_id, bezeichnung_1, laenge_1);
		Strecke assertStrecke = dbStrecke.holeStrecke(strecke_1_id);
		assertEquals(testStrecke.getStrecken_id(), assertStrecke.getStrecken_id());
		assertEquals(testStrecke.getLaenge(), assertStrecke.getLaenge());
		assertEquals(testStrecke.getBezeichnung(), assertStrecke.getBezeichnung());
		
		int strecke_2_id = dbStrecke.neueStrecke(testStrecke);
		assertEquals(strecke_1_id, strecke_2_id);
		
		int laenge_2 = 51;
		String bezeichnung_2 = "51m";
		dbStrecke.aendereStrecke(strecke_1_id, laenge_2);
		testStrecke = new Strecke(strecke_1_id, bezeichnung_2, laenge_2);
		assertStrecke = dbStrecke.holeStrecke(strecke_1_id);
		assertEquals(testStrecke.getStrecken_id(), assertStrecke.getStrecken_id());
		assertEquals(testStrecke.getLaenge(), assertStrecke.getLaenge());
		assertEquals(testStrecke.getBezeichnung(), assertStrecke.getBezeichnung());
		
		dbStrecke.loescheStrecke(strecke_1_id);
		
	}
	
	@Test
	public void testDBAthlet() {
		DBAthlet dbAthlet = new DBAthlet();
		
		String name_1 = "Testname";
		LinkedList<Leistung> leistungen_1 = new LinkedList<Leistung>();
		
		Athlet testAthlet = new Athlet(name_1, leistungen_1);
		int athlet_id = dbAthlet.neuerAthlet(testAthlet);
		
		Athlet assertAthlet = dbAthlet.holeAthlet(athlet_id);
		assertEquals(testAthlet.getName(), assertAthlet.getName());
		assertEquals(testAthlet.getLeistungen(), assertAthlet.getLeistungen());
		
		String name_2 = "Anderer Testname";
		dbAthlet.aendereAthlet(athlet_id, name_2);
		testAthlet = new Athlet(name_2, leistungen_1);
		assertAthlet = dbAthlet.holeAthlet(athlet_id);
		assertEquals(testAthlet.getName(), assertAthlet.getName());
		assertEquals(testAthlet.getLeistungen(), assertAthlet.getLeistungen());
		
		dbAthlet.loescheAthlet(athlet_id);
	}
	
	@Test
	public void testDBLeistung() {
		DBLeistung dbLeistung = new DBLeistung();
		DBAthlet dbAthlet = new DBAthlet();
		
		String name_1 = "Testname";
		LinkedList<Leistung> leistungen_1 = new LinkedList<Leistung>();
		Athlet testAthlet = new Athlet(name_1, leistungen_1);
		int athlet_id = dbAthlet.neuerAthlet(testAthlet);
		
		int strecke_id = 1;
		String bezeichnung = "Testbezeichnung";
		String datum = "25.08.2014";
		double geschwindigkeit = 25;
		Leistung testLeistung = new Leistung(strecke_id, athlet_id, bezeichnung, datum, geschwindigkeit);
		int leistung_id = dbLeistung.neueLeistung(testLeistung);
		
		Leistung assertLeistung = dbLeistung.holeLeistung(leistung_id);
		assertEquals(testLeistung.getId_strecke(), assertLeistung.getId_strecke());
		assertEquals(testLeistung.getId_athlet(), assertLeistung.getId_athlet());
		assertEquals(testLeistung.getBezeichnung(), assertLeistung.getBezeichnung());
		assertEquals(testLeistung.getDatum(), assertLeistung.getDatum());
		assertEquals(testLeistung.getGeschwindigkeit(), assertLeistung.getGeschwindigkeit(),0);
		
		strecke_id = 2;
		bezeichnung = "Andere Testbezeichnung";
		datum = "27.7.2014";
		geschwindigkeit = 26;
		testLeistung = new Leistung(strecke_id, athlet_id, bezeichnung, datum, geschwindigkeit);
		dbLeistung.aendereLeistung(leistung_id, testLeistung);
		assertLeistung = dbLeistung.holeLeistung(leistung_id);
		assertEquals(testLeistung.getId_strecke(), assertLeistung.getId_strecke());
		assertEquals(testLeistung.getId_athlet(), assertLeistung.getId_athlet());
		assertEquals(testLeistung.getBezeichnung(), assertLeistung.getBezeichnung());
		assertEquals(testLeistung.getDatum(), assertLeistung.getDatum());
		assertEquals(testLeistung.getGeschwindigkeit(), assertLeistung.getGeschwindigkeit(),0);
		
		dbLeistung.loescheLeistung(leistung_id);
		dbAthlet.loescheAthlet(athlet_id);
		
		testAthlet.addLeistung(testLeistung);
		athlet_id = dbAthlet.neuerAthlet(testAthlet);
		testAthlet = dbAthlet.holeAthlet(athlet_id);
		for (Leistung assertLeistung2 : testAthlet.getLeistungen()) {
			assertEquals(testLeistung.getId_strecke(), assertLeistung2.getId_strecke());
			assertEquals(testLeistung.getId_athlet(), assertLeistung2.getId_athlet());
			assertEquals(testLeistung.getBezeichnung(), assertLeistung2.getBezeichnung());
			assertEquals(testLeistung.getDatum(), assertLeistung2.getDatum());
			assertEquals(testLeistung.getGeschwindigkeit(), assertLeistung2.getGeschwindigkeit(),0);			
		}
		
		dbAthlet.loescheAthlet(athlet_id);
	}
}
