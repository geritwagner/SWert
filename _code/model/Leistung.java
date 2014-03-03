package model;

import main.Main;
import controller.LeistungController;
import controller.StreckenController;

/**
 * Model-Klasse für das "Leistung"-Objekt
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta)
 */
public class Leistung implements LeistungInterface{
	
//----------------------- VARIABLEN -----------------------
	private long id_leistung;
	private int id_strecke;
	private long id_athlet;
	private double zeit;
	private double geschwindigkeit;
	private String bezeichnung;
	private String datum;
	private boolean selectedForCalculatingSlopeFaktor;

	private StreckenController streckenController = Main.mainFrame.streckenController;
	private LeistungController leistungController = Main.mainFrame.leistungController;
	
//----------------------- KONSTRUKTOREN -----------------------
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

//----------------------- ÖFFENTLICHE METHODEN -----------------------	
	
	public long getId() {
		return id_leistung;
	}
	
	public int getId_strecke() {
		return id_strecke;
	}

	public void setId_strecke(int id_strecke) {
		this.id_strecke = id_strecke;
	}
	
	public long getId_athlet() {
		return id_athlet;
	}
	
	public int getStrecke() {
		int streckenID = this.getId_strecke();
		double geschwindigkeit = this.getGeschwindigkeit();
		if(streckenID == -1) {
			return leistungController.berechneStreckeAusGeschwindigkeit(geschwindigkeit);
		} else {
			return streckenController.getStreckenlaengeById(streckenID);
		}
	}

	public String getBezeichnung() {
		return bezeichnung;
	}

	public void setBezeichnung(String bezeichnung) {
		this.bezeichnung = bezeichnung;
	}

	public String getDatum() {
		return datum;
	}

	public void setDatum(String datum) {
		this.datum = datum;
	}

	public boolean isUsedForSlopeFaktor() {
		return selectedForCalculatingSlopeFaktor;
	}

	public void setIsUsedForSlopeFaktor(boolean berechnungSlopeFaktor) {
		this.selectedForCalculatingSlopeFaktor = berechnungSlopeFaktor;
	}

	public String toString(){
		return getId_strecke() + "; " + getStrecke() + ";" + getGeschwindigkeit() + ";" + 
				getBezeichnung() + ";" + getDatum() + ";" + ";" + getZeitString(); 
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

	public void setZeit(double inputZeit) {
		this.zeit = inputZeit;
		this.geschwindigkeit = leistungController.berechneGeschwindigkeit(getStrecke(), getZeitString());		
	}

	public void setGeschwindigkeit(double inputGeschwindigkeit) {
		this.geschwindigkeit = inputGeschwindigkeit;
		this.zeit = leistungController.berechneZeit(getStrecke(), inputGeschwindigkeit);
	}
}
