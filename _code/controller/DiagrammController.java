package controller;

import java.util.Iterator;
import java.util.LinkedList;

import main.Main;
import model.Athlet;
import model.Leistung;

import org.jfree.data.xy.XYSeries;

import view.DiagrammFrame;
import view.ProfilTab;

/**
 * Controller für alle Aktionen, die den LeistungsDialog betreffen
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta)
 */
public class DiagrammController {
	
//----------------------- VARIABLEN -----------------------
	private DiagrammFrame diagramm;
	private String letzterAthlet;

//----------------------- KONSTRUKTOREN -----------------------
	public DiagrammController() {
		diagramm = new DiagrammFrame();
	}

//----------------------- ÖFFENTLICHE METHODEN -----------------------
	/**
	 * Anzeigen des DiagrammFrames
	 */
	public void DiagrammOeffnen() {		
		diagramm.setEnabled(true);
		openAllAthletes();
		diagramm.setVisible(true);
	}
	
	/**
	 * Schließen des DiagrammFrames
	 */
	public void DiagrammSchließen() {
		diagramm.setEnabled(false);
		diagramm.dispose();
		diagramm = new DiagrammFrame();
	}
	
//----------------------- PRIVATE METHODEN -----------------------
	
	public void openAllAthletes(){
		int countAthletes = Main.mainFrame.tabbedPane.getTabCount() - 1;
		System.out.println(countAthletes);
		for(int i = 0;i<countAthletes; i++){
			ProfilTab tab = (ProfilTab) Main.mainFrame.tabbedPane.getComponentAt(i);
			addAthletBerechneteLeistungsKurve(tab.getAthlet());	
			// TODO: statt getLeistungen müssten vermutlich die 2 Punkte, auf deren Grundlage die Schwelle
			// berechnet wird, zurückgegeben werden...
			addBestzeiten(tab.getAthlet().getLeistungen());
		}
;	}
	
	/**
	 * Hinzufügen aller tatsächlich erbrachten Leistungen eines Athleten
	 * zum Diagramm
	 * @param athlet
	 */
	public void addAthletBerechneteLeistungsKurve(Athlet athlet) {
		LinkedList<Leistung> leistungen = athlet.getLeistungen();
		Iterator<Leistung> leistungenIterator = leistungen.iterator();
		letzterAthlet = athlet.getName();
		final XYSeries athletenSerie = new XYSeries(letzterAthlet);
		while (leistungenIterator.hasNext()) {
			Leistung leistung = leistungenIterator.next();
			double strecke = leistung.getStrecke();
			double geschwindigkeit = leistung.getGeschwindigkeit();
			double zeit = Einheitenumrechner.toMinKm(geschwindigkeit);
			athletenSerie.add(strecke, zeit);
		}
		diagramm.addLeistungsSerie(athletenSerie);
	}

	/**
	 * Hinzufügen der möglichen Bestleistungen eines Athleten
	 * für die verschiedenen Streckenlängen zum Diagramm
	 * @param athlet
	 */
	public void addBestzeiten (LinkedList<Leistung> bestzeiten) {
		Iterator<Leistung> leistungenIterator = bestzeiten.iterator();
		final XYSeries athletenSerie = new XYSeries(letzterAthlet);
		while (leistungenIterator.hasNext()) {
			Leistung leistung = leistungenIterator.next();
			int strecke = Main.streckenController.getStreckenlaengeById(leistung.getId_strecke());
			double geschwindigkeit = leistung.getGeschwindigkeit();
			double zeit = Einheitenumrechner.toMinKm(geschwindigkeit);
			athletenSerie.add(strecke, zeit);
		}
		diagramm.addBestzeitenSerie(athletenSerie);
	}
}
