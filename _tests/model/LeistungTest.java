package model;

import static org.junit.Assert.*;
import main.Main;
import org.junit.Before;
import org.junit.Test;
import view.MainFrame;

public class LeistungTest {

	Leistung testLeistung;
	
	@Before
    public void initTest() {
		Main.mainFrame = new MainFrame();
		Main.mainFrame.initializeControllers();
    }
	
	@Test
	public void testConstructorAndDataMethods(){
		
		testLeistung = new Leistung(2, 12, 183.125, "Test-Leistung", "01-01-2014");
		assertEquals(testLeistung.getId_strecke(), 2);
		assertEquals(testLeistung.getId_athlet(), 12);
		assertEquals(testLeistung.getGeschwindigkeit(), 183.125, 0.001);
		assertEquals(testLeistung.getBezeichnung(), "Test-Leistung");
		assertEquals(testLeistung.getDatum(), "01-01-2014");
		testLeistung.setId_strecke(1);
		assertEquals(testLeistung.getStrecke(), 800);
		testLeistung.setBezeichnung("Aktualisierte Bezeichnung");
		assertEquals(testLeistung.getBezeichnung(), "Aktualisierte Bezeichnung");
		testLeistung.setDatum("02-02-2014");
		assertEquals(testLeistung.getDatum(), "02-02-2014");
		
		testLeistung.setIsUsedForSlopeFaktor(true);
		assertTrue(testLeistung.isUsedForSlopeFaktor());
		
		assertTrue(testLeistung.equals(testLeistung));
	}


	@Test
	public void testGeschwindigkeitUndZeit(){
		//testen, ob bei Geschwindigkeit setzen die richtige Zeit gesetzt wird & vice versa
		testLeistung = new Leistung(1, 12, 3.125, "Test-Leistung", "01-01-2014");
		testLeistung.setZeitFromString("00:02:26,50");
		assertEquals(testLeistung.getGeschwindigkeit(), 183.125, 0.001);
		testLeistung.setZeitFromString("00:10:26,50");
		testLeistung.setGeschwindigkeit(183.125);
		assertEquals(testLeistung.getZeit(), 146.5, 0.1);
		assertEquals(testLeistung.getZeitString(), "00:02:26,50");
		testLeistung.setZeit(146.5);
		assertEquals(testLeistung.getZeitString(), "00:02:26,50");		
	}
}
