package main;

import java.io.IOException;

import datei_operationen.DateiSpeichernCSVController;
import datei_operationen.DateiSpeichern;
import analyse_bestzeiten.BestzeitenDialog;
import analyse_diagramm.DiagrammController;
import analyse_trainingsbereich.TrainingsbereichDialog;
import leistung_bearbeiten.LeistungDialog;
import model.*;

public class ProfilTabController {
	
	Athlet athlet;
	ProfilTab view;
	DateiSpeichern speicher;
	
	protected ProfilTabController(Athlet athlet, ProfilTab view){
		this.athlet = athlet;
		this.view = view;
	}

	protected void release(){
		view = null;
		athlet = null;
	}
	
	public void bestzeitenButtonPressed(){
		new BestzeitenDialog(athlet);
	}

	public void leistungskurveButtonPressed(){
		// TODO: call DiagrammFrame!!!
		new DiagrammController();
	}
	
	public void trainingsBereichButtonPressed(){
		new TrainingsbereichDialog(athlet);
	}
	
	public void leistungBearbeitenPressed(Leistung leistung) {
		new LeistungDialog(athlet, leistung);
	}

	
	public void neueLeistungButtonPressed(){
		view.setBearbeitenStatus(false);
		new LeistungDialog(athlet, null);		
	}
	
	public void leistungLöschen(Leistung leistung){
		athlet.removeLeistung(leistung);
	}

	protected void auswahlFürSlopeFaktorÄndern(Leistung leistung, boolean setTo) throws ThreeLeistungenForSlopeFaktorException, GleicheStreckeException{
		if (setTo == true){
			athlet.setLeistungToAuswahlForSlopeFaktor(leistung);
		} else {
			athlet.removeLeistungFromAuswahlForSlopeFaktor(leistung);
		}		
	}

	protected void automatischAuswählen() throws ThreeLeistungenForSlopeFaktorException, GleicheStreckeException{
		athlet.setLeistungenAuswahlForSlopeFaktorAutomatisch();		
	}

	protected void speichernUnter(boolean forceSpeichernUnter) throws IOException{
		if (speicher == null)
			speicher = new DateiSpeichern("");
		if (!speicher.isSetPfad())
			speicher.setPfadFromUserDialog(athlet.getName());

		// TODO: Pfad sollte beim öffnen bzw. bei speichern/UNTER über den 
		// "View" DateiPfadSpeichern im entsprechenden Controller gesetzt werden!!!!
		speicher.speichern(athlet);
		view.setSpeicherStatus(true);				
	}	
}