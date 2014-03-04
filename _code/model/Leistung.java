package model;

import main.Main;
import controller.LeistungController;
import controller.StreckenController;

/**
 * Model-Klasse für das "Leistung"-Objekt
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta)
 */
public class Leistung implements LeistungInterface{

	private long id_leistung;
//	kein Setter für id_strecke, da auch Zeit, Geschwindigeit etc. geändert werden müssten (Gefahr der Inkonsistenz!)
	private int id_strecke;
	private long id_athlet;
	private double zeit;
	private double geschwindigkeit;
	private String bezeichnung;
	private String datum;
	private boolean selectedForCalculatingSlopeFaktor;
	
	private static final int ID_SCHWELLENLEISTUNG = -1;
	
	private StreckenController streckenController = Main.mainFrame.streckenController;
	private LeistungController leistungController = Main.mainFrame.leistungController;
	
	public Leistung(int id_strecke, long id_athlet, double zeit, String bezeichnung, String datum) {
		this.id_strecke = id_strecke;
		this.id_athlet = id_athlet;
		this.zeit = zeit;
		String zeitString = leistungController.parseSecInZeitstring(zeit);
		this.geschwindigkeit = leistungController.berechneGeschwindigkeit(getStrecke(), zeitString);
		this.bezeichnung = bezeichnung;
		this.datum = datum;	
		this.selectedForCalculatingSlopeFaktor = false;
	}
	
	public long getId() {
		return id_leistung;
	}
	
	public int getId_strecke() {
		return id_strecke;
	}
	
	public long getId_athlet() {
		return id_athlet;
	}
	
	public int getStrecke() {
		int streckenID = this.getId_strecke();
		double geschwindigkeit = this.getGeschwindigkeit();
		if(streckenID == ID_SCHWELLENLEISTUNG) {
			return leistungController.berechneSchwellenStreckeAusGeschwindigkeit(geschwindigkeit);
		} else {
			return streckenController.getStreckenlaengeById(streckenID);
		}
	}

	public String getBezeichnung() {
		return bezeichnung;
	}

	public String getDatum() {
		return datum;
	}

	public boolean isUsedForSlopeFaktor() {
		return selectedForCalculatingSlopeFaktor;
	}

	public void setIsUsedForSlopeFaktor(boolean berechnungSlopeFaktor) {
		this.selectedForCalculatingSlopeFaktor = berechnungSlopeFaktor;
	}

	public String toString(){
		return getId_strecke() + "; " + getStrecke() + "; " + getZeit() + "(= " + getZeitString() + "); " + 
				getBezeichnung() + "; " + getDatum() + "; geschwindigkeit= " + getGeschwindigkeit() + ";" ; 
	}
	
	public boolean equals(Leistung andereLeistung){
		if (this.id_strecke 		== andereLeistung.getId_strecke() &&
			this.id_athlet 			== andereLeistung.getId_athlet() &&
			this.geschwindigkeit 	== andereLeistung.getGeschwindigkeit() &&
			this.bezeichnung 		== andereLeistung.getBezeichnung() &&
			this.datum 				== andereLeistung.getDatum()) {
			return true;
		} else {
			return false;
		}
	}


	
	public double getGeschwindigkeit() {
		return geschwindigkeit;
	}

	public String getZeitString() {
		return leistungController.parseSecInZeitstring(zeit);
	}
	
	public double getZeit(){
		return this.zeit;
	}
	

	
	public void setZeitFromString(String inputZeitString) {
		this.zeit = leistungController.parseZeitInSec(inputZeitString);
		this.geschwindigkeit = leistungController.berechneGeschwindigkeit(getStrecke(), inputZeitString);		
	}

	public void setZeitAndGeschwindigkeit(double inputZeit) {
		this.zeit = inputZeit;
		this.geschwindigkeit = leistungController.berechneGeschwindigkeit(getStrecke(), getZeitString());		
	}

	public void setGeschwindigkeitAndGeschwindigkeit(double inputGeschwindigkeit) {
		this.geschwindigkeit = inputGeschwindigkeit;
		this.zeit = leistungController.berechneZeit(getStrecke(), inputGeschwindigkeit);
	}
}
