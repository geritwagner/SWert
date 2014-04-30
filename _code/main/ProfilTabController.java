package main;

import java.awt.event.*;
import java.io.IOException;
import javax.swing.event.*;
import datei_operationen.*;
import analyse_bestzeiten.*;
import analyse_diagramm.*;
import analyse_trainingsbereich.*;
import leistung_bearbeiten.*;
import model.*;

/**
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta), Gerit Wagner
 */

public class ProfilTabController implements ActionListener, ListSelectionListener{
	
	private Athlet athlet;
	private ProfilTab view;
	private DateiSpeichern speicher;
	private Hauptfenster mainframe = Hauptfenster.aktuellesHauptfenster;
	
	protected ProfilTabController(Athlet athlet, ProfilTab view){
		this.athlet = athlet;
		this.view = view;
	}

	protected void release(){
		view = null;
		athlet = null;
		speicher = null;
	}
	
	protected void leistungBearbeitenPressed(){
		Leistung leistung = view.getLeistungInZeile(view.getSelectedRow());
		new LeistungDialog(athlet, leistung);		
	}
	
	protected void leistungLöschen(Leistung leistung){
		athlet.removeLeistung(leistung);
	}

	protected void auswahlFürSlopeFaktorÄndern(Leistung leistung, boolean setTo) throws ThreeLeistungenForSlopeFaktorException, GleicheStreckeException, TooGoodSlopeFaktorException, TooBadSlopeFaktorException{
		if (setTo == true){
			athlet.setLeistungToAuswahlForSlopeFaktor(leistung);
		} else {
			athlet.removeLeistungFromAuswahlForSlopeFaktor(leistung);
		}		
	}

	protected void automatischAuswählen() throws GleicheStreckeException, Exception{
		athlet.setLeistungenAuswahlForSlopeFaktorAutomatisch();	
	}
	
	protected void speichern(boolean forceSpeichernUnter) throws IOException, NoFileChosenException, SyntaxException{
		if (speicher == null)
			speicher = new DateiSpeichern(athlet);
		speicher.speichern(forceSpeichernUnter);
		mainframe.setSpeicherStatus();		
	}

	public void actionPerformed(ActionEvent arg0) {
		String command = arg0.getActionCommand();
		switch (command){
		case "Leistung hinzufügen":
			view.setLeistungBearbeitenAvailable(false);
			new LeistungDialog(athlet, null);
			if(view.btnBestzeiten.isEnabled() == false){
				view.tryAutomatischeAuswahl();
			}
			break;
		case "Leistung bearbeiten":
			view.leistungBearbeitenPressed();
			break;	
		case "Leistung löschen":
			view.leistungLoeschenPressed();
			break;
		case "":
			view.tabSchließenClicked();
			break;
		case "Automatisch wählen":
			view.LeistungenAutomatischWählenClicked();
			break;
		case "Mögliche Bestzeiten":
			new BestzeitenDialog(athlet);
			break;
		case "Trainingsbereiche":
			new TrainingsbereichDialog(athlet);
			break;
		case "Leistungskurve als Grafik":
			new DiagrammFrame(Hauptfenster.athletenListe);
			break;
		}
	}

	public void valueChanged(ListSelectionEvent arg0) {
    	if (arg0.getValueIsAdjusting()) {
    		return;
        }
        view.setLeistungBearbeitenAvailable(true);
    }
}