package model;

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
	
//----------------------- KONSTRUKTOREN -----------------------
	public Leistung(int id_strecke, long id_athlet, double geschwindigkeit, String bezeichnung, String datum) {
		this.id_strecke = id_strecke;
		this.id_athlet = id_athlet;
		this.geschwindigkeit = geschwindigkeit;
		this.bezeichnung = bezeichnung;
		this.datum = datum;		
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

	public boolean getBerechnungSlopeFaktor() {
		return berechnungSlopeFaktor;
	}

	public void setBerechnungSlopeFaktor(boolean berechnungSlopeFaktor) {
		this.berechnungSlopeFaktor = berechnungSlopeFaktor;
	}

}
