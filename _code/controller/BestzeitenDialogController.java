package controller;

import helper.LeistungHelper;

import java.util.LinkedList;

import view.BestzeitenDialog;
import model.Athlet;
import model.Leistung;
import model.Strecken;

public class BestzeitenDialogController {
	
	Athlet athlet;
	BestzeitenDialog view;
	
	public BestzeitenDialogController(Athlet athlet, BestzeitenDialog view){
		this.athlet = athlet;
		this.view = view;
	}
	
	public String berechneBestzeit(String streckenString) {
    	if (streckenString!= null) {
    		int strecke = -1;
    		try{
    			strecke = Integer.parseInt(streckenString);
    		} catch (Exception h) {
    			return "-";
    		}  
    		if (strecke < 300)
    			return "-";
    		
    		double bestzeit;
			try {
				bestzeit = athlet.calculateTime(strecke);
				LeistungHelper lController = new LeistungHelper();
				String bestzeitString = lController.parseSecInMinutenstring(bestzeit);
				return bestzeitString;
			} catch (Exception e) {
				return "-";
			}
    	}
		return "-";
	}
	
	/**
	 * Berechnen der m�glichen Bestzeiten zu den fixen Streckenl�ngen,
	 * die direkt an die JTable �bergeben werden
	 * @return: Array mit [][0] als Streckenl�nge (String) und [][1] als Bestzeit (String: 00:00,00)
	 */
	public Object[][] berechneBestzeiten() {
		int streckenAnzahl = Strecken.getStreckenArrayLength();
		Object[][] data = new Object [streckenAnzahl][2];
		try {
			LinkedList<Leistung>  bestleistungen = athlet.getMoeglicheBestzeitenListe();
			Leistung aktuelleLeistung;
			for (int i = 0; i < streckenAnzahl; i++) {
				aktuelleLeistung = bestleistungen.get(i);
				data[i][0] = aktuelleLeistung.getStreckenString();
				data[i][1] = aktuelleLeistung.getZeitString();			
			}
		} catch (Exception e) {
		}
		return data;
	}
}