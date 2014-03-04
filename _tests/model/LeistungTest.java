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
		
		testLeistung = new Leistung(1, 12, 146.5, "Test-Leistung", "01-01-2014");
		assertEquals(testLeistung.getId_strecke(), 1);
		assertEquals(testLeistung.getId_athlet(), 12);
		assertEquals(146.5, testLeistung.getZeit(), 0.1);
		System.out.println("speed: " + testLeistung.getGeschwindigkeit());
		//		assertEquals(183.125, testLeistung.getGeschwindigkeit(),0.1);
		assertEquals(testLeistung.getBezeichnung(), "Test-Leistung");
		assertEquals(testLeistung.getDatum(), "01-01-2014");
		assertEquals(testLeistung.getStrecke(), 800);

		System.out.println(testLeistung.getZeitString());
		testLeistung.setIsUsedForSlopeFaktor(true);
		assertTrue(testLeistung.isUsedForSlopeFaktor());
		
		assertTrue(testLeistung.equals(testLeistung));
		assertEquals("1; 800; 146.5(= 00:02:26,50); Test-Leistung; 01-01-2014; geschwindigkeit= 183.125;", testLeistung.toString());
		
		// TODO: -1 wird bei der Schwelle verwendet, dafür ist aber getStrecke nicht ausgelegt... müsste noch genau überprüft werden
		testLeistung = new Leistung(-1, 12, 146.5, "Test-Leistung", "01-01-2014");
		
		System.out.println(testLeistung.getStrecke());

		// TODO: wird die leistung_id überhaupt verwendet?!??!
//		assertEquals(1, testLeistung.getId());
	}

	// TODO: Test anpassen und ausbauen!
	@Test
	public void testGeschwindigkeitUndZeit(){
		//testen, ob bei Geschwindigkeit setzen die richtige Zeit gesetzt wird & vice versa
		double unbrauchbareGeschwindigkeit = 3.125;
		testLeistung = new Leistung(1, 12, unbrauchbareGeschwindigkeit, "Test-Leistung", "01-01-2014");
		testLeistung.setZeitFromString("00:02:26,50");
		assertEquals(testLeistung.getGeschwindigkeit(), 183.125, 0.001);
		testLeistung.setZeitFromString("00:10:26,50");
		testLeistung.setGeschwindigkeitAndGeschwindigkeit(183.125);
		assertEquals(testLeistung.getZeit(), 146.5, 0.1);
		assertEquals(testLeistung.getZeitString(), "00:02:26,50");
		testLeistung.setZeitAndGeschwindigkeit(146.5);
		assertEquals(testLeistung.getZeitString(), "00:02:26,50");		
	}

}
