package model;

import static org.junit.Assert.*;
import java.util.*;

import org.junit.*;

/**
 * @author Honors-WInfo-Projekt (Fabian B�hm, Alexander Puchta), Gerit Wagner
 */

public class AthletTest {

	// ECLEMMA: Bug expectedException - http://www.eclemma.org/faq.html#trouble05
	
	private Athlet testAthlet;
	
	@Test
	public void testConstructorAndDataMethods(){
		
		//	public long getId();
		//	public String getName();
		
		testAthlet = new Athlet ("Athlet ohne ID (wird erzeugt)", null);
		assertEquals("Athlet ohne ID (wird erzeugt)", testAthlet.getName());
		
		testAthlet = new Athlet(12, "Tester", null);
		assertEquals(testAthlet.getId(), 12);
		testAthlet = null;
		testAthlet = new Athlet ("Tester", null);
		long naechsteId = testAthlet.getId() + 1;
		Athlet neuerAthlet = new Athlet ("neuer", null);
		assertEquals(naechsteId, neuerAthlet.getId());
		assertEquals("Tester", testAthlet.getName());
		assertEquals(0, testAthlet.getLeistungen().size());

		//	public boolean addLeistung(Leistung leistung);
		//	public LinkedList<Leistung> getLeistungen();
		//	public Leistung getLeistungById(long id);

		// 2.000m Leistung: 6:00 min
		Leistung testLeistung = new Leistung(4, 12, "Test Wettkampf", "01-01-2014", 180);
		testAthlet.addLeistung(testLeistung);
		assertEquals(testLeistung, testAthlet.getLeistungen().getFirst());
		assertEquals(360, testAthlet.getLeistungen().get(0).getZeit(), 0.1);
		long id = testLeistung.getId();
		assertTrue(testAthlet.getLeistungById(id).equals(testLeistung));
		assertEquals(null, testAthlet.getLeistungById(id+1));

		//	public void updateLeistung(long id_leistung, int id_strecke, String bezeichnung, String datum, double geschwindigkeit);
		
		testAthlet.updateLeistung(testLeistung.getId(), 4, "updated", "01-01-2014", 180);
		assertEquals("updated", testLeistung.getBezeichnung());

		//	public void setSpeicherpfad(String speicherpfad);
		//	public String getSpeicherpfad();

		assertEquals(false, testAthlet.isSetSpeicherpfad());
		testAthlet.setSpeicherpfad("C://Ordner/Datei.csv");
		assertEquals("C://Ordner/Datei.csv", testAthlet.getSpeicherpfad());
		assertEquals(true, testAthlet.isSetSpeicherpfad());

		//	public boolean equalsWithoutID (Athlet andererAthlet);
		Leistung leistung = new Leistung(4, 20, "updated", "01-01-2014", 180);
		Athlet gleicherGeoeffneterAthlet = new Athlet(20, "andererName", null);
		assertFalse(testAthlet.equalsWithoutID(gleicherGeoeffneterAthlet));
		gleicherGeoeffneterAthlet = new Athlet(20, "Tester", null);
		assertFalse(testAthlet.equalsWithoutID(gleicherGeoeffneterAthlet));
		gleicherGeoeffneterAthlet.addLeistung(leistung);
		assertTrue(testAthlet.equalsWithoutID(gleicherGeoeffneterAthlet));
		gleicherGeoeffneterAthlet.removeLeistung(leistung);
		leistung = new Leistung(4, 20, "updated", "01-01-2014", 500);
		gleicherGeoeffneterAthlet.addLeistung(leistung);
		assertFalse(testAthlet.equalsWithoutID(gleicherGeoeffneterAthlet));

		//	public boolean removeLeistung(Leistung leistungToRemove);
		testAthlet.removeLeistung(testLeistung);
		testAthlet.removeLeistung(testLeistung);
		assertEquals(0, testAthlet.getLeistungen().size());
	}
	
