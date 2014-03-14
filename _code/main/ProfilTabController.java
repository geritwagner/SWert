package main;

import analyse_bestzeiten.BestzeitenDialog;
import analyse_diagramm.DiagrammController;
import analyse_trainingsbereich.TrainingsbereichDialog;
import leistung_bearbeiten.LeistungDialog;
import model.*;

public class ProfilTabController {
	
	Athlet athlet;
	ProfilTab view;
	
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
}