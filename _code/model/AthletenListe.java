package model;

import java.util.*;

/**
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta)
 */

public class AthletenListe extends Observable {

	private LinkedList<Athlet> alleAthleten;
	private Athlet letzterGeoeffneterAthlet;
	private Athlet letzterGeschlossenerAthlet;

	public AthletenListe(){
		alleAthleten = new LinkedList<>();
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