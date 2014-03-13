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
}