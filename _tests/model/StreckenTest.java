package model;

import static org.junit.Assert.*;
import org.junit.Test;

public class StreckenTest {

	@Test
	public void coverage() {
		@SuppressWarnings("unused")
		Strecken b = new Strecken(){};
	}
	
	@Test
	public void testStrecke(){
		assertEquals(12, Strecken.getStreckenArrayLength());
		
		assertEquals(400, Strecken.getStreckenlaengeById(0));
		assertEquals(800, Strecken.getStreckenlaengeById(1));
		assertEquals(1000, Strecken.getStreckenlaengeById(2));
		assertEquals(1500, Strecken.getStreckenlaengeById(3));
		assertEquals(2000, Strecken.getStreckenlaengeById(4));
		assertEquals(3000, Strecken.getStreckenlaengeById(5));
		assertEquals(5000, Strecken.getStreckenlaengeById(6));
		assertEquals(10000, Strecken.getStreckenlaengeById(7));
		assertEquals(15000, Strecken.getStreckenlaengeById(8));
		assertEquals(21098, Strecken.getStreckenlaengeById(9));
		assertEquals(25000, Strecken.getStreckenlaengeById(10));
		assertEquals(42195, Strecken.getStreckenlaengeById(11));
		
		assertEquals("400m", Strecken.getStreckenlaengeStringById(0));
		assertEquals("800m", Strecken.getStreckenlaengeStringById(1));
		assertEquals("1.000m", Strecken.getStreckenlaengeStringById(2));
		assertEquals("1.500m", Strecken.getStreckenlaengeStringById(3));
		assertEquals("2.000m", Strecken.getStreckenlaengeStringById(4));
		assertEquals("3.000m", Strecken.getStreckenlaengeStringById(5));
		assertEquals("5.000m", Strecken.getStreckenlaengeStringById(6));
		assertEquals("10.000m", Strecken.getStreckenlaengeStringById(7));
		assertEquals("15km", Strecken.getStreckenlaengeStringById(8));
		assertEquals("Halbmarathon", Strecken.getStreckenlaengeStringById(9));
		assertEquals("25km", Strecken.getStreckenlaengeStringById(10));
		assertEquals("Marathon", Strecken.getStreckenlaengeStringById(11));
		
		assertEquals(0, Strecken.getStreckenIdByString("400m"));
		assertEquals(1, Strecken.getStreckenIdByString("800m"));
		assertEquals(2, Strecken.getStreckenIdByString("1.000m"));
		assertEquals(3, Strecken.getStreckenIdByString("1.500m"));
		assertEquals(4, Strecken.getStreckenIdByString("2.000m"));
		assertEquals(5, Strecken.getStreckenIdByString("3.000m"));
		assertEquals(6, Strecken.getStreckenIdByString("5.000m"));
		assertEquals(7, Strecken.getStreckenIdByString("10.000m"));
		assertEquals(8, Strecken.getStreckenIdByString("15km"));
		assertEquals(9, Strecken.getStreckenIdByString("Halbmarathon"));
		assertEquals(10, Strecken.getStreckenIdByString("25km"));
		assertEquals(11, Strecken.getStreckenIdByString("Marathon"));
		
		assertEquals(-1, Strecken.getStreckenIdByString("Schwelle"));
		
	}
}
