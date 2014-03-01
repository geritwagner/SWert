package model;

import main.Main;
import controller.LeistungController;
import controller.StreckenController;

/**
 * Model-Klasse für das "Leistung"-Objekt
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta)
 */
public class Leistung{
	
//----------------------- VARIABLEN -----------------------
	private long id;
	private int id_strecke;
	private long id_athlet;
	private String zeit;
	private double geschwindigkeit;
	private String bezeichnung;
	private String datum;
	private boolean berechnungSlopeFaktor;

	private StreckenController streckenController = Main.mainFrame.streckenController;
	private LeistungController leistungController = Main.mainFrame.leistungController;
	
//----------------------- KONSTRUKTOREN -----------------------
	public Leistung(int id_strecke, long id_athlet, double geschwindigkeit, String bezeichnung, String datum) {
		this.id_strecke = id_strecke;
		this.id_athlet = id_athlet;
		this.geschwindigkeit = geschwindigkeit;
		this.bezeichnung = bezeichnung;
		this.datum = datum;		
	}

//----------------------- ÖFFENTLICHE METHODEN -----------------------	
	
	/**
	 * Auslesen der gegebenen Streckenlänge einer Strecke
	 * @param leistung
	 * @return: Double-Wert der Streckenlänge
	 */
	public double getStrecke() {
		int streckenID = this.getId_strecke();
		double geschwindigkeit = this.getGeschwindigkeit();
		if(streckenID == -1) {
			return leistungController.berechneStreckeAusGeschwindigkeit(geschwindigkeit);
		} else {
			return streckenController.getStreckenlaengeById(streckenID);
		}
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
	
//----------------------- GETTER UND SETTER -----------------------
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
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

	public void setId_athlet(long id_athlet) {
		this.id_athlet = id_athlet;
	}

	public String getZeit() {
		return zeit;
	}
	
	public void setZeit(String zeit) {
		this.zeit = zeit;
	}
	
	public double getGeschwindigkeit() {
		return geschwindigkeit;
	}

	public void setGeschwindigkeit(double geschwindigkeit) {
		this.geschwindigkeit = geschwindigkeit;
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
		return berechnungSlopeFaktor;
	}

	public void setIsUsedForSlopeFaktor(boolean berechnungSlopeFaktor) {
		this.berechnungSlopeFaktor = berechnungSlopeFaktor;
	}

	public String toString(){
		return getId_strecke() + "; " + getStrecke() + ";" + getGeschwindigkeit() + ";" + getBezeichnung() + ";" + getDatum() + ";"; 
	}
}
