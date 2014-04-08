package model;

import static org.junit.Assert.*;
import org.junit.*;

/**
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta), Gerit Wagner
 */

public class TestAthletenListe {
	
	private AthletenListe liste;
	
	@Test
	public void testConstructorAndDataMethods(){
		liste = new AthletenListe();
		assertEquals(0, liste.getAlleAthleten().size());
		assertEquals(null, liste.getLetzterGeoeffneterAthlet());
		assertEquals(null, liste.getLetzterGeschlossenerAthlet());
		
		Athlet athlet1 = new Athlet("athlet1", null);
		liste.addAthlet(athlet1);
		assertEquals(athlet1, liste.getLetzterGeoeffneterAthlet());
		liste.removeAthlet(athlet1);
		assertEquals(athlet1, liste.getLetzterGeschlossenerAthlet());
		assertEquals(null, liste.getLetzterGeoeffneterAthlet());
		assertEquals(null, liste.getLetzterGeschlossenerAthlet());
	}
}