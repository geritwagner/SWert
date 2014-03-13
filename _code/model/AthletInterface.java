package model;

import java.util.*;

interface AthletInterface {

	public long getId();
	public String getName();
	public LinkedList<Leistung> getLeistungen();
	public boolean addLeistung(Leistung leistung);
	public boolean removeLeistung(Leistung leistungToRemove);
	public Leistung getLeistungById(long id);
	public void updateLeistung(long id_leistung, int id_strecke, String bezeichnung, String datum, double geschwindigkeit);
	
	// Leistungen für Slope-Faktor bearbeiten
	public void setLeistungToAuswahlForSlopeFaktor(Leistung ausgewaehlteLeistung) throws Exception;
	public void removeLeistungFromAuswahlForSlopeFaktor(Leistung ausgewaehlteLeistung);
	public Leistung[] getLeistungAuswahlForSlopeFaktor();
	public void resetLeistungAuswahlForSlopeFaktor();
	public void setLeistungenAuswahlForSlopeFaktorAutomatisch() throws ThreeLeistungenForSlopeFaktorException, GleicheStreckeException;
	
	public boolean isSetSlopeFaktor();
	public String getSlopeFaktorStatus();

	// Berechnete Leistungen
	public LinkedList<Leistung> getMoeglicheBestzeitenListe () throws Exception;
	public double calculateSpeedSecondsPerKm (double entfernung) throws Exception;
	public double calculateTime (double entfernung) throws Exception;
	public double getAnaerobeSchwelle() throws Exception;	
}