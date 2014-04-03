package main;

import java.awt.event.*;
import java.io.IOException;
import javax.swing.event.*;
import datei_operationen.*;
import analyse_bestzeiten.BestzeitenDialog;
import analyse_diagramm.DiagrammFrame;
import analyse_trainingsbereich.TrainingsbereichDialog;
import leistung_bearbeiten.LeistungDialog;
import model.*;

/**
 * @author Honors-WInfo-Projekt (Fabian B�hm, Alexander Puchta), Gerit Wagner
 */

public class ProfilTabController implements ActionListener, ListSelectionListener{
	
	private Athlet athlet;
	private ProfilTab view;
	private DateiSpeichern speicher;
	private AthletenListe athletenliste;
	private Hauptfenster mainframe = Hauptfenster.aktuellesHauptfenster;
	
	protected ProfilTabController(AthletenListe athletenliste, Athlet athlet, ProfilTab view){
		this.athlet = athlet;
		this.athletenliste = athletenliste;
		this.view = view;
	}

	protected void release(){
		view = null;
		athlet = null;
		athletenliste = null;
		speicher = null;
	}
	
	protected void leistungBearbeitenPressed(){
		Leistung leistung = view.getLeistungInZeile(view.getSelectedRow());
		new LeistungDialog(athlet, leistung);		
	}
	
	protected void leistungL�schen(Leistung leistung){
		athlet.removeLeistung(leistung);
	}

	protected void auswahlF�rSlopeFaktor�ndern(Leistung leistung, boolean setTo) throws ThreeLeistungenForSlopeFaktorException, GleicheStreckeException, TooGoodSlopeFaktorException, TooBadSlopeFaktorException{
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
		mainframe.setSpeicherStatus();		
	}

	public void actionPerformed(ActionEvent arg0) {
		String command = arg0.getActionCommand();
		switch (command){
		case "Leistung hinzuf�gen":
			view.setLeistungBearbeitenAvailable(false);
			new LeistungDialog(athlet, null);
			break;
		case "Leistung bearbeiten":
			view.leistungBearbeitenPressed();
			break;	
		case "Leistung l�schen":
			view.leistungLoeschenPressed();
			break;
		case "":
			view.tabSchlie�enClicked();
			break;
		case "Leistungen automatisch w�hlen":
			view.checkboxLeistungenAutomatischW�hlenClicked();
			break;
		case "M�gliche Bestzeiten":
			new BestzeitenDialog(athlet);
			break;
		case "Trainingsbereiche":
			new TrainingsbereichDialog(athlet);
			break;
		case "Leistungskurve als Grafik":
			new DiagrammFrame(athletenliste);
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