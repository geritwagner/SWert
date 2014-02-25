package controller;

import model.Leistung;

/**
 * Controller zum Handlen aller Aktionen, die Funktionen wie
 * Berechnen von Slope und Schwelle usw betreffen
 * @author Honors-WInfo-Projekt (Fabian B�hm, Alexander Puchta)
 */
public class FunktionenController {

//----------------------- VARIABLEN -----------------------
	
//----------------------- �FFENTLICHE METHODEN -----------------------
	
	
	// TODO: funktionencontroller komplett aufl�sen!
	
	
	
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
		//Geschwindigkeit f�r 1 km
		double kilometerGeschwindigkeit = referenzGeschwindigkeit + slopeFaktor * (Math.log10(entfernung/referenzEntfernung));
		//Geschwindigkeit abh�ngig von entfernung
		double gesch�tzteGeschwindigkeit = kilometerGeschwindigkeit*(entfernung/1000);	
		
		if (gesch�tzteGeschwindigkeit <= 0) {
			return 0;
		}
		return gesch�tzteGeschwindigkeit;
	}
	
	
}
