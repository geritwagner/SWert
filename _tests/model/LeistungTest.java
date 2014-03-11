package model;

import static org.junit.Assert.*;

import main.Main;
import org.junit.*;
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
		testLeistung = new Leistung(1, 12, "Test-Leistung", "01-01-2014", 183.125);
		assertEquals(testLeistung.getId_strecke(), 1);
		assertEquals(testLeistung.getId_athlet(), 12);
		assertEquals(146.5, testLeistung.getZeit(), 0.1);
		assertEquals(testLeistung.getBezeichnung(), "Test-Leistung");
		assertEquals(testLeistung.getDatum(), "01-01-2014");
		assertEquals(testLeistung.getStrecke(), 800);
		@SuppressWarnings("unused")
		long i = testLeistung.getId();
		
		assertEquals("800m", testLeistung.getStreckenString());

		testLeistung.setIsUsedForSlopeFaktor(true);
		assertTrue(testLeistung.isUsedForSlopeFaktor());
		
		assertTrue(testLeistung.equals(testLeistung));
		assertEquals("1; 800; 146.5(= 00:02:26,50); Test-Leistung; 01-01-2014; geschwindigkeit= 183.125;", testLeistung.toString());
		
		// TODO: -1 wird bei der Schwelle verwendet, dafür ist aber getStrecke nicht ausgelegt... müsste noch genau überprüft werden
		testLeistung = new Leistung(-1, 12, "Test-Leistung", "01-01-2014", 183.125);
		
		testLeistung.updateLeistung(2, 150, "updated", "02-02-2015");
		assertEquals(1000, testLeistung.getStrecke());
		assertEquals(150, testLeistung.getZeit(), 0.1);
		assertEquals("updated", testLeistung.getBezeichnung());
		assertEquals("02-02-2015", testLeistung.getDatum());
	}

	@Test
	public void testEquals(){
		Leistung leistung1 = new Leistung(1,12,"Test-Leistung","01-01-2014",180);
		Leistung leistung2 = new Leistung(1,12,"Test-Leistung","01-01-2014",180);
		assertTrue(leistung1.equals(leistung2));
		leistung2 = new Leistung(2,12,"Test-Leistung","01-01-2014",180);
		assertFalse(leistung1.equals(leistung2));
		leistung2 = new Leistung(1,13,"Test-Leistung","01-01-2014",180);
		assertFalse(leistung1.equals(leistung2));
		leistung2 = new Leistung(1,12,"Test-Leistung 2","01-01-2014",180);
		assertFalse(leistung1.equals(leistung2));
		leistung2 = new Leistung(1,12,"Test-Leistung","01-01-2015",180);
		assertFalse(leistung1.equals(leistung2));
		leistung2 = new Leistung(1,12,"Test-Leistung","01-01-2014",190);
		assertFalse(leistung1.equals(leistung2));
	}
	
	@Test
	public void testGeschwindigkeitUndZeit(){
		//testen, ob bei Geschwindigkeit setzen die richtige Zeit gesetzt wird & vice versa
		double unbrauchbareGeschwindigkeit = 3.125;
		testLeistung = new Leistung(1, 12, "Test-Leistung", "01-01-2014", unbrauchbareGeschwindigkeit);
		testLeistung.setZeitFromString("00:02:26,50");
		assertEquals(testLeistung.getGeschwindigkeit(), 183.125, 0.001);
		testLeistung.setZeitFromString("00:10:26,50");
		testLeistung.setGeschwindigkeitAndGeschwindigkeit(183.125);
		assertEquals(testLeistung.getZeit(), 146.5, 0.1);
		assertEquals(testLeistung.getZeitString(), "00:02:26,50");
		testLeistung.setZeitAndGeschwindigkeit(146.5);
		assertEquals(testLeistung.getZeitString(), "00:02:26,50");		
	}
	
	@Test
	public void testGetObjectDataForTable() {
		testLeistung = new Leistung(1, 12, "Test-Leistung", "01-01-2014", 183.125);
		Object[] data = testLeistung.getObjectDataForTable();
		assertEquals("01-01-2014", data[0]);
		assertEquals("800m", data[1]);
		assertEquals("Test-Leistung", data[2]);
		assertEquals("00:02:26,50", data[3]);
		assertEquals("19,66", data[4]);
		assertEquals("5,46", data[5]);
		assertEquals("03:03,01", data[6]);
		assertEquals(false, data[7]);
		assertEquals(1, data[8]);
		assertEquals("183.125", data[9]);	
		testLeistung = new Leistung(-1, 12, "Test-Leistung", "01-01-2014", 183.125);
		
		// Schwelle: 180s/km müssten 20km entsprechen.
	testLeistung = new Leistung(-1, 12, "Schwellen-Leistung", "01-01-2014", 180);
	data = testLeistung.getObjectDataForTable();
	assertEquals("20.000m", data[1]);
	}
}