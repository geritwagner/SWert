package model;

import java.util.LinkedList;

/**
 * Model-Klasse für das "Athlet"-Objekt
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta)
 */
public class Athlet {

//----------------------- VARIABLEN -----------------------
	private long id;
	private String name;
	private double slopeFaktor;
	private LinkedList<Leistung> leistungen = new LinkedList<Leistung>();
	
//----------------------- KONSTRUKTOREN -----------------------
	public Athlet(long id, String name) {
		this.id = id;
		this.name = name;		
	}
	
	//----------------------- GETTER UND SETTER -----------------------
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getSlopeFaktor() {
		return slopeFaktor;
	}
	
	public void setSlopeFaktor(double slopeFaktor) {
		this.slopeFaktor = slopeFaktor;
	}
	
	public LinkedList<Leistung> getLeistungen() {
		return leistungen;
	}
	
	public boolean addLeistung(Leistung leistung) {
		leistungen.add(leistung);
		return true;
	}
	
	public void resetLeistungen() {
		leistungen = new LinkedList<Leistung>();
	}
}