	@Test
	public void testSlopeFaktorLogik() throws Exception {
		
		Leistung leistung1 = new Leistung(1, 12, "800m-Leistung (langsam)", "01-01-2014", 183.125);
		Leistung leistung2 = new Leistung(7, 12, "10.000m-Leistung (langsam)", "01-01-2014", 300);
		LinkedList<Leistung> leistungen = new LinkedList<>();
		leistungen.add(leistung1);
		leistungen.add(leistung2);
		
		
		// Leistungen f�r Slope-Faktor bearbeiten
		// public void setLeistungToAuswahlForSlopeFaktor(Leistung ausgewaehlteLeistung) throws Exception;
		// public void removeLeistungFromAuswahlForSlopeFaktor(Leistung ausgewaehlteLeistung);
		// public Leistung[] getLeistungAuswahlForSlopeFaktor();
		// public void resetLeistungAuswahlForSlopeFaktor();

		
		// public boolean isSetSlopeFaktor();
		// public String getSlopeFaktorStatus();
		
		// TODO: testen, ob ein "zu guter" Slope-Faktor erkannt & nicht verwendet wird.

		// public void setLeistungenAuswahlForSlopeFaktorAutomatisch() throws ThreeLeistungenForSlopeFaktorException, GleicheStreckeException;		
		testAthlet = new Athlet("Tester", leistungen);
		testAthlet.setLeistungenAuswahlForSlopeFaktorAutomatisch();
//		testAthlet.getAnaerobeSchwelle();
		testAthlet.resetLeistungAuswahlForSlopeFaktor();
		
		
		testAthlet.setLeistungToAuswahlForSlopeFaktor(leistung1);
		testAthlet.setLeistungToAuswahlForSlopeFaktor(leistung2);
		assertEquals(leistung1, testAthlet.getLeistungAuswahlForSlopeFaktor()[0]);
		assertEquals(leistung2, testAthlet.getLeistungAuswahlForSlopeFaktor()[1]);
		testAthlet.removeLeistung(leistung2);
		assertEquals(null, testAthlet.getLeistungAuswahlForSlopeFaktor()[1]);
		
		leistung2 = new Leistung(7, 12, "10.000m-Leistung (langsam)", "01-01-2014", 261.53);
		testAthlet.addLeistung(leistung2);
		testAthlet.setLeistungToAuswahlForSlopeFaktor(leistung2);
		
		Leistung nichtEnthalteneLeistung = new Leistung(7, 132, "nicht in der Leistungs-Liste enthalten", "01-01-2014", 143.65);
		testAthlet.removeLeistungFromAuswahlForSlopeFaktor(nichtEnthalteneLeistung);
		testAthlet.setLeistungToAuswahlForSlopeFaktor(nichtEnthalteneLeistung);
		
		// Werden die Leistungen getauscht, wenn sie nicht nach aufsteigender Streckenl�nge hinzugef�gt werden?
		leistung1 = new Leistung(7, 12, "10.000m-Leistung (langsam)", "01-01-2014", 261.53);
		leistung2 = new Leistung(1, 12, "800m-Leistung (langsam)", "01-01-2014", 183.125);
		Leistung leistung3 = new Leistung(0, 12, "400m-Leistung (langsam)", "01-01-2014", 140);
		Leistung leistung4 = new Leistung(8, 12, "15km-Leistung (langsam)", "01-01-2014", 360);
		leistungen = new LinkedList<>();
		leistungen.add(leistung1);
		leistungen.add(leistung2);
		leistungen.add(leistung3);
		leistungen.add(leistung4);

		testAthlet = new Athlet(12, "Tester", leistungen);
		testAthlet.resetLeistungAuswahlForSlopeFaktor();
		testAthlet.setLeistungToAuswahlForSlopeFaktor(leistung1);
		testAthlet.setLeistungToAuswahlForSlopeFaktor(leistung2);
		testAthlet.resetLeistungAuswahlForSlopeFaktor();
		testAthlet.setLeistungenAuswahlForSlopeFaktorAutomatisch();
		
		testAthlet.removeLeistung(leistung3);
		testAthlet.removeLeistung(leistung4);
		
		// Es sollten keine Leistungen �ber die gleiche Strecke als Slope-Faktor gesetzt werden k�nnen
		
		leistung1 = new Leistung(7, 12, "10.000m-Leistung (langsam)", "01-01-2014",183.2);
		leistung2 = new Leistung(1, 12, "800m-Leistung (langsam)", "01-01-2014", 183.125);
		testAthlet = new Athlet(12, "Tester", null);
		// zu gute Slope-Faktoren sollten nicht akzeptiert werden
		testAthlet.addLeistung(leistung1);
		testAthlet.setLeistungToAuswahlForSlopeFaktor(leistung1);
		testAthlet.addLeistung(leistung2);
		testAthlet.setLeistungToAuswahlForSlopeFaktor(leistung2);

		// zu schlechte Slope-Faktoren sollten nicht akzeptiert werden
		testAthlet.removeLeistung(leistung1);
		leistung1 = new Leistung(7, 12, "10.000m-Leistung (langsam)", "01-01-2014", 18843.2);
		testAthlet.addLeistung(leistung1);
		testAthlet.setLeistungToAuswahlForSlopeFaktor(leistung1);
	}
	
