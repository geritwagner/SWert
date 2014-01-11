package controller;

import java.util.Iterator;
import java.util.LinkedList;

import main.Main;
import model.Athlet;
import model.Leistung;

import org.jfree.data.xy.XYSeries;

import view.DiagrammFrame;

/**
 * Controller zum Handlen aller Aktionen, die den LeistungsDialog betreffen
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
	
	/**
	 * Hinzufügen aller tatsächlich erbrachten Leistungen eines Athleten
	 * zum Diagramm
	 * @param athlet
	 */
	public void addAthletLeistungsKurve(Athlet athlet) {
		LinkedList<Leistung> leistungen = athlet.getLeistungen();
		Iterator<Leistung> leistungenIterator = leistungen.iterator();
		letzterAthlet = athlet.getName();
		final XYSeries athletenSerie = new XYSeries(letzterAthlet);
		while (leistungenIterator.hasNext()) {
			Leistung leistung = leistungenIterator.next();
			double strecke = Main.funktionenController.getStrecke(leistung);
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
	public void addBestzeitenKurve (LinkedList<Leistung> bestzeiten) {
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
