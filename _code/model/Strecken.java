package model;

/**
 * Model-Klasse für die Strecken
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta)
 */
public abstract class Strecken {
	
	// Aktuell werden entweder Standardstrecken oder die Schwellenleistung (id = -1) verwaltet
	
	private static int[] streckenlaengen = {
			400,
			800,
			1000,
			1500,
			2000,
			3000,
			5000,
			10000,
			15000,
			21098,
			25000,
			42195};
	
	private static String[] streckenlaengenString = {
			"400m",
			"800m",
			"1.000m",
			"1.500m",
			"2.000m",
			"3.000m",
			"5.000m",
			"10.000m",
			"15km",
			"Halbmarathon",
			"25km",
			"Marathon"};

	public static int getStreckenlaengeById(int id) {		
		return streckenlaengen[id];
	}
	
	public static String getStreckenlaengeStringById(int id) {
		return streckenlaengenString[id];
	}
	
	public static int getStreckenArrayLength() {
		return streckenlaengenString.length;
	}

	public static int getStreckenIdByString(String strecke) {
		for (int zaehler = 0; zaehler < getStreckenArrayLength(); zaehler++) {
			String aktuellerStreckenString = getStreckenlaengeStringById(zaehler);
			if(strecke.equals(aktuellerStreckenString)) {
				return zaehler;
			}
		}
		return -1;
	}
}