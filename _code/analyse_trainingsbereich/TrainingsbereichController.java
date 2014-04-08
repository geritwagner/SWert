package analyse_trainingsbereich;

import java.awt.event.*;
import java.text.*;
import javax.swing.JSlider;
import javax.swing.event.*;
import globale_helper.*;
import model.*;

/**
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta), Gerit Wagner
 */

public class TrainingsbereichController implements ChangeListener, ActionListener {

	private static final double WINZERER_AUFSCHLAG = 1.03;
	private static final double WINZERER_RUNDEN_LÄNGE = 3.409;
	private static final int UNTERE_SCHRANKE_TRAININGSBEREICHE = 60;
	private static final int OBERE_SCHRANKE_TRAININGSBEREICHE = 110;
	private static final int SCHRITT_TRAININGSBEREICH = 5;
		
	private Athlet athlet;
	private TrainingsbereichDialog view;
	
	protected TrainingsbereichController(Athlet athlet, TrainingsbereichDialog view){
		this.athlet = athlet;
		this.view = view;
	}

	protected void release(){
		view = null;
		athlet = null;
	}
	protected static double getWinzererAufschlag() {
		return WINZERER_AUFSCHLAG;
	}

	protected static int getAnzahlBereiche(){
		return (OBERE_SCHRANKE_TRAININGSBEREICHE-UNTERE_SCHRANKE_TRAININGSBEREICHE)/SCHRITT_TRAININGSBEREICH + 1;
	}
	
	/**
	 * Berechnen der Trainingsbereiche, die direkt an die JTable übergeben werden
	 * @return: [x][y] --> x enthält die verschiedenen Strecke,
	 * y die entsprechenden Geschwindigkeiten in kmh, ms, minkm
	 */
	protected Object[][] berechneTrainingsBereiche() {	
		LeistungHelper l = new LeistungHelper();
		DecimalFormat f = new DecimalFormat("#0.00");
		Object[][] data = new Object [getAnzahlBereiche()][4];
		int zähler = 0;
		double gewichtung, trainingsGeschwindigkeitSKm;
		for (int i = UNTERE_SCHRANKE_TRAININGSBEREICHE; i <= OBERE_SCHRANKE_TRAININGSBEREICHE; i = i + SCHRITT_TRAININGSBEREICH) {
			gewichtung = (200-i)/100D;
			try {
				trainingsGeschwindigkeitSKm = gewichtung * athlet.getAnaerobeSchwelle();
				data[zähler][0] = String.valueOf(i) + "%";
				data[zähler][1] = l.parseSecInMinutenstring(trainingsGeschwindigkeitSKm);	
				data[zähler][2] = f.format(UnitsHelper.toKmH(trainingsGeschwindigkeitSKm));
				data[zähler][3] = f.format(UnitsHelper.toMS(trainingsGeschwindigkeitSKm));
				zähler++;
			} catch (Exception e) {
			}			
		}
		return data;	
	}
	
	/**
	 * Berechnen der Trainingsbereiche, die direkt an die JTable übergeben werden
	 * @return: [x][y] --> x enthält die verschiedenen Strecke,
	 * y die entsprechenden Geschwindigkeiten in kmh, ms, minkm, rundengeschwindigkeit
	 */
	protected Object[][] berechneTrainingsBereicheProfiliert(int rundenAnzahl) {	
		LeistungHelper l = new LeistungHelper();
		DecimalFormat f = new DecimalFormat("#0.00");
		Object[][] data = new Object [getAnzahlBereiche()][5];
		int zähler = 0;
		double gewichtung, trainingsGeschwindigkeitSKm, winzererRundenZeitSec;
		for (int i = UNTERE_SCHRANKE_TRAININGSBEREICHE; i <= OBERE_SCHRANKE_TRAININGSBEREICHE; 
				i = i + SCHRITT_TRAININGSBEREICH) {
			gewichtung = (200-i)/100D;
			try {
				trainingsGeschwindigkeitSKm = gewichtung * athlet.getAnaerobeSchwelle() * WINZERER_AUFSCHLAG;
				winzererRundenZeitSec = trainingsGeschwindigkeitSKm*WINZERER_RUNDEN_LÄNGE;
				data[zähler][0] = String.valueOf(i) + "%";
				data[zähler][1] = l.parseSecInMinutenstring(trainingsGeschwindigkeitSKm);	
				data[zähler][2] = f.format(UnitsHelper.toKmH(trainingsGeschwindigkeitSKm));
				data[zähler][3] = f.format(UnitsHelper.toMS(trainingsGeschwindigkeitSKm));
				data[zähler][4] = l.parseSecInMinutenstring(winzererRundenZeitSec*rundenAnzahl);
				zähler++;
			} catch (Exception e) {
			}	
		}		
		return data;	
	}
	
	/**
	 * Updaten der einzelenen Rundengeschwindigkeit in Abhängigkeit der Rundenanzahl
	 * @param rundenAnzahl: aktuell im Slider eingestellte Rundenzahl
	 */
	protected void updateRundenzeit (int rundenAnzahl) {	
		LeistungHelper l = new LeistungHelper();
		int zähler = 0;
		double gewichtung, trainingsGeschwindigkeitSKm, winzererRundenZeitSec;
		for (int i = UNTERE_SCHRANKE_TRAININGSBEREICHE; i <= OBERE_SCHRANKE_TRAININGSBEREICHE; 
				i = i + SCHRITT_TRAININGSBEREICH) {
			gewichtung = (200-i)/100D;
			try {
				trainingsGeschwindigkeitSKm = gewichtung * athlet.getAnaerobeSchwelle() * WINZERER_AUFSCHLAG;
				winzererRundenZeitSec = trainingsGeschwindigkeitSKm*WINZERER_RUNDEN_LÄNGE;
				String MinutenString = l.parseSecInMinutenstring(winzererRundenZeitSec*rundenAnzahl);
				view.trainingsTabelle.setValueAt(MinutenString, zähler, 4);
				zähler++;
			} catch (Exception e) {
			}
		}		
	}

	public void stateChanged(ChangeEvent arg0) {
        JSlider slider = (JSlider) arg0.getSource();
        if (!slider.getValueIsAdjusting()) {
      	int rundenZahl = slider.getValue();
          updateRundenzeit(rundenZahl);	            
        }		
	}

	public void actionPerformed(ActionEvent arg0) {
		String command = arg0.getActionCommand();
		switch (command){
		case "RWH": 				
			view.initJTableProfiliert();
			break;
		case "flache Strecke": 						
		    view.initJTable();		      
		    view.slider.setVisible(false);
		    view.lblRundenanzahl.setVisible(false);
		    break;
		case "Cancel":
			view.release();
			break;
		}
	}
}