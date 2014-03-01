package model;

/**
 * Model-Klasse f�r die Strecken
 * @author Honors-WInfo-Projekt (Fabian B�hm, Alexander Puchta)
 */
public class Strecken {

//----------------------- VARIABLEN -----------------------
	/*
	 * Streckenl�ngen als Interger-Werte
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
			21098,
			25000,
			42195};
	
	/*
	 * Streckenl�ngen als Strings
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
