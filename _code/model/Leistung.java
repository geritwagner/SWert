package model;

import java.text.*;
import java.util.*;
import java.util.prefs.*;
import controller.Einheitenumrechner;
import helper.LeistungHelper;
import main.Main;

/**
 * Model-Klasse für das "Leistung"-Objekt
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta)
 */
public class Leistung implements LeistungInterface{
	
	private Preferences pref = Preferences.userRoot().node(this.getClass().getName());
	
	// TODO: ggf. später: id_strecke kapseln, beliebige Streckenlängen lösen
	
	private static final int ID_SCHWELLENLEISTUNG = -1;
	
	private long id_leistung;
	//	kein Setter für id_strecke, da auch Zeit, Geschwindigeit etc. geändert werden müssten (Gefahr der Inkonsistenz!)
	private int id_strecke;
	private long id_athlet;
	private double zeit;
	private double geschwindigkeit;
	private String bezeichnung;
	private String datum;
	private boolean selectedForCalculatingSlopeFaktor;
	
	private LeistungHelper leistungHelper = Main.mainFrame.leistungHelper;
	
	public Leistung(int id_strecke, long id_athlet, String bezeichnung, String datum, double geschwindigkeit) {
		this.id_leistung = getNextLeistungId();
		this.id_strecke = id_strecke;
		this.id_athlet = id_athlet;
		this.zeit = leistungHelper.berechneZeit(getStrecke(), geschwindigkeit);
		this.geschwindigkeit = geschwindigkeit;
		//		this.geschwindigkeit = leistungHelper.berechneGeschwindigkeit(getStrecke(), zeitString);
		this.bezeichnung = bezeichnung;
		this.datum = datum;	
		this.selectedForCalculatingSlopeFaktor = false;
	}

	private long getNextLeistungId() {
		long idAthlet = 0;
		idAthlet = pref.getLong("LeistungId", 1);
		writeLeistungId(idAthlet);
		return idAthlet;
	}
	
	private void writeLeistungId(long id) {
		pref.put("LeistungId", String.valueOf(id+1));
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
			return leistungHelper.berechneSchwellenStreckeAusGeschwindigkeit(geschwindigkeit);
		} else {
			return Strecken.getStreckenlaengeById(streckenID);
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

	public void updateLeistung(int id_strecke, double zeit, String bezeichnung, String datum) {
		this.id_strecke = id_strecke;
		this.zeit = zeit;
		String zeitString = leistungHelper.parseSecInZeitstring(zeit);
		this.geschwindigkeit = leistungHelper.berechneGeschwindigkeit(getStrecke(), zeitString);
		this.bezeichnung = bezeichnung;
		this.datum = datum;	
	}
	
	public double getGeschwindigkeit() {
		return geschwindigkeit;
	}

	public String getZeitString() {
		return leistungHelper.parseSecInZeitstring(zeit);
	}
	
	public double getZeit(){
		return this.zeit;
	}
	
	public void setZeitFromString(String inputZeitString) {
		this.zeit = leistungHelper.parseZeitInSec(inputZeitString);
		this.geschwindigkeit = leistungHelper.berechneGeschwindigkeit(getStrecke(), inputZeitString);		
	}

	public void setZeitAndGeschwindigkeit(double inputZeit) {
		this.zeit = inputZeit;
		this.geschwindigkeit = leistungHelper.berechneGeschwindigkeit(getStrecke(), getZeitString());		
	}

	public void setGeschwindigkeitAndGeschwindigkeit(double inputGeschwindigkeit) {
		this.geschwindigkeit = inputGeschwindigkeit;
		this.zeit = leistungHelper.berechneZeit(getStrecke(), inputGeschwindigkeit);
	}

	@Override
	public Object[] getObjectDataForTable() {
		DecimalFormat f = new DecimalFormat("#0.00");
		double geschwindigkeit = this.getGeschwindigkeit();
		double kmH = Einheitenumrechner.toKmH(geschwindigkeit);
		double mS = Einheitenumrechner.toMS(geschwindigkeit);
		
		int streckenID = this.getId_strecke();
		String streckenString;
		if(streckenID == -1) {
			int strecke = leistungHelper.berechneSchwellenStreckeAusGeschwindigkeit(geschwindigkeit);
			DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.GERMANY);
			streckenString = formatter.format(strecke);
			streckenString = streckenString+"m";
		} else {
			streckenString = Strecken.getStreckenlaengeStringById(streckenID);
		}
		
		Object[] daten = {this.getDatum(),
						  streckenString,
						  this.getBezeichnung(),
						  this.getZeitString(),
						  f.format(kmH),
						  f.format(mS),
						  leistungHelper.parseSecInMinutenstring(geschwindigkeit),
						  new Boolean(false),
						  new Integer(this.getId_strecke()),
						  String.valueOf(this.getGeschwindigkeit())};
		return daten;
	}

	public String getStreckenString() {
		return Strecken.getStreckenlaengeStringById(getId_strecke());
	}
}