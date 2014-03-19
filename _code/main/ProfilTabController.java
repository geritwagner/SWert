package main;

import java.io.IOException;
import datei_operationen.*;
import analyse_bestzeiten.BestzeitenDialog;
import analyse_diagramm.DiagrammFrame;
import analyse_trainingsbereich.TrainingsbereichDialog;
import leistung_bearbeiten.LeistungDialog;
import model.*;

/**
 * @author Honors-WInfo-Projekt (Fabian B�hm, Alexander Puchta), Gerit Wagner
 */

public class ProfilTabController {
	
	Athlet athlet;
	ProfilTab view;
	DateiSpeichern speicher;
	AthletenListe athletenliste;
	
	protected ProfilTabController(AthletenListe athletenliste, Athlet athlet, ProfilTab view){
		this.athlet = athlet;
		this.athletenliste = athletenliste;
		this.view = view;
	}

	protected void release(){
		view = null;
		athlet = null;
	}
	
	//TODO: Button-Pressed Methoden �ber Action-Listener l�sen
	// da f�r das Hauptfenster schon ein Listener hinzugef�gt wurde m�sste man hier evtl. mit einem container!?!? arbeiten
	// ein "weiterleiten" vom HauptfensterController zum ProfilTabController w�re vermutlich zu aufwendig...
	
	public void bestzeitenButtonPressed(){
		new BestzeitenDialog(athlet);
	}

	public void leistungskurveButtonPressed(){
		new DiagrammFrame(athletenliste);
	}
	
	public void trainingsBereichButtonPressed(){
		new TrainingsbereichDialog(athlet);
	}
	
	public void leistungBearbeitenPressed(Leistung leistung) {
		new LeistungDialog(athlet, leistung);
	}

	
	public void neueLeistungButtonPressed(){
		view.setLeistungBearbeitenAvailable(false);
		new LeistungDialog(athlet, null);		
	}
	
	public void leistungL�schen(Leistung leistung){
		athlet.removeLeistung(leistung);
	}

	protected void auswahlF�rSlopeFaktor�ndern(Leistung leistung, boolean setTo) throws ThreeLeistungenForSlopeFaktorException, GleicheStreckeException{
		if (setTo == true){
			athlet.setLeistungToAuswahlForSlopeFaktor(leistung);
		} else {
			athlet.removeLeistungFromAuswahlForSlopeFaktor(leistung);
		}		
	}

	protected void automatischAusw�hlen() throws ThreeLeistungenForSlopeFaktorException, GleicheStreckeException{
		athlet.setLeistungenAuswahlForSlopeFaktorAutomatisch();		
	}
	
	protected void speichern(boolean forceSpeichernUnter) throws IOException, NoFileChosenException, SyntaxException{
		if (speicher == null)
			speicher = new DateiSpeichern(athlet);
		speicher.speichern(forceSpeichernUnter);
		view.setSpeicherStatus(true);				
	}	
}