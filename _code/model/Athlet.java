package model;

import java.util.LinkedList;

import main.Main;
import controller.StreckenController;

/**
 * Model-Klasse für das "Athlet"-Objekt
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta)
 */
public class Athlet implements AthletInterface{

	private static final int ANZAHL_LEISTUNGEN_FÜR_BERECHNUNG_DES_SLOPE_FAKTORS = 2;
	private static final int MINIMUM_VALID_SLOPE_FAKTOR = 15;
	private static final int MAXIMUM_VALID_SLOPE_FAKTOR = 200;
	
	private long id;
	private String name;
	private double slopeFaktor;
	private double anaerobeSchwelle;
	private LinkedList<Leistung> alleLeistungen = new LinkedList<Leistung>();

	private StreckenController streckenController = Main.mainFrame.streckenController;
	
	public Athlet(long id, String name) {
		this.id = id;
		this.name = name;		
	}
	
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
		if(!inAlleLeistungenEnthalten(ausgewaehlteLeistung)){
			return;
		}
		// bis jetzt wurde keine oder eine Leistung ausgewählt, d.h. es muss noch eine Leistung ausgewählt werden
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
		if(!inAlleLeistungenEnthalten(ausgewaehlteLeistung)){
			return;
		}
		for (Leistung aktuelleLeistung: alleLeistungen){
			if(aktuelleLeistung.equals(ausgewaehlteLeistung)){
				aktuelleLeistung.setIsUsedForSlopeFaktor(false);
			}
		}
	}
	
	public Leistung[] getLeistungAuswahlForSlopeFaktor() {
		Leistung[] LeistungAuswahlForSlopeFaktor = 
				new Leistung[ANZAHL_LEISTUNGEN_FÜR_BERECHNUNG_DES_SLOPE_FAKTORS];
		int i = 0;
		for (Leistung aktuelleLeistung: alleLeistungen){
			if (aktuelleLeistung.isUsedForSlopeFaktor()){
				LeistungAuswahlForSlopeFaktor[i] = aktuelleLeistung;
				i++;
			}
		}
		assert i <= ANZAHL_LEISTUNGEN_FÜR_BERECHNUNG_DES_SLOPE_FAKTORS;
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
		return "notSet";
	}
	
	private void requireSlopeFaktor() throws Exception{
		if ("set" != getSlopeFaktorStatus()){
			throw new Exception();
			// TODO: change to SlopeFaktorNotSetException
		}
	}
	
	/**
	 * Schätzen der möglichen Bestzeiten anhand SlopeFaktor und einer ReferenzleistungW
	 * @throws Exception 
	 */
	public LinkedList<Leistung> getMoeglicheBestzeitenListe () throws Exception { 
		requireSlopeFaktor();

		LinkedList<Leistung> bestzeitenListe = new LinkedList<Leistung>();
		for (int i = 0; i < streckenController.getStreckenLength(); i++) { 
			double entfernung = streckenController.getStreckenlaengeById(i); 
			double bestzeit = calculateTime(entfernung);
			// -1: mögliche Bestzeiten werden dem Athleten nicht direkt zugewiesen
			bestzeitenListe.add(new Leistung (i,-1,bestzeit,null,null)); 
		} 
		return bestzeitenListe; 
	}
	
	public double calculateSpeedSecondsPerKm(double entfernung) throws Exception {
		Leistung referenzLeistung = getLeistungAuswahlForSlopeFaktor()[0];
		double referenzGeschwindigkeit = referenzLeistung.getGeschwindigkeit();
		double referenzEntfernung = referenzLeistung.getStrecke();
		double speed = referenzGeschwindigkeit + slopeFaktor * (Math.log10(entfernung/referenzEntfernung));
		return speed;
	}
	
	public double calculateTime (double entfernung) throws Exception {
		requireSlopeFaktor();
		
		double kilometerGeschwindigkeit = calculateSpeedSecondsPerKm(entfernung);
		double geschätzteGeschwindigkeit = kilometerGeschwindigkeit*(entfernung/1000);	
		return geschätzteGeschwindigkeit;
	}
	
	public double getAnaerobeSchwelle() throws Exception{
		requireSlopeFaktor();
		return anaerobeSchwelle;
	}

	private void triggerCalculations() throws Exception{
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
	
	/**
	 * Berechnen des Slope-Faktors anhand zweier Leistungen
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

	private double slopeFaktorBerechnen(Leistung[] LeistungAuswahlForSlopeFaktor) {
		return slopeFaktorBerechnen(LeistungAuswahlForSlopeFaktor[0], LeistungAuswahlForSlopeFaktor[1]);
	}
	
	/**
	 * Schätzen der anaerobe Schwelle in s/km
	 */
	private void setAnaerobeSchwelle () throws Exception {
		if (!isSetSlopeFaktor()){
			return;
		}
		
		int maxIter = 1000;
		double accuracy = 1;
		double timeToSearch = 3600.0;
		double diff;
		double newGuessKm;
		double distanceMeter = 10000;
		int counter;
		for (counter = 0; counter < maxIter; counter++){
			newGuessKm = timeToSearch / (calculateSpeedSecondsPerKm(distanceMeter));
			diff = Math.abs(newGuessKm*1000 - distanceMeter);
			if (diff<accuracy) {
				break;
			}
			distanceMeter = newGuessKm*1000;
		}
		double speed = timeToSearch / distanceMeter*1000;	
		this.anaerobeSchwelle = speed;
	}
	
	private boolean inAlleLeistungenEnthalten(Leistung ausgewaehlteLeistung) {
		for (Leistung aktuelleLeistung: alleLeistungen){
			if(aktuelleLeistung.equals(ausgewaehlteLeistung)){
				return true;
			}
		}
		return false;
	}
	
	private boolean isValidSlopeFaktor(double inputSlopeFaktor){
		if (inputSlopeFaktor > MINIMUM_VALID_SLOPE_FAKTOR 
				&& inputSlopeFaktor < MAXIMUM_VALID_SLOPE_FAKTOR){
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