package model;

/**
 * Model-Klasse für die Strecken
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta)
 */
// TODO: auf statisch umschreiben und StreckenController entfernen!
public abstract class Strecken {
	
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

	public static int getStreckenlaenge(int id) {		
		return streckenlaengen[id];
	}
	
	public static String getStreckenlaengeString (int id) {
		return streckenlaengenString[id];
	}
	
	public static int getStringArrayLength() {
		return streckenlaengenString.length;
	}
}
