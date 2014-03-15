package model;

import java.util.*;

public class AthletenListe extends Observable {

	LinkedList<Athlet> alleAthleten;
	
	//TODO: attributes to manage: active (tab), saved (status), editable (~Leistung ausgewählt)
	
	public AthletenListe(){
		alleAthleten = new LinkedList<>();
	}
	
	public void addAthlet (Athlet athlet){
		alleAthleten.add(athlet);
		setChanged();
		notifyObservers();
	}
	
	public void removeAthlet (Athlet athlet){
		for (int i = 0; i<alleAthleten.size(); i++){
			if (alleAthleten.get(i).equals(athlet)){
				alleAthleten.remove(i);
				setChanged();
				notifyObservers();				
			}
		}
	}
}