package model;

import java.text.*;
import java.util.*;
import java.util.prefs.*;
import globale_helper.*;


/**
 * @author Honors-WInfo-Projekt (Fabian B�hm, Alexander Puchta), Gerit Wagner
 */

public class Leistung implements LeistungInterface{
	
	private Preferences pref = Preferences.userRoot().node(this.getClass().getName());

	private static final int ID_SCHWELLENLEISTUNG = -1;
	
	private long id_leistung;
	//	kein Setter f�r id_strecke, da auch Zeit, Geschwindigeit etc. ge�ndert werden m�ssten (Gefahr der Inkonsistenz!)
	private int id_strecke;
	private long id_athlet;
	private double zeit;
	private double geschwindigkeit;
	private String bezeichnung;
	private String datum;
	private boolean selectedForCalculatingSlopeFaktor;
	
	private LeistungHelper leistungHelper;
	
	public Leistung(int id_strecke, long id_athlet, String bezeichnung, String datum, double geschwindigkeit) {
		leistungHelper = new LeistungHelper();
		this.id_leistung = getNextLeistungId();
		this.id_strecke = id_strecke;
		this.id_athlet = id_athlet;
		if (id_strecke == ID_SCHWELLENLEISTUNG){
			this.zeit = 3600;
		} else {
			int strecke = Strecken.getStreckenlaengeById(id_strecke);
			LeistungHelper leistungHelper = new LeistungHelper();
			this.zeit = leistungHelper.berechneZeitInSec(strecke, geschwindigkeit);			
		}
		this.geschwindigkeit = geschwindigkeit;
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
		return this.id_leistung;
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

	public String getStreckenString() {
		return Strecken.getStreckenlaengeStringById(getId_strecke());
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

	protected void setIsUsedForSlopeFaktor(boolean berechnungSlopeFaktor) {
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
			this.bezeichnung.equals(andereLeistung.getBezeichnung()) &&
			this.datum.equals(andereLeistung.getDatum())) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean equalsWithoutIDs (Leistung andereLeistung){
		if (this.id_strecke 		== andereLeistung.getId_strecke() &&
			this.geschwindigkeit 	== andereLeistung.getGeschwindigkeit() &&
			this.bezeichnung.equals(andereLeistung.getBezeichnung()) &&
			this.datum.equals(andereLeistung.getDatum())) {
			return true;
		} else {
			return false;
		}
	}

	public void updateLeistung(int id_strecke, String bezeichnung, String datum, double geschwindigkeit) {
		this.id_strecke = id_strecke;
		if (id_strecke == ID_SCHWELLENLEISTUNG){
			this.zeit = 3600;
		} else {
			int strecke = Strecken.getStreckenlaengeById(id_strecke);
			LeistungHelper leistungHelper = new LeistungHelper();
			this.zeit = leistungHelper.berechneZeitInSec(strecke, geschwindigkeit);			
		}
		this.geschwindigkeit = geschwindigkeit;
		String zeitString = leistungHelper.parseSecInZeitstring(zeit);
		this.geschwindigkeit = leistungHelper.berechneGeschwindigkeit(getStrecke(), zeitString);
		this.bezeichnung = bezeichnung;
		this.datum = datum;	
	}
	
	public double getGeschwindigkeit() {
		return geschwindigkeit;
	}

	public String getZeitString() {
		LeistungHelper leistungHelper = new LeistungHelper();
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
		this.zeit = leistungHelper.berechneZeitInSec(getStrecke(), inputGeschwindigkeit);
	}

	@Override
	public Object[] getObjectDataForTable() {
		LeistungHelper leistungHelper = new LeistungHelper();
		DecimalFormat f = new DecimalFormat("#0.00");
		double geschwindigkeit = this.getGeschwindigkeit();
		double kmH = UnitsHelper.toKmH(geschwindigkeit);
		double mS = UnitsHelper.toMS(geschwindigkeit);
		
		int streckenID = this.getId_strecke();
		String streckenString;
		if(streckenID == ID_SCHWELLENLEISTUNG) {
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
						  this.isUsedForSlopeFaktor(),
						  new Integer(this.getId_strecke()),
						  String.valueOf(this.getGeschwindigkeit())};
		return daten;
	}
}