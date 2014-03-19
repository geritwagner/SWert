package main;

import java.awt.event.*;
import javax.swing.event.*;
import leistung_bearbeiten.LeistungDialog;
import model.*;

/**
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta), Gerit Wagner
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
			case "Athletenprofil öffnen": 						
				view.dateiOeffnenClicked();
				break;
			case "Athletenprofil schließen":					
				tab.tabSchließenClicked();
				break;
			case "Speichern":									
				tab.speichernClicked(false);
				break;
			case "Speichern unter...":							
				tab.speichernClicked(true);
				break;
			case "S-Wert schließen":							
				view.fensterSchließen();
				break;
			case "Leistung hinzufügen":							
				new LeistungDialog(tab.getAthlet(), null);
				break;
			case "Leistung bearbeiten":							
				tab.leistungBearbeitenPressed();
				break;
			case "Leistung löschen":							
				tab.deleteZeileButtonPressed();
				break;
		}
	}

    public void windowClosing(WindowEvent we) {
        view.fensterSchließen();
    }
    
    protected void release(){
    	athletenListe = null;
    	view = null;
    }

	public void stateChanged(ChangeEvent arg0) {
		int alleTabs = view.getAnzahlTabs();
		int selectedTab = view.getIndexSelectedTab();
		if(selectedTab != alleTabs-1) {
			// TODO hier müsste noch setLeistungenMenüVerfügbar auf true oder false gesetzt werden!!
			view.athletenMenüVerfügbar(true);
		} else {
			view.setLeistungenMenüVerfügbar(false);
			view.athletenMenüVerfügbar(false);
		}
	}	
}