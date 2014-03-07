package model;

import static org.junit.Assert.*;

import java.util.LinkedList;

import main.Main;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import view.MainFrame;

public class AthletTest {

	// ECLEMMA: Bug expectedException - http://www.eclemma.org/faq.html#trouble05
	
	Athlet testAthlet;
	
    @Rule
    public ExpectedException thrown= ExpectedException.none();
    
	@Before
    public void initTest() {
		Main.mainFrame = new MainFrame();
		Main.mainFrame.initializeControllers();
    }
	
	@Test
	public void testConstructorAndDataMethods(){
		testAthlet = new Athlet(12, "Tester");
		assertEquals(testAthlet.getId(), 12);
		testAthlet = null;
		testAthlet = new Athlet ("Tester");
		long naechsteId = testAthlet.getId() + 1;
		Athlet neuerAthlet = new Athlet ("neuer");
		assertEquals(naechsteId, neuerAthlet.getId());
		
		assertTrue( testAthlet.getName() == "Tester");
		assertTrue( 0 == testAthlet.getLeistungen().size());
		Leistung testLeistung = new Leistung(2, 12, 50.0, "Test Wettkampf", "01-01-2014");
		testAthlet.addLeistung(testLeistung);
		assertTrue( testAthlet.getLeistungen().get(0).equals(testLeistung));
		assertTrue( 50 == testAthlet.getLeistungen().get(0).getGeschwindigkeit());
		testAthlet.removeLeistung(testLeistung);
		testAthlet.removeLeistung(testLeistung);
		assertTrue( 0 == testAthlet.getLeistungen().size());
		
		testAthlet = new Athlet ("Athlet ohne ID (wird erzeugt)");
		assertEquals("Athlet ohne ID (wird erzeugt)", testAthlet.getName());
	}
	
	@Test
	public void testSlopeFaktorLogik() throws Exception {
		testAthlet = new Athlet(12, "Tester");
		assertEquals("notSet", testAthlet.getSlopeFaktorStatus());
		Leistung leistung1 = new Leistung(1, 12, 146.5, "800m-Leistung (langsam)", "01-01-2014");
		Leistung leistung2 = new Leistung(7, 12, 61.5300003, "10.00m-Leistung (langsam)", "01-01-2014");
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
		
		leistung2 = new Leistung(7, 12, 2615.3000000000003, "10.000m-Leistung (langsam)", "01-01-2014");
		assertEquals(testAthlet.getSlopeFaktorStatus(), "notSet");
		testAthlet.addLeistung(leistung2);
		assertEquals(testAthlet.getSlopeFaktorStatus(), "notSet");
		assertFalse(testAthlet.isSetSlopeFaktor());
		testAthlet.setLeistungToAuswahlForSlopeFaktor(leistung2);
		assertEquals(testAthlet.getSlopeFaktorStatus(), "set");
		assertTrue(testAthlet.isSetSlopeFaktor());
		
		Leistung nichtEnthalteneLeistung = new Leistung(7, 132, 1436.5, "nicht in der Leistungs-Liste enthalten", "01-01-2014");
		testAthlet.removeLeistungFromAuswahlForSlopeFaktor(nichtEnthalteneLeistung);
		testAthlet.setLeistungToAuswahlForSlopeFaktor(nichtEnthalteneLeistung);
		
		// Werden die Leistungen getauscht, wenn sie nicht nach aufsteigender Streckenl�nge hinzugef�gt werden?
		leistung1 = new Leistung(7, 12, 2615.3000000000003, "10.000m-Leistung (langsam)", "01-01-2014");
		leistung2 = new Leistung(1, 12, 146.5, "800m-Leistung (langsam)", "01-01-2014");
		testAthlet = new Athlet(12, "Tester");
		testAthlet.addLeistung(leistung1);
		testAthlet.setLeistungToAuswahlForSlopeFaktor(leistung1);
		testAthlet.addLeistung(leistung2);
		testAthlet.setLeistungToAuswahlForSlopeFaktor(leistung2);
		assertEquals("set", testAthlet.getSlopeFaktorStatus());

		// Es sollten keine Leistungen �ber die gleiche Strecke als Slope-Faktor gesetzt werden k�nnen
		
		leistung1 = new Leistung(7, 12, 1832, "10.000m-Leistung (langsam)", "01-01-2014");
		leistung2 = new Leistung(1, 12, 146.5, "800m-Leistung (langsam)", "01-01-2014");
		testAthlet = new Athlet(12, "Tester");
		// zu gute Slope-Faktoren sollten nicht akzeptiert werden
		testAthlet.addLeistung(leistung1);
		testAthlet.setLeistungToAuswahlForSlopeFaktor(leistung1);
		testAthlet.addLeistung(leistung2);
		testAthlet.setLeistungToAuswahlForSlopeFaktor(leistung2);
		assertEquals("notSet", testAthlet.getSlopeFaktorStatus());

		// zu schlechte Slope-Faktoren sollten nicht akzeptiert werden
		testAthlet.removeLeistung(leistung1);
		leistung1 = new Leistung(7, 12, 188432, "10.000m-Leistung (langsam)", "01-01-2014");
		testAthlet.addLeistung(leistung1);
		testAthlet.setLeistungToAuswahlForSlopeFaktor(leistung1);
		assertEquals("notSet", testAthlet.getSlopeFaktorStatus());
	}
	
	@Test
	public void testCalculations() throws Exception {
		testAthlet = new Athlet(12, "Tester");
				
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
		
		// Auswahl einer 3. Leistung f�r die Berechnung des Slope-Faktors sollte nicht m�glich sein.
		Leistung leistung1 = new Leistung(7, 12, 2615.3000000000003, "10.000m-Leistung", "01-01-2014");
		Leistung leistung2 = new Leistung(1, 12, 146.5, "800m-Leistung", "01-01-2014");
		Leistung leistung3 = new Leistung (6, 12, 1315, "5.000m-Leistung", "11-01-2014");
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

		leistung1 = new Leistung(7, 12, 2615.3000000000003, "10.000m-Leistung", "01-01-2014");
		leistung2 = new Leistung(7, 12, 2815.3000000000003, "andere 10.000m-Leistung", "01-01-2014");
		testAthlet.addLeistung(leistung1);
		testAthlet.addLeistung(leistung2);
		testAthlet.addLeistung(leistung3);
		testAthlet.setLeistungToAuswahlForSlopeFaktor(leistung1);
		
		// es d�rfen keine Leistungen �ber die gleiche Strecke f�r die Berechnung des Slope-Faktors ausgew�hlt werden
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

		Leistung leistung1Langsam = new Leistung(1, 12, 146.5, "800m-Leistung (langsam)", "01-01-2014");
		Leistung leistung2Langsam = new Leistung(7, 12, 2615.3, "10.00m-Leistung (langsam)", "01-01-2014");
		double schwelleLangsam = 270.4;
		
		Leistung leistung1Mittel = new Leistung(1, 12, 132.2, "800m-Leistung (mittel)", "01-01-2014");
		Leistung leistung2Mittel= new Leistung(7, 12, 2186.4, "10.00m-Leistung (mittel)", "01-01-2014");
		double schwelleMittel = 228.3;
		
		Leistung leistung1Schnell = new Leistung(1, 12, 109.6, "800m-Leistung (schnell)", "01-01-2014");
		Leistung leistung2Schnell= new Leistung(7, 12, 1714.7 , "10.00m-Leistung (schnell)", "01-01-2014");
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
