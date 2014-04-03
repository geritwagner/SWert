package model;

import globale_helper.LeistungHelper;

import java.util.*;
import java.util.prefs.*;

/**
 * @author Honors-WInfo-Projekt (Fabian B�hm, Alexander Puchta), Gerit Wagner
 */

public class Athlet extends Observable implements AthletInterface {

	private static final int ANZAHL_LEISTUNGEN_F�R_BERECHNUNG_DES_SLOPE_FAKTORS = 2;
	private static final int MINIMUM_VALID_SLOPE_FAKTOR = 15;
	private static final int MAXIMUM_VALID_SLOPE_FAKTOR = 200;
	
	private long id;
	private String name;
	private double slopeFaktor;
	private double anaerobeSchwelle;
	private LinkedList<Leistung> alleLeistungen;
	private String speicherpfad = "";
	
	private LeistungHelper leistungHelper;
	private Preferences pref = Preferences.userRoot().node(this.getClass().getName());
	
	public Athlet(String name, LinkedList<Leistung> leistungen) {
		leistungHelper = new LeistungHelper();
		this.id = getNextAthletId();
		this.name = name;
		if (leistungen == null){
			alleLeistungen = new LinkedList<Leistung>();
		} else {
			alleLeistungen = leistungen;
		}
	}
	
	public Athlet(long id, String name, LinkedList<Leistung> leistungen) {
		leistungHelper = new LeistungHelper();
		this.id = id;
		this.name = name;
		if (leistungen == null){
			alleLeistungen = new LinkedList<Leistung>();
		} else {
			alleLeistungen = leistungen;			
		}
	}
	
	private long getNextAthletId() {
		long idAthlet = 0;
		idAthlet = pref.getLong("ahletId", 1);
		writeAthletId(idAthlet);
		return idAthlet;
	}
	
	private void writeAthletId(long id) {
		pref.put("ahletId", String.valueOf(id+1));
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
		setChanged();
		notifyObservers(this);
		return true;
	}
	
	public boolean removeLeistung(Leistung leistungToRemove) {
		for (int i = 0; i < alleLeistungen.size(); i++ ){
			Leistung aktuelleLeistung = alleLeistungen.get(i);
			if(aktuelleLeistung.equals(leistungToRemove)){
				alleLeistungen.remove(i);
				setChanged();
				notifyObservers(this);
				return true;
			}
		}
		return false;
	}

	public Leistung getLeistungById(long id) {
		for (Leistung aktuelleLeistung: alleLeistungen){
			if (aktuelleLeistung.getId() == id)
				return aktuelleLeistung;
		}
		return null;
	}
	
	public void updateLeistung(long id_leistung, int id_strecke,
			String bezeichnung, String datum, double geschwindigkeit) {
		Leistung zu�nderndeLeistung = getLeistungById(id_leistung);
		zu�nderndeLeistung.updateLeistung(id_strecke, bezeichnung, datum, geschwindigkeit);
		setChanged();
		notifyObservers(this);
	}

	public String getSpeicherpfad() {
		return speicherpfad;
	}

	public void setSpeicherpfad(String speicherpfad) {
		this.speicherpfad = speicherpfad;
	}
	
	public boolean equalsWithoutID (Athlet andererAthlet){
		if ( name.equals(andererAthlet.getName())){
				if (andererAthlet.getLeistungen().size() != alleLeistungen.size())
					return false;
				for (int i = 0; i< alleLeistungen.size(); i++){
					Leistung aktuelleLeistung = alleLeistungen.get(i);
					Leistung andereLeistung = andererAthlet.getLeistungen().get(i);
					if (! aktuelleLeistung.equalsWithoutIDs(andereLeistung)){
						return false;
					}
				}
				return true;
		}
		return false;
	}
	
	public boolean isSetSpeicherpfad(){
		if (speicherpfad == "")
			return false;
		return true;
	}

