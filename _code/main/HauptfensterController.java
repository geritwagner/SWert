package main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import leistung_bearbeiten.LeistungDialog;
import model.AthletenListe;

public class HauptfensterController extends WindowAdapter implements ActionListener, ChangeListener{

	private AthletenListe athletenListe;
	protected Hauptfenster view;
	
	public HauptfensterController (AthletenListe athletenListe, Hauptfenster main){
		this.athletenListe = athletenListe;
		this.view = main;
	}
	
	@Override
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
				view.release();
				break;
			case "Leistung hinzufügen":							
				new LeistungDialog(tab.getAthlet(), null);
				break;
			case "Leistung bearbeiten":							
				tab.leistungBearbeitenPressed();
				break;
			case "Leistung löschen":							
				tab.deleteZeileButtonPressed(); // Exception: bitte ein Tab wählen!??!
				break;
		}
	}

    public void windowClosing(WindowEvent we) {
        view.release();
    }

	@Override
	public void stateChanged(ChangeEvent arg0) {
		int alleTabs = view.tabbedPane.getTabCount();
		int selectedTab = view.tabbedPane.getSelectedIndex();
		if(selectedTab != alleTabs-1) {
			view.selectedIndex = selectedTab;
			view.athletenMenüVerfügbar(true);
		} else {
			view.setLeistungenMenüVerfügbar(false);
			view.athletenMenüVerfügbar(false);
		}
	}	
}