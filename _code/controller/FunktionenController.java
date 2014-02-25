package controller;

import model.Leistung;

/**
 * Controller zum Handlen aller Aktionen, die Funktionen wie
 * Berechnen von Slope und Schwelle usw betreffen
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta)
 */
public class FunktionenController {

//----------------------- VARIABLEN -----------------------
	
//----------------------- ÖFFENTLICHE METHODEN -----------------------
	
	
	// TODO: funktionencontroller komplett auflösen!
	
	
	
	/**
	 * 
	 * @param referenzLeistung
	 * @param entfernung
	 * @param slopeFaktor
	 * @return
	 */
	public double calculateSpeed (Leistung referenzLeistung, double entfernung, double slopeFaktor) {		
		double referenzGeschwindigkeit = referenzLeistung.getGeschwindigkeit();
		double referenzEntfernung = referenzLeistung.getStrecke();
		//Geschwindigkeit für 1 km
		double kilometerGeschwindigkeit = referenzGeschwindigkeit + slopeFaktor * (Math.log10(entfernung/referenzEntfernung));
		//Geschwindigkeit abhängig von entfernung
		double geschätzteGeschwindigkeit = kilometerGeschwindigkeit*(entfernung/1000);	
		
		if (geschätzteGeschwindigkeit <= 0) {
			return 0;
		}
		return geschätzteGeschwindigkeit;
	}
	
	
}