	public void setLeistungToAuswahlForSlopeFaktor(Leistung ausgewaehlteLeistung) 
			throws ThreeLeistungenForSlopeFaktorException, GleicheStreckeException, TooGoodSlopeFaktorException, TooBadSlopeFaktorException {
		if(!inAlleLeistungenEnthalten(ausgewaehlteLeistung)){
			// TODO: throw exception
			return;
		}
		if (getLeistungAuswahlForSlopeFaktor()[1] != null){
			throw new ThreeLeistungenForSlopeFaktorException();
		}
		if (	getLeistungAuswahlForSlopeFaktor()[0] != null && 
				getLeistungAuswahlForSlopeFaktor()[0].getStrecke() == ausgewaehlteLeistung.getStrecke()){
			throw new GleicheStreckeException();
		}		
		for (Leistung aktuelleLeistung: alleLeistungen){
			if(aktuelleLeistung.equals(ausgewaehlteLeistung)){
				if (getLeistungAuswahlForSlopeFaktor()[0] != null){
					// checks: werden nur durchgef�hrt, wenn die zweite Strecke gesetzt werden soll
					double tempSlopeFaktor = slopeFaktorBerechnen(getLeistungAuswahlForSlopeFaktor()[0], aktuelleLeistung);
					checkValidityOfSlopeFaktor(tempSlopeFaktor);
					// throws TooGoodSlopeFaktorException, TooBadSlopeFaktorException
				}
				aktuelleLeistung.setIsUsedForSlopeFaktor(true);
				setChanged();
				notifyObservers(this);
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
				setChanged();
				notifyObservers(this);
			}
		}
	}
	
	public Leistung[] getLeistungAuswahlForSlopeFaktor() {
		Leistung[] LeistungAuswahlForSlopeFaktor = 
				new Leistung[ANZAHL_LEISTUNGEN_F�R_BERECHNUNG_DES_SLOPE_FAKTORS];
		int i = 0;
		for (Leistung aktuelleLeistung: alleLeistungen){
			if (aktuelleLeistung.isUsedForSlopeFaktor()){
				LeistungAuswahlForSlopeFaktor[i] = aktuelleLeistung;
				i++;
			}
		}
		return LeistungAuswahlForSlopeFaktor;
	}
	
	public void resetLeistungAuswahlForSlopeFaktor(){
		for (Leistung aktuelleLeistung: alleLeistungen){
			aktuelleLeistung.setIsUsedForSlopeFaktor(false);
			setChanged();
			notifyObservers(this);
		}
	}
	
	public void setLeistungenAuswahlForSlopeFaktorAutomatisch() 
			throws GleicheStreckeException,Exception {
		// Ein eindeutig bester Slope-Faktor kann nicht immer bestimmt werden, daher wird versucht,
		// 1.) basierend auf der k�rzesten & l�ngsten Strecke einen m�glichst zuverl�ssigen 
		// bzw. falls dieser nicht zul�ssig w�re,
		// 2.) ein zul�ssigen
		// Slope-Faktor zu berechnen.

		Leistung k�rzereStreckenLeistung = alleLeistungen.get(0);
		Leistung l�ngereStreckenLeistung = alleLeistungen.get(1);
		if (k�rzereStreckenLeistung == null || l�ngereStreckenLeistung == null)
			throw new Exception();
		for (Leistung aktuelleLeistung : alleLeistungen){
			if (aktuelleLeistung.getStrecke() < k�rzereStreckenLeistung.getStrecke())
				k�rzereStreckenLeistung = aktuelleLeistung;
		}
		for (Leistung aktuelleLeistung : alleLeistungen){
			if (aktuelleLeistung.getStrecke() > l�ngereStreckenLeistung.getStrecke())
				l�ngereStreckenLeistung = aktuelleLeistung;
		}
		resetLeistungAuswahlForSlopeFaktor();
		try {
			// TODO: falls too good/bad slope-Faktoren resultieren w�rden, sollten andere Leistungen ber�cksichtigt werden!
			
			setLeistungToAuswahlForSlopeFaktor(k�rzereStreckenLeistung);
			setLeistungToAuswahlForSlopeFaktor(l�ngereStreckenLeistung);
			setChanged();
			notifyObservers(this);
		} catch (Exception e) {
			// TODO Auto-generated catch block
		}

	}
		
