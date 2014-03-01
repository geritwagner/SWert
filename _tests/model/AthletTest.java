package model;

import static org.junit.Assert.*;
import main.Main;
import org.junit.Before;
import org.junit.Test;
import view.MainFrame;

public class AthletTest {

	Athlet testAthlet;
	
	@Before
    public void initTest() {
		Main.mainFrame = new MainFrame();
		Main.mainFrame.initializeControllers();
    }
	
	@Test
	public void testConstructorAndDataMethods(){
		testAthlet = new Athlet(12, "Tester");
		assertEquals(testAthlet.getId(), 12);
		assertTrue( testAthlet.getName() == "Tester");
		assertTrue( 0 == testAthlet.getLeistungen().size());
		Leistung testLeistung = new Leistung(2, 12, 50.0, "Test Wettkampf", "01-01-2014");
		testAthlet.addLeistung(testLeistung);
		assertTrue( testAthlet.getLeistungen().get(0).equals(testLeistung));
		assertTrue( 50 == testAthlet.getLeistungen().get(0).getGeschwindigkeit());
		testAthlet.removeLeistung(testLeistung);
		System.out.println(testAthlet.getLeistungen().size());
		assertTrue( 0 == testAthlet.getLeistungen().size());
	}
	
	// TODO
	@Test
	public void testSlopeFaktorLogik() {
		testAthlet = new Athlet(12, "Tester");
//		Leistung leistung1 = new Leistung()
		
		assert true;
	}

	@Test
	public void testCalculations() {
		assert true;
	}
	
	
	
//	public void setLeistungToAuswahlForSlopeFaktor(Leistung ausgewaehlteLeistung);
//	public void removeLeistungFromAuswahlForSlopeFaktor(Leistung ausgewaehlteLeistung);
//	public Leistung[] getLeistungAuswahlForSlopeFaktor();
//
//	public boolean isSetSlopeFaktor();
//	public String getSlopeFaktorStatus();

	
	
//	// Berechnete Leistungen
//	public LinkedList<Leistung> getMoeglicheBestzeitenListe ();
//	public double calculateSpeed (double entfernung);
//	public double getAnaerobeSchwelle();

}
