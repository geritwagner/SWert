package controller;

import model.Strecken;

/**
 * Controller zum Handlen aller Aktionen, die das Strecken-Model betreffen
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta)
 */
public class StreckenController {
	
//----------------------- VARIABLEN -----------------------
	private Strecken strecken = new Strecken();
	
//----------------------- ÖFFENTLICHE METHODEN -----------------------
	/**
	 * Auslesen des Integers-Wertes der Streckenlänge an der Arraystelle id
	 * @param id: Arraystelle des auszulesenden Streckenlängewertes
	 * @return
	 */
	public int getStreckenlaengeById(int id) {
		int streckenlaenge = strecken.getStreckenlaenge(id);
		return streckenlaenge;
	}
	
	/**
	 * Auslesen des String-Wertes der Streckenlänge an der Arraystelle id
	 * @param id: Arraystelle des auszulesenden Streckenlängewertes
	 * @return
	 */
	public String getStreckenlaengeStringById(int id) {
		return strecken.getStreckenlaengeString(id);		
	}
	
	/**
	 * Auslesen der ID der übergebenen Strecke im Array
	 * @param strecke
	 * @return
	 */
	public int getStreckenIdByString(String strecke) {
		for (int zaehler = 0; zaehler < strecken.getStringArrayLength(); zaehler++) {
			String aktuellerStreckenString = strecken.getStreckenlaengeString(zaehler);
			if(strecke.equals(aktuellerStreckenString)) {
				return zaehler;
			}
		}
		return -1;
	}
	
	/**
	 * Anzahl aller Strecken ermitteln
	 * @return
	 */
	public int getStreckenLength() {
		return strecken.getStringArrayLength();
	}
}
