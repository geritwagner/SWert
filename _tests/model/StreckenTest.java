package model;

import static org.junit.Assert.*;
import org.junit.Test;

public class StreckenTest {

	@Test
	public void testStrecke(){
		assertEquals(12, Strecken.getStringArrayLength());
		
		assertEquals(400, Strecken.getStreckenlaenge(0));
		assertEquals(800, Strecken.getStreckenlaenge(1));
		assertEquals(1000, Strecken.getStreckenlaenge(2));
		assertEquals(1500, Strecken.getStreckenlaenge(3));
		assertEquals(2000, Strecken.getStreckenlaenge(4));
		assertEquals(3000, Strecken.getStreckenlaenge(5));
		assertEquals(5000, Strecken.getStreckenlaenge(6));
		assertEquals(10000, Strecken.getStreckenlaenge(7));
		assertEquals(15000, Strecken.getStreckenlaenge(8));
		assertEquals(21098, Strecken.getStreckenlaenge(9));
		assertEquals(25000, Strecken.getStreckenlaenge(10));
		assertEquals(42195, Strecken.getStreckenlaenge(11));
		
		assertEquals("400m", Strecken.getStreckenlaengeString(0));
		assertEquals("800m", Strecken.getStreckenlaengeString(1));
		assertEquals("1.000m", Strecken.getStreckenlaengeString(2));
		assertEquals("1.500m", Strecken.getStreckenlaengeString(3));
		assertEquals("2.000m", Strecken.getStreckenlaengeString(4));
		assertEquals("3.000m", Strecken.getStreckenlaengeString(5));
		assertEquals("5.000m", Strecken.getStreckenlaengeString(6));
		assertEquals("10.000m", Strecken.getStreckenlaengeString(7));
		assertEquals("15km", Strecken.getStreckenlaengeString(8));
		assertEquals("Halbmarathon", Strecken.getStreckenlaengeString(9));
		assertEquals("25km", Strecken.getStreckenlaengeString(10));
		assertEquals("Marathon", Strecken.getStreckenlaengeString(11));		
	}
}
