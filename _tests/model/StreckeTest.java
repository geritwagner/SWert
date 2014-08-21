package model;

import static org.junit.Assert.*;

import org.junit.Test;

public class StreckeTest {

	private Strecke testStrecke;
	
	@Test
	public void testConstructorAndDataMethods(){
		int id = 100;
		int andere_id = 101;
		int laenge = 50;
		int andere_laenge = 51;
		String bezeichnung = "50m-Test";
		String andere_bezeichnung  = "51m-Test2";
		
		testStrecke = new Strecke(100, bezeichnung, laenge);
		
		assertEquals(bezeichnung, testStrecke.getBezeichnung());
		assertEquals(laenge, testStrecke.getLaenge());
		assertEquals(id, testStrecke.getStrecken_id());
		
		testStrecke.setStrecken_id(andere_id);
		testStrecke.setBezeichnung(andere_bezeichnung);
		testStrecke.setLaenge(andere_laenge);
		
		assertEquals(andere_bezeichnung, testStrecke.getBezeichnung());
		assertEquals(andere_laenge, testStrecke.getLaenge());
		assertEquals(andere_id, testStrecke.getStrecken_id());
	}
}
