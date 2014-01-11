package controller;

import java.util.LinkedList;

import main.Main;
import model.Leistung;

/**
 * Controller zum Handlen aller Aktionen, die Funktionen wie
 * Berechnen von Slope und Schwelle usw betreffen
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta)
 */
public class FunktionenController {

//----------------------- VARIABLEN -----------------------
	private StreckenController streckenController = Main.streckenController;
	private LeistungController leistungController = Main.leistungController;
	
//----------------------- ÖFFENTLICHE METHODEN -----------------------
	/**
	 * Berechnen des Slope-Faktors anhand zweier Leistungen
	 * @param leistung1
	 * @param leistung2
	 * @return: slopeFaktor
	 */
	public double slopeFaktorBerechnen(Leistung leistung1, Leistung leistung2) {
		double geschwindigkeit1 = leistung1.getGeschwindigkeit();
		double geschwindigkeit2 = leistung2.getGeschwindigkeit();
		
		double strecke1 = getStrecke(leistung1);
		double strecke2 = getStrecke(leistung2);
		
		if (strecke1 == strecke2) {
			return -1;
		}
		
		double slopeFaktor = Math.abs((geschwindigkeit2-geschwindigkeit1)/(Math.log10(strecke2/strecke1)));	
		return slopeFaktor;
	}
	
	/**
	 * 
	 * @param referenzLeistung
	 * @param entfernung
	 * @param slopeFaktor
	 * @return
	 */
	public double calculateSpeed (Leistung referenzLeistung, double entfernung, double slopeFaktor) {		
		double referenzGeschwindigkeit = referenzLeistung.getGeschwindigkeit();
		double referenzEntfernung = getStrecke(referenzLeistung);
		//Geschwindigkeit für 1 km
		double kilometerGeschwindigkeit = referenzGeschwindigkeit + slopeFaktor * (Math.log10(entfernung/referenzEntfernung));
		//Geschwindigkeit abhängig von entfernung
		double geschätzteGeschwindigkeit = kilometerGeschwindigkeit*(entfernung/1000);	
		
		if (geschätzteGeschwindigkeit <= 0) {
			return 0;
		}
		return geschätzteGeschwindigkeit;
	}
	
	/**
	 * Schätzen der anaerobe Schwelle in s/km
	 * @param referenzLeistung
	 * @param slopeFaktor
	 * @return: schwelle
	 */
	public double anaerobeSchwelleBerechnen (Leistung referenzLeistung, double slopeFaktor) {
		double referenzGeschwindigkeit = referenzLeistung.getGeschwindigkeit();
		double referenzEntfernung = (getStrecke(referenzLeistung)/1000D);		
		int referenzId = referenzLeistung.getId_strecke();
		if (referenzId == -1) {
			return referenzGeschwindigkeit;
		}
		int maxIter = 1000;
		double accuracy = 0.001;
		double timeToSearch = 3600.0;
		double diff;
		double newGuess;
		double distance;
		distance = 10.0;
		int counter;
		
		for (counter = 0; counter < maxIter; counter++){
			newGuess = timeToSearch / (referenzGeschwindigkeit + slopeFaktor * (Math.log10(distance/referenzEntfernung)));
			diff = Math.abs(newGuess-distance);
			if (diff<accuracy) {
				break;
			}
			distance = newGuess;
		}
		if (counter == maxIter) {
			return -1;
		}
		double speed = timeToSearch / distance;		
		return speed;
	}
	
	/**
	 * Schätzen der möglichen Bestzeiten anhand SlopeFaktor und einer Referenzleistung
	 * @param referenzLeistung
	 * @param slopeFaktor
	 * @return: Liste mit Leistungen für jede Streckenlänge, bei der die angegebene Zeit
	 * der möglichen Bestzeit entspricht
	 */
	public LinkedList<Leistung> bestzeitenListe (Leistung referenzLeistung, double slopeFaktor) { 
		LinkedList<Leistung> bestzeitenListe = new LinkedList<Leistung>();
		double referenzGeschwindigkeit = referenzLeistung.getGeschwindigkeit();
		double referenzEntfernung = getStrecke(referenzLeistung);
		for (int i = 0; i < streckenController.getStreckenLength(); i++) { 
			double entfernung = streckenController.getStreckenlaengeById(i); 
			double bestzeit = referenzGeschwindigkeit + slopeFaktor * (Math.log10(entfernung/referenzEntfernung)); 
			bestzeitenListe.add(new Leistung (i,-1,bestzeit,null,null)); 
		} 
		return bestzeitenListe; 
	}
	
	/**
	 * Auslesen der gegebenen Streckenlänge einer Strecke
	 * @param leistung
	 * @return: Double-Wert der Streckenlänge
	 */
	public double getStrecke(Leistung leistung) {
		int streckenID = leistung.getId_strecke();
		double geschwindigkeit = leistung.getGeschwindigkeit();
		if(streckenID == -1) {
			return leistungController.berechneStreckeAusGeschwindigkeit(geschwindigkeit);
		} else {
			return streckenController.getStreckenlaengeById(streckenID);
		}
	}
	
}
