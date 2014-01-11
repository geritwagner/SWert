package model;

/**
 * Model-Klasse für die Strecken
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta)
 */
public class Strecken {

//----------------------- VARIABLEN -----------------------
	/*
	 * Streckenlängen als Interger-Werte
	 */
	private int[] streckenlaengen = {
			400,
			800,
			1000,
			1500,
			2000,
			3000,
			5000,
			10000,
			15000,
			21000,
			25000,
			42000};
	
	/*
	 * Streckenlängen als Strings
	 */
	private String[] streckenlaengenString = {
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

//----------------------- GETTER UND SETTER -----------------------
	public int getStreckenlaenge(int id) {		
		return streckenlaengen[id];
	}
	
	public String getStreckenlaengeString (int id) {
		return streckenlaengenString[id];
	}
	
	public int getStringArrayLength() {
		return streckenlaengenString.length;
	}
}