	@Test
	public void testCalculations() throws Exception {
		
//		// Berechnete Leistungen
//		public LinkedList<Leistung> getMoeglicheBestzeitenListe () throws SlopeFaktorNotSetException;
//		public double calculateSpeedSecondsPerKm (double entfernung) throws SlopeFaktorNotSetException;
//		public double calculateTime (double entfernung) throws SlopeFaktorNotSetException;
//		public double getAnaerobeSchwelle() throws SlopeFaktorNotSetException;	
		
		testAthlet = new Athlet(12, "Tester", null);
				
		boolean exceptionThrown = false;
		try{
			testAthlet.getMoeglicheBestzeitenListe();
		} catch (Exception e){
			exceptionThrown = true;
		}
		assertTrue(exceptionThrown);
		
		// Auswahl einer 3. Leistung f�r die Berechnung des Slope-Faktors sollte nicht m�glich sein.
		Leistung leistung1 = new Leistung(7, 12, "10.000m-Leistung", "01-01-2014", 261.53);
		Leistung leistung2 = new Leistung(1, 12, "800m-Leistung", "01-01-2014", 183.125);
		Leistung leistung3 = new Leistung (6, 12, "5.000m-Leistung", "11-01-2014", 263);
		testAthlet.addLeistung(leistung1);
		testAthlet.addLeistung(leistung2);
		testAthlet.addLeistung(leistung3);
		testAthlet.setLeistungToAuswahlForSlopeFaktor(leistung1);
		testAthlet.setLeistungToAuswahlForSlopeFaktor(leistung2);
		
		exceptionThrown = false;
		try{
			testAthlet.setLeistungToAuswahlForSlopeFaktor(leistung3);
		} catch (Exception e){
			exceptionThrown = true;
		}
		assertTrue(exceptionThrown);

		testAthlet.removeLeistung(leistung1);
		testAthlet.removeLeistung(leistung2);
		testAthlet.removeLeistung(leistung3);

		leistung1 = new Leistung(7, 12, "10.000m-Leistung", "01-01-2014", 261.53);
		leistung2 = new Leistung(7, 12, "andere 10.000m-Leistung", "01-01-2014", 281.53);
		testAthlet.addLeistung(leistung1);
		testAthlet.addLeistung(leistung2);
		testAthlet.addLeistung(leistung3);
		testAthlet.setLeistungToAuswahlForSlopeFaktor(leistung1);
		
		// es d�rfen keine Leistungen �ber die gleiche Strecke f�r die Berechnung des Slope-Faktors ausgew�hlt werden
		exceptionThrown = false;
		try{
			testAthlet.setLeistungToAuswahlForSlopeFaktor(leistung2);
		} catch (Exception e){
			exceptionThrown = true;
		}
		assertTrue(exceptionThrown);
		
		testAthlet.removeLeistung(leistung1);
		testAthlet.removeLeistung(leistung2);
		testAthlet.removeLeistung(leistung3);

		Leistung leistung1Langsam = new Leistung(1, 12, "800m-Leistung (langsam)", "01-01-2014", 183.125);
		Leistung leistung2Langsam = new Leistung(7, 12, "10.00m-Leistung (langsam)", "01-01-2014", 261.53);
		double schwelleLangsam = 270.4;
		
		Leistung leistung1Mittel = new Leistung(1, 12, "800m-Leistung (mittel)", "01-01-2014", 165.25);
		Leistung leistung2Mittel= new Leistung(7, 12, "10.00m-Leistung (mittel)", "01-01-2014", 218.64);
		double schwelleMittel = 228.3;
		
		Leistung leistung1Schnell = new Leistung(1, 12, "800m-Leistung (schnell)", "01-01-2014", 137);
		Leistung leistung2Schnell= new Leistung(7, 12, "10.00m-Leistung (schnell)", "01-01-2014", 171.47);
		double schwelleSchnell = 180.9;
		
		// bestzeiten und calculateSpeed testen:

		// test slow profile
		testAthlet.addLeistung(leistung1Langsam);
		testAthlet.addLeistung(leistung2Langsam);
		testAthlet.setLeistungToAuswahlForSlopeFaktor(leistung1Langsam);
		testAthlet.setLeistungToAuswahlForSlopeFaktor(leistung2Langsam);
		assertEquals(schwelleLangsam, testAthlet.getAnaerobeSchwelle(), 0.1);

		// TODO: test bestzeit und calculateSpeed/Time/etc.
		testAthlet.removeLeistung(leistung1Langsam);
		testAthlet.removeLeistung(leistung2Langsam);
		
		// test normal profile
		testAthlet.addLeistung(leistung1Mittel);
		testAthlet.addLeistung(leistung2Mittel);
		testAthlet.setLeistungToAuswahlForSlopeFaktor(leistung1Mittel);
		testAthlet.setLeistungToAuswahlForSlopeFaktor(leistung2Mittel);
		assertEquals(schwelleMittel, testAthlet.getAnaerobeSchwelle(), 0.1);

		// TODO: test bestzeit und calculateSpeed
		testAthlet.removeLeistung(leistung1Mittel);
		testAthlet.removeLeistung(leistung2Mittel);
		
		// test fast profile
		testAthlet.addLeistung(leistung1Schnell);
		testAthlet.addLeistung(leistung2Schnell);
		testAthlet.setLeistungToAuswahlForSlopeFaktor(leistung1Schnell);
		testAthlet.setLeistungToAuswahlForSlopeFaktor(leistung2Schnell);

		testAthlet.removeLeistungFromAuswahlForSlopeFaktor(leistung1Schnell);
		testAthlet.setLeistungToAuswahlForSlopeFaktor(leistung1Schnell);
		assertEquals(schwelleSchnell, testAthlet.getAnaerobeSchwelle(), 0.1);
		LinkedList<Leistung> bestzeiten;
		bestzeiten = testAthlet.getMoeglicheBestzeitenListe ();
		assertEquals(465.1, bestzeiten.get(5).getZeit(), 0.1);
		assertEquals(465.1, testAthlet.calculateTime(3000.0), 0.1);
		testAthlet.removeLeistung(leistung1Schnell);
		testAthlet.resetLeistungAuswahlForSlopeFaktor();
	}
}