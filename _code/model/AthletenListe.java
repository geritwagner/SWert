package model;

import java.util.*;

public class AthletenListe extends Observable {

	LinkedList<Athlet> alleAthleten;
	Athlet letzterGeoeffneterAthlet;
	Athlet letzterGeschlossenerAthlet;
	
	//TODO: attributes to manage: active (tab), saved (status), editable (~Leistung ausgew�hlt)
	
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

	public AthletenListe(){
		alleAthleten = new LinkedList<>();
		setChanged();
		notifyObservers();
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