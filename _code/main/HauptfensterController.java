package main;

import java.awt.event.*;

import javax.swing.event.*;

import datei_operationen.DateiOeffnen;
import leistung_bearbeiten.LeistungDialog;
import model.*;

/**
 * @author Honors-WInfo-Projekt (Fabian B�hm, Alexander Puchta), Gerit Wagner
 */

public class HauptfensterController extends WindowAdapter implements ActionListener, ChangeListener{

	private AthletenListe athletenListe;
	private Hauptfenster view;
	
	protected HauptfensterController (AthletenListe athletenListe, Hauptfenster main){
		this.athletenListe = athletenListe;
		this.view = main;
	}
	
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		ProfilTab tab = view.getAktivesTab();
		switch (command){
			case "Neues Athletenprofil anlegen": 				
				new NeuerAthletDialog(athletenListe);
				break;
			case "Athletenprofil �ffnen": 						
				view.dateiOeffnenClicked();
				break;
			case "Athletenprofil schlie�en":					
				tab.tabSchlie�enClicked();
				break;
			case "Speichern":									
				tab.speichernClicked(false);
				break;
			case "Speichern unter...":							
				tab.speichernClicked(true);
				break;
			case "S-Wert schlie�en":							
				view.fensterSchlie�en();
				break;
			case "Leistung hinzuf�gen":							
				new LeistungDialog(tab.getAthlet(), null);
				break;
			case "Leistung bearbeiten":							
				tab.leistungBearbeitenPressed();
				break;
			case "Leistung l�schen":							
				tab.leistungLoeschenPressed();
				break;
			case "About":							
				new About();
				break;
		}
	}
	
	protected boolean isAktuellerAthletGespeichert (){
		Athlet aktuellerAthlet = view.getAktivesTab().getAthlet();
		DateiOeffnen dateiOeffnen = new DateiOeffnen();
		try {
			dateiOeffnen.setCSVPfad(aktuellerAthlet.getSpeicherpfad());
			Athlet gespeicherterAthlet = dateiOeffnen.getAthlet();
			if (aktuellerAthlet.equalsWithoutID(gespeicherterAthlet))
				return true;
		} catch (Exception e) {
			// nothing
		}
		return false;
	}

    public void windowClosing(WindowEvent we) {
        view.fensterSchlie�en();
    }
    
    protected void release(){
    	athletenListe = null;
    	view = null;
    }

	public void stateChanged(ChangeEvent arg0) {
		int alleTabs = view.getAnzahlTabs();
		int selectedTab = view.getIndexSelectedTab();
		if(selectedTab != alleTabs-1) {
			ProfilTab tab = view.getAktivesTab();
			if (tab.isSelectedLeistung()){
				tab.setLeistungBearbeitenAvailable(true);
			} else {
				tab.setLeistungBearbeitenAvailable(false);
			}
			view.athletenMen�Verf�gbar(true);
		} else {
			view.setLeistungenMen�Verf�gbar(false);
			view.athletenMen�Verf�gbar(false);
		}
	}	
}