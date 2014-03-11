package controller;

import helper.UnitsHelper;

import java.util.*;
import main.Main;
import model.*;
import org.jfree.data.xy.XYSeries;
import view.DiagrammFrame;
import view.ProfilTab;

/**
 * Controller f�r alle Aktionen, die den LeistungsDialog betreffen
 * @author Honors-WInfo-Projekt (Fabian B�hm, Alexander Puchta)
 */
public class DiagrammController {
	
	private DiagrammFrame diagramm;
	private String letzterAthlet;

	public DiagrammController() {
		diagramm = new DiagrammFrame();
	}

	public void DiagrammOeffnen() {		
		diagramm.setEnabled(true);
		openAllAthletes();
		diagramm.setVisible(true);
	}
	
	public void DiagrammSchlie�en() {
		diagramm.setEnabled(false);
		diagramm.dispose();
		diagramm = new DiagrammFrame();
	}
		
	private void openAllAthletes(){
		// Athletenliste auf andere Weise holen!
		int countAthletes = Main.mainFrame.tabbedPane.getTabCount() - 1;
		for(int i = 0; i < countAthletes; i++){
			ProfilTab tab = (ProfilTab) Main.mainFrame.tabbedPane.getComponentAt(i);
			addAthletBerechneteLeistungsKurve(tab.getAthlet());	
			// TODO: statt getLeistungen m�ssten vermutlich die 2 Punkte, auf deren Grundlage die Schwelle
			// berechnet wird, zur�ckgegeben werden...
			// addBestzeiten(tab.getAthlet().getLeistungen());
			try {
				addBestzeiten(tab.getAthlet().getMoeglicheBestzeitenListe());
			} catch (Exception e) {
				e.printStackTrace();
				// Bestzeiten k�nnen nur f�r Athleten berechnet werden, bei denen der Slope-Faktor bekannt ist.
			}
		}
	}
	
	/**
	 * Hinzuf�gen aller tats�chlich erbrachten Leistungen eines Athleten
	 * zum Diagramm
	 * @param athlet
	 */
	// TODO: Benennung??? berechneteLeistungen = tats�chlich erbrachte!??!
	public void addAthletBerechneteLeistungsKurve(Athlet athlet) {
		LinkedList<Leistung> leistungen = athlet.getLeistungen();
		Iterator<Leistung> leistungenIterator = leistungen.iterator();
		letzterAthlet = athlet.getName();
		final XYSeries athletenSerie = new XYSeries(letzterAthlet);
		while (leistungenIterator.hasNext()) {
			Leistung leistung = leistungenIterator.next();
			double strecke = leistung.getStrecke();
			double geschwindigkeit = leistung.getGeschwindigkeit();
			double zeit = UnitsHelper.toMinKm(geschwindigkeit);
			athletenSerie.add(strecke, zeit);
		}
		diagramm.addLeistungsSerie(athletenSerie);
	}

	/**
	 * Hinzuf�gen der m�glichen Bestleistungen eines Athleten
	 * f�r die verschiedenen Streckenl�ngen zum Diagramm
	 * @param athlet
	 */
	// TODO: Benennung? m�gliche = addBestzeiten?
	public void addBestzeiten (LinkedList<Leistung> bestzeiten) {
		Iterator<Leistung> leistungenIterator = bestzeiten.iterator();
		final XYSeries athletenSerie = new XYSeries(letzterAthlet);
		while (leistungenIterator.hasNext()) {
			Leistung leistung = leistungenIterator.next();
			int strecke = Strecken.getStreckenlaengeById(leistung.getId_strecke());
			double geschwindigkeit = leistung.getGeschwindigkeit();
			double zeit = UnitsHelper.toMinKm(geschwindigkeit);
			athletenSerie.add(strecke, zeit);
		}
		diagramm.addBestzeitenSerie(athletenSerie);
	}
}