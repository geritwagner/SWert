package model;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;


public class AthletTest {

	// ECLEMMA: Bug expectedException - http://www.eclemma.org/faq.html#trouble05
	
	Athlet testAthlet;
	
    @Rule
    public ExpectedException thrown= ExpectedException.none();

	
	@Test
	public void testConstructorAndDataMethods(){
		testAthlet = new Athlet(12, "Tester", null);
		assertEquals(testAthlet.getId(), 12);
		testAthlet = null;
		testAthlet = new Athlet ("Tester", null);
		long naechsteId = testAthlet.getId() + 1;
		Athlet neuerAthlet = new Athlet ("neuer", null);
		assertEquals(naechsteId, neuerAthlet.getId());
		
		assertTrue( testAthlet.getName() == "Tester");
		assertTrue( 0 == testAthlet.getLeistungen().size());
		Leistung testLeistung = new Leistung(4, 12, "Test Wettkampf", "01-01-2014", 180);
		testAthlet.addLeistung(testLeistung);
		assertTrue( testAthlet.getLeistungen().get(0).equals(testLeistung));
		assertTrue( 360 == testAthlet.getLeistungen().get(0).getZeit());
		testAthlet.removeLeistung(testLeistung);
		testAthlet.removeLeistung(testLeistung);
		assertTrue( 0 == testAthlet.getLeistungen().size());
		
		testAthlet = new Athlet ("Athlet ohne ID (wird erzeugt)", null);
		assertEquals("Athlet ohne ID (wird erzeugt)", testAthlet.getName());
	}
	
	@Test
	public void testSlopeFaktorLogik() throws Exception {
		testAthlet = new Athlet(12, "Tester", null);
		assertEquals("notSet", testAthlet.getSlopeFaktorStatus());
		
		// testen, ob ein "zu guter" Slope-Faktor erkannt & nicht verwendet wird.
		Leistung leistung1 = new Leistung(1, 12, "800m-Leistung (langsam)", "01-01-2014", 183.125);
		Leistung leistung2 = new Leistung(7, 12, "10.000m-Leistung (langsam)", "01-01-2014", 61000);
		testAthlet.addLeistung(leistung1);
		testAthlet.addLeistung(leistung2);
		testAthlet.setLeistungToAuswahlForSlopeFaktor(leistung1);
		testAthlet.setLeistungToAuswahlForSlopeFaktor(leistung2);
		assertEquals(testAthlet.getLeistungAuswahlForSlopeFaktor()[0], leistung1);
		assertEquals(testAthlet.getLeistungAuswahlForSlopeFaktor()[1], leistung2);
		testAthlet.removeLeistung(leistung2);
		assertTrue(testAthlet.getLeistungAuswahlForSlopeFaktor()[1] == null);
		assertFalse(testAthlet.isSetSlopeFaktor());
		assertEquals(testAthlet.getSlopeFaktorStatus(), "notSet");
		
		leistung2 = new Leistung(7, 12, "10.000m-Leistung (langsam)", "01-01-2014", 261.53);
		assertEquals(testAthlet.getSlopeFaktorStatus(), "notSet");
		testAthlet.addLeistung(leistung2);
		assertEquals(testAthlet.getSlopeFaktorStatus(), "notSet");
		assertFalse(testAthlet.isSetSlopeFaktor());
		testAthlet.setLeistungToAuswahlForSlopeFaktor(leistung2);
		assertEquals(testAthlet.getSlopeFaktorStatus(), "set");
		assertTrue(testAthlet.isSetSlopeFaktor());
		
		Leistung nichtEnthalteneLeistung = new Leistung(7, 132, "nicht in der Leistungs-Liste enthalten", "01-01-2014", 143.65);
		testAthlet.removeLeistungFromAuswahlForSlopeFaktor(nichtEnthalteneLeistung);
		testAthlet.setLeistungToAuswahlForSlopeFaktor(nichtEnthalteneLeistung);
		
		// Werden die Leistungen getauscht, wenn sie nicht nach aufsteigender Streckenlänge hinzugefügt werden?
		leistung1 = new Leistung(7, 12, "10.000m-Leistung (langsam)", "01-01-2014", 261.53);
		leistung2 = new Leistung(1, 12, "800m-Leistung (langsam)", "01-01-2014", 183.125);
		testAthlet = new Athlet(12, "Tester", null);
		testAthlet.addLeistung(leistung1);
		testAthlet.setLeistungToAuswahlForSlopeFaktor(leistung1);
		testAthlet.addLeistung(leistung2);
		testAthlet.setLeistungToAuswahlForSlopeFaktor(leistung2);
		assertEquals("set", testAthlet.getSlopeFaktorStatus());

		// Es sollten keine Leistungen über die gleiche Strecke als Slope-Faktor gesetzt werden können
		
		leistung1 = new Leistung(7, 12, "10.000m-Leistung (langsam)", "01-01-2014",183.2);
		leistung2 = new Leistung(1, 12, "800m-Leistung (langsam)", "01-01-2014", 183.125);
		testAthlet = new Athlet(12, "Tester", null);
		// zu gute Slope-Faktoren sollten nicht akzeptiert werden
		testAthlet.addLeistung(leistung1);
		testAthlet.setLeistungToAuswahlForSlopeFaktor(leistung1);
		testAthlet.addLeistung(leistung2);
		testAthlet.setLeistungToAuswahlForSlopeFaktor(leistung2);
		assertEquals("notSet", testAthlet.getSlopeFaktorStatus());

		// zu schlechte Slope-Faktoren sollten nicht akzeptiert werden
		testAthlet.removeLeistung(leistung1);
//		leistung1 = new Leistung(7, 12, 188432, "10.000m-Leistung (langsam)", "01-01-2014");
		leistung1 = new Leistung(7, 12, "10.000m-Leistung (langsam)", "01-01-2014", 18843.2);
		testAthlet.addLeistung(leistung1);
		testAthlet.setLeistungToAuswahlForSlopeFaktor(leistung1);
		assertEquals("notSet", testAthlet.getSlopeFaktorStatus());
	}
	
	@Test
	public void testCalculations() throws Exception {
		testAthlet = new Athlet(12, "Tester", null);
				
		boolean exceptionThrown = false;
		try{
			// test Exception at requireSlopeFaktor();
			@SuppressWarnings("unused")
			LinkedList<Leistung> liste = testAthlet.getMoeglicheBestzeitenListe();
			fail("no Exception thrown");
		} catch (Exception e){
			exceptionThrown = true;
		}
		assertTrue(exceptionThrown);
		
		// Auswahl einer 3. Leistung für die Berechnung des Slope-Faktors sollte nicht möglich sein.
//		Leistung leistung1 = new Leistung(7, 12, 2615.3000000000003, "10.000m-Leistung", "01-01-2014");
//		Leistung leistung2 = new Leistung(1, 12, 146.5, "800m-Leistung", "01-01-2014");
//		Leistung leistung3 = new Leistung (6, 12, 1315, "5.000m-Leistung", "11-01-2014");
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
			fail("no Exception thrown");
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
		
		// es dürfen keine Leistungen über die gleiche Strecke für die Berechnung des Slope-Faktors ausgewählt werden
		exceptionThrown = false;
		try{
			testAthlet.setLeistungToAuswahlForSlopeFaktor(leistung2);
			fail("no Exception thrown");
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