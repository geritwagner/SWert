package datenbank;

import static org.junit.Assert.*;

import org.junit.Test;

import model.Strecke;
import datenbank_kommunikation.DBVerbindung;
import datenbank_zugriff.DBStrecke;

public class DBTest {

	private DBStrecke dbStrecke;
	private Strecke testStrecke;
	private DBVerbindung verbindung;
		
	@Test
	public void testDBVerbindung() {
		DBVerbindung verbindung = new DBVerbindung();	
		verbindung.verbindungBeenden();
	}
	
	@Test
	public void testDBStrecke() {
		verbindung = new DBVerbindung();
		dbStrecke = new DBStrecke();
		
		int laenge_1 = 50;
		String bezeichnung_1 = "50m";
		
		int strecke_id = dbStrecke.neueStrecke(laenge_1);
		testStrecke = dbStrecke.holeStrecke(strecke_id);
		assertEquals(strecke_id, testStrecke.getStrecken_id());
		assertEquals(laenge_1, testStrecke.getLaenge());
		assertEquals(bezeichnung_1, testStrecke.getBezeichnung());
		
		int laenge_2 = 51;
		String bezeichnung_2 = "51m";
		dbStrecke.aendereStrecke(strecke_id, laenge_2);
		testStrecke = dbStrecke.holeStrecke(strecke_id);
		assertEquals(strecke_id, testStrecke.getStrecken_id());
		assertEquals(laenge_2, testStrecke.getLaenge());
		assertEquals(bezeichnung_2, testStrecke.getBezeichnung());
		
		verbindung.verbindungBeenden();
	}
	
	@Test
	public void testDBLeistung() {
		
	}
}
