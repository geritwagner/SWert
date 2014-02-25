package model;

import java.util.LinkedList;

import main.Main;
import controller.StreckenController;

/**
 * Model-Klasse für das "Athlet"-Objekt
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta)
 */
public class Athlet {

//----------------------- VARIABLEN -----------------------
	private long id;
	private String name;
	private double slopeFaktor;
	private double anaerobeSchwelle;
	private LinkedList<Leistung> alleLeistungen = new LinkedList<Leistung>();

	private StreckenController streckenController = Main.streckenController;
	
//----------------------- KONSTRUKTOREN -----------------------
	public Athlet(long id, String name) {
		this.id = id;
		this.name = name;		
	}

//----------------------- ÖFFETLICHE METHODEN -----------------------

	
	public void setLeistungToAuswahlForSlopeFaktor(Leistung ausgewaehlteLeistung){
		for (Leistung aktuelleLeistung: alleLeistungen){
			if(aktuelleLeistung.equals(ausgewaehlteLeistung)){
				aktuelleLeistung.setIsUsedForSlopeFaktor(true);
			}
		}
		setSlopeFactor();
		setAnaerobeSchwelle();
	}

	public void removeLeistungFromAuswahlForSlopeFaktor(Leistung ausgewaehlteLeistung){
		for (Leistung aktuelleLeistung: alleLeistungen){
			if(aktuelleLeistung.equals(ausgewaehlteLeistung)){
				aktuelleLeistung.setIsUsedForSlopeFaktor(false);
			}
		}
		setSlopeFactor();
		setAnaerobeSchwelle();
	}
	
	
//----------------------- PRIVATE METHODEN -----------------------


	private void setSlopeFactor(){
		if (isSetLeistungAuswahlForSlopeFaktor() ){
			double tempSlopeFaktor = slopeFaktorBerechnen(getLeistungAuswahlForSlopeFaktor());
			if(isValidSlopeFaktor(tempSlopeFaktor)){
				this.slopeFaktor = tempSlopeFaktor;
			} else {
				this.slopeFaktor = 0;
			}
		} else {
			this.slopeFaktor = 0;
		}
	}
	
	private boolean isValidSlopeFaktor(double tempSlopeFaktor){
		// TODO: Obergrenze ggf. geringer ansetzen
		if (tempSlopeFaktor > 1 && tempSlopeFaktor < 500){
			return true;
		} else {
			return false;
		}
	}
	
	private boolean isSetLeistungAuswahlForSlopeFaktor(){
		Leistung leistung1 = getLeistungAuswahlForSlopeFaktor()[0];
		Leistung leistung2 = getLeistungAuswahlForSlopeFaktor()[1];
		if (leistung1 != null && leistung2 != null){
			return true;
		}
		return false;
	}
	
	/**
	 * Berechnen des Slope-Faktors anhand zweier Leistungen
	 * @param leistung1
	 * @param leistung2
	 * @return: slopeFaktor
	 */
	private double slopeFaktorBerechnen(Leistung leistung1, Leistung leistung2) {
		// tauschen, wenn strecke1 > strecke2
		if (leistung1.getStrecke() > leistung2.getStrecke()){
			Leistung temp = leistung1;
			leistung1 = leistung2;
			leistung2 = temp;
		}
			
		double geschwindigkeit1 = leistung1.getGeschwindigkeit();
		double geschwindigkeit2 = leistung2.getGeschwindigkeit();
		
		double strecke1 = leistung1.getStrecke();
		double strecke2 = leistung2.getStrecke();
		
		if (strecke1 == strecke2) {
			return -1;
		}
		
		double slopeFaktor = Math.abs((geschwindigkeit2-geschwindigkeit1)/(Math.log10(strecke2/strecke1)));	
		return slopeFaktor;
	}
	
	private double slopeFaktorBerechnen(Leistung[] LeistungAuswahlForSlopeFaktor) {
		return slopeFaktorBerechnen(LeistungAuswahlForSlopeFaktor[0], LeistungAuswahlForSlopeFaktor[1]);
	}
	
	/**
	 * Schätzen der anaerobe Schwelle in s/km
	 */
	private void setAnaerobeSchwelle () {
		if (!isSetLeistungAuswahlForSlopeFaktor()){
			return;
		}
		Leistung referenzLeistung = getLeistungAuswahlForSlopeFaktor()[0];
		double referenzGeschwindigkeit = referenzLeistung.getGeschwindigkeit();
		double referenzEntfernung = (referenzLeistung.getStrecke()/1000D);		
		int referenzId = referenzLeistung.getId_strecke();
		if (referenzId == -1) {
			// TODO: check logic!!
			this.anaerobeSchwelle = referenzGeschwindigkeit;
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
			newGuess = timeToSearch / (referenzGeschwindigkeit + this.slopeFaktor * (Math.log10(distance/referenzEntfernung)));
			diff = Math.abs(newGuess-distance);
			if (diff<accuracy) {
				break;
			}
			distance = newGuess;
		}
		if (counter == maxIter) {
			this.anaerobeSchwelle = -1;
		}
		double speed = timeToSearch / distance;		
		this.anaerobeSchwelle = speed;
	}
	
	/**
	 * Schätzen der möglichen Bestzeiten anhand SlopeFaktor und einer Referenzleistung
	 * @return: Liste mit Leistungen für jede Streckenlänge, bei der die angegebene Zeit
	 * der möglichen Bestzeit entspricht
	 * @throws Exception 
	 */
	public LinkedList<Leistung> getMoeglicheBestzeitenListe () throws Exception { 
		if (!isSetLeistungAuswahlForSlopeFaktor()){
			// TODO: Exception anpassen
			throw new Exception();
		}
		Leistung referenzLeistung = getLeistungAuswahlForSlopeFaktor()[0];
		LinkedList<Leistung> bestzeitenListe = new LinkedList<Leistung>();
		double referenzGeschwindigkeit = referenzLeistung.getGeschwindigkeit();
		double referenzEntfernung = referenzLeistung.getStrecke();
		for (int i = 0; i < streckenController.getStreckenLength(); i++) { 
			double entfernung = streckenController.getStreckenlaengeById(i); 
			double bestzeit = referenzGeschwindigkeit + slopeFaktor * (Math.log10(entfernung/referenzEntfernung)); 
			bestzeitenListe.add(new Leistung (i,-1,bestzeit,null,null)); 
		} 
		return bestzeitenListe; 
	}
	
//----------------------- GETTER UND SETTER -----------------------
	
	public Leistung[] getLeistungAuswahlForSlopeFaktor() {
		Leistung[] LeistungAuswahlForSlopeFaktor = new Leistung[2];
		int i = 0;
		for (Leistung aktuelleLeistung: alleLeistungen){
			if (aktuelleLeistung.isUsedForSlopeFaktor()){
				LeistungAuswahlForSlopeFaktor[i] = aktuelleLeistung;
				i++;
			}
		}
		return LeistungAuswahlForSlopeFaktor;
	}
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getSlopeFaktor() {
		return slopeFaktor;
	}
		
	public LinkedList<Leistung> getLeistungen() {
		return alleLeistungen;
	}
	
	public boolean addLeistung(Leistung leistung) {
		alleLeistungen.add(leistung);
		return true;
	}
	
	public void resetLeistungen() {
		alleLeistungen = new LinkedList<Leistung>();
	}
	
	public double getAnaerobeSchwelle(){
		return anaerobeSchwelle;
	}
	
}
