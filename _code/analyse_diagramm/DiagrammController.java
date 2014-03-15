package analyse_diagramm;

import globale_helper.UnitsHelper;

import java.util.*;
import main.Hauptfenster;
import main.ProfilTab;
import model.*;
import org.jfree.data.xy.XYSeries;

/**
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta)
 */
public class DiagrammController {
	
	private DiagrammFrame view;
//	private List<Athlet> alleAthleten - im Konstruktor übergeben?

	// TODO: change to protected!!
	public DiagrammController() {
		view = new DiagrammFrame();
		openAllAthletes();
	}
	
	protected void release(){
		view = null;
//		athlet = null;
	}

	private void openAllAthletes(){
		// TODO: Athletenliste auf andere Weise holen!
		int countAthletes = Hauptfenster.aktuellesHauptfenster.tabbedPane.getTabCount() - 1;
		Athlet aktuellerAthlet;
		ProfilTab tab;
		for(int i = 0; i < countAthletes; i++){
			try {
				tab = (ProfilTab) Hauptfenster.aktuellesHauptfenster.tabbedPane.getComponentAt(i);
				aktuellerAthlet = tab.getAthlet();
				plotLeistungen(aktuellerAthlet);	
				plotBerechneteBestzeiten(aktuellerAthlet);
			} catch (Exception e) {
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

	protected void plotBerechneteBestzeiten (Athlet athlet) throws Exception {
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
}