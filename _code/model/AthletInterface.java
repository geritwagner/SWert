package model;

import java.util.LinkedList;

interface AthletInterface {

	public long getId();
	public String getName();
	public LinkedList<Leistung> getLeistungen();
	public boolean addLeistung(Leistung leistung);
	public boolean removeLeistung(Leistung leistungToRemove);
		
	
	// Leistungen für Slope-Faktor bearbeiten
	public void setLeistungToAuswahlForSlopeFaktor(Leistung ausgewaehlteLeistung);
	public void removeLeistungFromAuswahlForSlopeFaktor(Leistung ausgewaehlteLeistung);
	public Leistung[] getLeistungAuswahlForSlopeFaktor();

	public boolean isSetSlopeFaktor();
	public String getSlopeFaktorStatus();

	// Berechnete Leistungen
	public LinkedList<Leistung> getMoeglicheBestzeitenListe ();
	public double calculateSpeed (double entfernung);
	public double getAnaerobeSchwelle();
	
}
