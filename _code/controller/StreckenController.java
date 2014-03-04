package controller;

import model.Strecken;

/**
 * Controller zum Handlen aller Aktionen, die das Strecken-Model betreffen
 * @author Honors-WInfo-Projekt (Fabian B�hm, Alexander Puchta)
 */
public class StreckenController {
	
	//----------------------- �FFENTLICHE METHODEN -----------------------
	/**
	 * Auslesen des Integers-Wertes der Streckenl�nge an der Arraystelle id
	 * @param id: Arraystelle des auszulesenden Streckenl�ngewertes
	 * @return
	 */
	public int getStreckenlaengeById(int id) {
		int streckenlaenge = Strecken.getStreckenlaenge(id);
		return streckenlaenge;
	}
	
	/**
	 * Auslesen des String-Wertes der Streckenl�nge an der Arraystelle id
	 * @param id: Arraystelle des auszulesenden Streckenl�ngewertes
	 * @return
	 */
	public String getStreckenlaengeStringById(int id) {
		return Strecken.getStreckenlaengeString(id);		
	}
	
	/**
	 * Auslesen der ID der �bergebenen Strecke im Array
	 * @param strecke
	 * @return
	 */
	public int getStreckenIdByString(String strecke) {
		for (int zaehler = 0; zaehler < Strecken.getStringArrayLength(); zaehler++) {
			String aktuellerStreckenString = Strecken.getStreckenlaengeString(zaehler);
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
		return Strecken.getStringArrayLength();
	}
}
