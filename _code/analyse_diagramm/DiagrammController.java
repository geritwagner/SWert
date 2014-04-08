package analyse_diagramm;

import org.jfree.data.xy.XYSeries;
import java.awt.event.*;
import java.util.*;
import globale_helper.*;
import model.*;

/**
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta), Gerit Wagner
 */

public class DiagrammController implements WindowListener {
	
	private DiagrammFrame view;
	private AthletenListe athletenListe;
	
	protected DiagrammController(DiagrammFrame view, AthletenListe athletenListe) {
		this.view = view;
		this.athletenListe = athletenListe;
	}
	
	protected void release(){
		view = null;
		athletenListe = null;
	}

	protected void openAllAthletes(){
		for (Athlet aktuellerAthlet : athletenListe.getAlleAthleten()){
			try {
				plotLeistungen(aktuellerAthlet);	
				plotBerechneteBestzeiten(aktuellerAthlet);
			} catch (SlopeFaktorNotSetException e) {
				e.printStackTrace();
				// Bestzeiten können nur für Athleten berechnet werden, bei denen der Slope-Faktor bekannt ist.
			}
		}
	}
	
	protected void plotLeistungen(Athlet athlet) {
		LinkedList<Leistung> leistungen = athlet.getLeistungen();
		Iterator<Leistung> leistungenIterator = leistungen.iterator();
		final XYSeries athletenSerie = new XYSeries(athlet.getName());
		double strecke, geschwindigkeit, zeit;
		Leistung aktuelleLeistung;
		while (leistungenIterator.hasNext()) {
			aktuelleLeistung = leistungenIterator.next();
			strecke = aktuelleLeistung.getStrecke();
			geschwindigkeit = aktuelleLeistung.getGeschwindigkeit();
			zeit = UnitsHelper.toMinKm(geschwindigkeit);
			athletenSerie.add(strecke, zeit);
		}
		view.addLeistungsSerie(athletenSerie);
	}

	protected void plotBerechneteBestzeiten (Athlet athlet) throws SlopeFaktorNotSetException {
		LinkedList<Leistung> bestzeiten = athlet.getMoeglicheBestzeitenListe();
		String athletName = athlet.getName();
		Iterator<Leistung> leistungenIterator = bestzeiten.iterator();
		final XYSeries athletenSerie = new XYSeries(athletName);
		int strecke;
		double geschwindigkeit, zeit;
		Leistung leistung;
		while (leistungenIterator.hasNext()) {
			leistung = leistungenIterator.next();
			strecke = Strecken.getStreckenlaengeById(leistung.getId_strecke());
			geschwindigkeit = leistung.getGeschwindigkeit();
			zeit = UnitsHelper.toMinKm(geschwindigkeit);
			athletenSerie.add(strecke, zeit);
		}
		view.addBerechneteBestzeitenUndTrendlinie(athletenSerie);
	}

	public void windowActivated(WindowEvent arg0) {
		// Auto-generated method stub
	}

	public void windowClosed(WindowEvent arg0) {
		// Auto-generated method stub
	}

	public void windowClosing(WindowEvent arg0) {
		view.release();		
	}

	public void windowDeactivated(WindowEvent arg0) {
		// Auto-generated method stub
	}

	public void windowDeiconified(WindowEvent arg0) {
		// Auto-generated method stub		
	}

	public void windowIconified(WindowEvent arg0) {
		// Auto-generated method stub
	}

	public void windowOpened(WindowEvent arg0) {
		// Auto-generated method stub
	}
}