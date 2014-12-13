package model;

import java.util.*;
import datenbank_zugriff.DBAthlet;

/**
 * @author Honors-WInfo-Projekt (Fabian B�hm, Alexander Puchta), Gerit Wagner
 */

public class AthletenListe extends Observable {

	private LinkedList<Athlet> alleAthleten;
	private DBAthlet tableAthlet;
	private static Athlet letzterGeoeffneterAthlet;
	private static Athlet letzterGeschlossenerAthlet;

	public AthletenListe(){
		tableAthlet = new DBAthlet();
		alleAthleten = tableAthlet.holeAlleAthleten();
		setChanged();
		notifyObservers();
	}
	
	public Athlet getLetzterGeoeffneterAthlet() {
		Athlet aktuellerAthlet = letzterGeoeffneterAthlet;
		letzterGeoeffneterAthlet = null;
		return aktuellerAthlet;
	}

	public Athlet getLetzterGeschlossenerAthlet() {
		Athlet aktuellerAthlet = letzterGeschlossenerAthlet;
		letzterGeschlossenerAthlet = null;
		return aktuellerAthlet;
	}

	
	public LinkedList<Athlet> getAlleAthleten(){
		return alleAthleten;
	}
	
	public void addAthlet (Athlet athlet){
		alleAthleten.add(athlet);
		letzterGeoeffneterAthlet = athlet;
		setChanged();
		notifyObservers();
	}
	
	public void removeAthlet (Athlet athlet){
		for (int i = 0; i<alleAthleten.size(); i++){
			if (alleAthleten.get(i).equals(athlet)){
				alleAthleten.remove(i);
				letzterGeschlossenerAthlet = athlet;
				setChanged();
				notifyObservers();				
			}
		}
	}
}