	private void requireSlopeFaktor() throws SlopeFaktorNotSetException{
		try{
			triggerCalculations();
			checkValidityOfSlopeFaktor(slopeFaktor);
		} catch (Exception e){
			throw new SlopeFaktorNotSetException();
		}
	}
	
	public LinkedList<Leistung> getMoeglicheBestzeitenListe () throws SlopeFaktorNotSetException { 
		requireSlopeFaktor();

		LinkedList<Leistung> bestzeitenListe = new LinkedList<Leistung>();
		for (int i = 0; i < Strecken.getStreckenArrayLength(); i++) { 
			int entfernung = Strecken.getStreckenlaengeById(i); 
			double bestzeit = calculateTime(entfernung);
			String zeitString = leistungHelper.parseSecInZeitstring(bestzeit);
			// -1: m�gliche Bestzeiten werden dem Athleten nicht direkt zugewiesen
			double geschwindigkeit = leistungHelper.berechneGeschwindigkeit(entfernung, zeitString);
			bestzeitenListe.add(new Leistung (i,-1,null,null, geschwindigkeit)); 
		} 
		return bestzeitenListe; 
	}
	
	public double getSpeedSecondsPerKm(double entfernung) throws SlopeFaktorNotSetException {
		requireSlopeFaktor();
		return calculateSpeedSecondsPerKm(entfernung);
	}
	
	private double calculateSpeedSecondsPerKm(double entfernung) throws SlopeFaktorNotSetException {
		Leistung referenzLeistung = getLeistungAuswahlForSlopeFaktor()[0];
		double referenzGeschwindigkeit = referenzLeistung.getGeschwindigkeit();
		double referenzEntfernung = referenzLeistung.getStrecke();
		double speed = referenzGeschwindigkeit + slopeFaktor * (Math.log10(entfernung/referenzEntfernung));
		return speed;
	}
	
	public double getTime (double entfernung) throws SlopeFaktorNotSetException {
		requireSlopeFaktor();
		return calculateTime(entfernung);
	}
	
	private double calculateTime (double entfernung) throws SlopeFaktorNotSetException {
		requireSlopeFaktor();
		
		double kilometerGeschwindigkeit = calculateSpeedSecondsPerKm(entfernung);
		double gesch�tzteGeschwindigkeit = kilometerGeschwindigkeit*(entfernung/1000);	
		return gesch�tzteGeschwindigkeit;
	}
	
	public double getAnaerobeSchwelle() throws SlopeFaktorNotSetException{
		requireSlopeFaktor();
		return anaerobeSchwelle;
	}

	private void triggerCalculations() throws Exception{
		setSlopeFactor();
		estimateThreshold();
	}
	
	private void setSlopeFactor() throws Exception{
		double tempSlopeFaktor = slopeFaktorBerechnen(getLeistungAuswahlForSlopeFaktor());
		checkValidityOfSlopeFaktor(tempSlopeFaktor);
		this.slopeFaktor = tempSlopeFaktor;
	}
	
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
	
	private void estimateThreshold () throws SlopeFaktorNotSetException {		
		double accuracy = 1;
		double timeToSearch = 3600.0;
		double diff;
		double newGuessKm;
		double distanceMeter = 10000;
		do{
			newGuessKm = timeToSearch / (calculateSpeedSecondsPerKm(distanceMeter));
			diff = Math.abs(newGuessKm*1000 - distanceMeter);
			distanceMeter = newGuessKm*1000;
		} while (diff>accuracy);
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
	
	private void checkValidityOfSlopeFaktor(double inputSlopeFaktor) throws TooGoodSlopeFaktorException, TooBadSlopeFaktorException{
		if (inputSlopeFaktor < MINIMUM_VALID_SLOPE_FAKTOR)
			throw new TooGoodSlopeFaktorException();
		if (inputSlopeFaktor > MAXIMUM_VALID_SLOPE_FAKTOR)
			throw new TooBadSlopeFaktorException();
	}
}