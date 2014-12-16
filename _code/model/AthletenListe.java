package model;

import java.util.*;
import datenbank_zugriff.DBAthlet;

/**
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta), Gerit Wagner
 */

public class AthletenListe extends Observable {

	private LinkedList<Athlet> alleAthleten;
	private LinkedList<Athlet> alleGeoeffnetenAthleten;
	private DBAthlet tableAthlet;
	private static Athlet letzterGeoeffneterAthlet;
	private static Athlet letzterGeschlossenerAthlet;

	public AthletenListe(){
		tableAthlet = new DBAthlet();
		alleAthleten = tableAthlet.holeAlleAthleten();
		alleGeoeffnetenAthleten = new LinkedList<Athlet>();
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
	
	public LinkedList<Athlet> getAlleGeoeffnetenAthleten(){
		return alleGeoeffnetenAthleten;
	}
	
	public void addAthlet (Athlet athlet){
		alleGeoeffnetenAthleten.add(athlet);
		letzterGeoeffneterAthlet = athlet;
		setChanged();
		notifyObservers();
	}
	
	public void removeAthlet (Athlet athlet){
		for (int i = 0; i<alleGeoeffnetenAthleten.size(); i++){
			if (alleGeoeffnetenAthleten.get(i).equals(athlet)){
				tableAthlet.schliesseAthlet(i);
				alleGeoeffnetenAthleten.remove(i);
				letzterGeschlossenerAthlet = athlet;
				for (int j = 0; j<alleAthleten.size(); j++) {
					if (alleAthleten.get(j).equals(athlet)){
						alleAthleten.get(j).setGeoeffnet(false);
					}
				}
				setChanged();
				notifyObservers();				
			}
		}
	}
}