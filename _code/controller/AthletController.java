package controller;

import java.util.prefs.Preferences;

import model.Athlet;

/**
 * Controller für alle Anfragen auf das "Athlet"-Model
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta)
 */
public class AthletController{

//----------------------- VARIABLEN -----------------------
	private Preferences pref = Preferences.userRoot().node(this.getClass().getName());
	
	// TODO: gehört eigentlich in Athlet.java

//----------------------- ÖFFENTLICHE METHODEN -----------------------
	/**
	 * Anlegen eines neuen Athleten
	 * @param name: Name des Athleten
	 * @return: Neue angelegtes Objekt des "Ahlet"-Models
	 */
	public Athlet neuerAthlet(String name) {
		long id = getNextId();
		Athlet neuerAthlet = new Athlet(id, name);
		return neuerAthlet;
	}
	
	/**
	 * Anlegen eines neuen Athleten
	 * @param name: Name des Ahtleten
	 * @param id: ID des Athleten
	 * @return: Neue angelegtes Objekt des "Ahlet"-Models
	 */
	public Athlet neuerAthlet(String name, long id) {
		Athlet neuerAthlet = new Athlet(id, name);
		return neuerAthlet;
	}
	
//----------------------- PRIVATE METHODEN -----------------------
	/**
	 * Auslesen der aktuellen Id aus der ids.ini-Datei
	 * @return: aktuelle ID
	 */
	private long getNextId() {
		long idAthlet = 0;
		idAthlet = pref.getLong("ahletId", 1);
		writeId(idAthlet);
		return idAthlet;
	}
	
	/**
	 * Schreiben der aktuelle Id in die Datei
	 * @param id: aktuelle ID
	 */
	private void writeId(long id) {
		pref.put("ahletId", String.valueOf(id+1));
	}
}
