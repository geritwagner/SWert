package model;

import java.util.LinkedList;

import main.Main;
import controller.StreckenController;

/**
 * Model-Klasse für das "Athlet"-Objekt
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta)
 */
public class Athlet implements AthletInterface{

//----------------------- VARIABLEN -----------------------
	private long id;
	private String name;
	private double slopeFaktor;
	private double anaerobeSchwelle;
	private LinkedList<Leistung> alleLeistungen = new LinkedList<Leistung>();

	private StreckenController streckenController = Main.mainFrame.streckenController;
	
//----------------------- KONSTRUKTOREN -----------------------
	public Athlet(long id, String name) {
		this.id = id;
		this.name = name;		
	}

//----------------------- METHODEN -----------------------
	
	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}
		
	public LinkedList<Leistung> getLeistungen() {
		return alleLeistungen;
	}
	
	public boolean addLeistung(Leistung leistung) {
		alleLeistungen.add(leistung);
		return true;
	}
	
	public boolean removeLeistung(Leistung leistungToRemove) {
		for (int i = 0; i < alleLeistungen.size(); i++ ){
			Leistung aktuelleLeistung = alleLeistungen.get(i);
			if(aktuelleLeistung.equals(leistungToRemove)){
				alleLeistungen.remove(i);
				return true;
			}
		}
		return false;
	}

	public void setLeistungToAuswahlForSlopeFaktor(Leistung ausgewaehlteLeistung){
		assert inAlleLeistungenEnthalten(ausgewaehlteLeistung);
		// bis jetzt wurde keine oder eine Leistung ausgewählt, d.h. es muss noch eine Leistung ausgewähltwerden
		assert getLeistungAuswahlForSlopeFaktor()[1] == null;
		// Keine gleichen Strecken akzeptieren!
		if (getLeistungAuswahlForSlopeFaktor()[0] != null && 
				getLeistungAuswahlForSlopeFaktor()[0].getStrecke() == ausgewaehlteLeistung.getStrecke()){
			return;
		}
		
		for (Leistung aktuelleLeistung: alleLeistungen){
			if(aktuelleLeistung.equals(ausgewaehlteLeistung)){
				aktuelleLeistung.setIsUsedForSlopeFaktor(true);
			}
		}
	}

	public void removeLeistungFromAuswahlForSlopeFaktor(Leistung ausgewaehlteLeistung){
		assert inAlleLeistungenEnthalten(ausgewaehlteLeistung);
		for (Leistung aktuelleLeistung: alleLeistungen){
			if(aktuelleLeistung.equals(ausgewaehlteLeistung)){
				aktuelleLeistung.setIsUsedForSlopeFaktor(false);
			}
		}
	}
	
	public Leistung[] getLeistungAuswahlForSlopeFaktor() {
		Leistung[] LeistungAuswahlForSlopeFaktor = new Leistung[2];
		int i = 0;
		for (Leistung aktuelleLeistung: alleLeistungen){
			if (aktuelleLeistung.isUsedForSlopeFaktor()){
				LeistungAuswahlForSlopeFaktor[i] = aktuelleLeistung;
				i++;
			}
		}
		assert i<3;
		return LeistungAuswahlForSlopeFaktor;
	}
	
	public void resetLeistungAuswahlForSlopeFaktor(){
		for (Leistung aktuelleLeistung: alleLeistungen){
			aktuelleLeistung.setIsUsedForSlopeFaktor(false);
		}
	}

	
	public boolean isSetSlopeFaktor(){
		if ( isValidSlopeFaktor(slopeFaktor) && isValidLeistungAuswahlForSlopeFaktor()){
				return true;
		} else {
			return false;			
		}
	}

	public String getSlopeFaktorStatus(){
		try{
			triggerCalculations();
		} catch (Exception e){
			// nothing
		}
		if (isSetSlopeFaktor()){
			return "set";
		}
		// TODO: weitere Stati?
		return "notSet";
	}
	
	/**
	 * Schätzen der möglichen Bestzeiten anhand SlopeFaktor und einer Referenzleistung
	 * @return: Liste mit Leistungen für jede Streckenlänge, bei der die angegebene Zeit
	 * der möglichen Bestzeit entspricht
	 */
	public LinkedList<Leistung> getMoeglicheBestzeitenListe () { 
		assert "set" == getSlopeFaktorStatus();
		triggerCalculations();

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
	
	/**
	 * 
	 * @param entfernung
	 * @return
	 */
	public double calculateSpeed (double entfernung) {
		assert "set" == getSlopeFaktorStatus();
		triggerCalculations();
		
		Leistung referenzLeistung = getLeistungAuswahlForSlopeFaktor()[0];
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
	
	public double getAnaerobeSchwelle(){
		assert "set" == getSlopeFaktorStatus();
		triggerCalculations();
		return anaerobeSchwelle;
	}
	
		
//----------------------- PRIVATE METHODEN -----------------------

	private void triggerCalculations(){
		setSlopeFactor();
		setAnaerobeSchwelle();
	}
	
	private void setSlopeFactor(){
		if (isValidLeistungAuswahlForSlopeFaktor() ){
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
	
	private double slopeFaktorBerechnen(Leistung[] LeistungAuswahlForSlopeFaktor) {
		return slopeFaktorBerechnen(LeistungAuswahlForSlopeFaktor[0], LeistungAuswahlForSlopeFaktor[1]);
	}
	
	/**
	 * Berechnen des Slope-Faktors anhand zweier Leistungen
	 * @param leistung1
	 * @param leistung2
	 * @return: slopeFaktor
	 */
	private double  slopeFaktorBerechnen(Leistung leistung1, Leistung leistung2) {
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
		
		return Math.abs((geschwindigkeit2-geschwindigkeit1)/(Math.log10(strecke2/strecke1)));	
	}
	
	/**
	 * Schätzen der anaerobe Schwelle in s/km
	 */
	private void setAnaerobeSchwelle () {
		Leistung referenzLeistung = getLeistungAuswahlForSlopeFaktor()[0];
		double referenzGeschwindigkeit = referenzLeistung.getGeschwindigkeit();
		double referenzStrecke = (referenzLeistung.getStrecke()/1000D);		
		int referenzId = referenzLeistung.getId_strecke();
		if (referenzId == -1) {
			this.anaerobeSchwelle = referenzGeschwindigkeit;
		}
		int maxIter = 1000;
		double accuracy = 0.001;
		double timeToSearch = 3600.0;
		double diff;
		double newGuess;
		double distance = 10.0;
		int counter;
		
		for (counter = 0; counter < maxIter; counter++){
			newGuess = timeToSearch / (referenzGeschwindigkeit + this.slopeFaktor * (Math.log10(distance/referenzStrecke)));
			diff = Math.abs(newGuess-distance);
			if (diff<accuracy) {
				break;
			}
			distance = newGuess;
		}
		double speed = timeToSearch / distance;		
		this.anaerobeSchwelle = speed;
	}
	
	public boolean inAlleLeistungenEnthalten(Leistung ausgewaehlteLeistung) {
		for (Leistung aktuelleLeistung: alleLeistungen){
			if(aktuelleLeistung.equals(ausgewaehlteLeistung)){
				return true;
			}
		}
		return false;
	}
	
	private boolean isValidSlopeFaktor(double inputSlopeFaktor){
		if (inputSlopeFaktor > 15 && inputSlopeFaktor < 200){
			return true;
		} else {
			return false;
		}
	}

	private boolean isValidLeistungAuswahlForSlopeFaktor() {
		if (getLeistungAuswahlForSlopeFaktor() == null){
			return false;
		}
		Leistung leistung1 = getLeistungAuswahlForSlopeFaktor()[0];
		Leistung leistung2 = getLeistungAuswahlForSlopeFaktor()[1];
		if (leistung1 != null && leistung2 != null && leistung1.getStrecke() != leistung2.getStrecke()){
			return true;
		}
		return false;
	}

}