package main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;

import leistung_bearbeiten.LeistungDialog;
import model.AthletenListe;

public class HauptfensterController extends WindowAdapter implements ActionListener{

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
			case "Neues Athletenprofil anlegen": 	new NeuerAthletDialog(athletenListe);	
			case "Athletenprofil öffnen": 			view.dateiOeffnenClicked();
			case "Athletenprofil schließen":		view.tabSchließenClicked();
			case "Speichern":						view.speichernClicked(false);
			case "Speichern unter...":				view.speichernClicked(true);
			case "S-Wert 3.0 schließen":			view.programmSchliessen();
			case "Leistung hinzufügen":				new LeistungDialog(tab.getAthlet(), null);
			case "Leistung bearbeiten":				tab.leistungBearbeitenPressed();
			case "Leistung löschen":				tab.deleteZeileButtonPressed(); // Exception: bitte ein Tab wählen!??!
		}
	}	
}