package analyse_trainingsbereich;

import java.text.*;

import global_helpers.*;
import model.*;

public class TrainingsbereichController {

	private static final double WINZERER_AUFSCHLAG = 1.03;
	private static final double WINZERER_RUNDEN_L�NGE = 3.409;
	private static final int UNTERE_SCHRANKE_TRAININGSBEREICHE = 60;
	private static final int OBERE_SCHRANKE_TRAININGSBEREICHE = 110;
	private static final int SCHRITT_TRAININGSBEREICH = 5;
		
	Athlet athlet;
	TrainingsbereichDialog view;
	
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
	 * Berechnen der Trainingsbereiche, die direkt an die JTable �bergeben werden
	 * @return: [x][y] --> x enth�lt die verschiedenen Strecke,
	 * y die entsprechenden Geschwindigkeiten in kmh, ms, minkm
	 */
	protected Object[][] berechneTrainingsBereiche() {	
		LeistungHelper l = new LeistungHelper();
		DecimalFormat f = new DecimalFormat("#0.00");
		Object[][] data = new Object [getAnzahlBereiche()][4];
		int z�hler = 0;
		double gewichtung, trainingsGeschwindigkeitSKm;
		for (int i = UNTERE_SCHRANKE_TRAININGSBEREICHE; i <= OBERE_SCHRANKE_TRAININGSBEREICHE; i = i + SCHRITT_TRAININGSBEREICH) {
			gewichtung = (200-i)/100D;
			try {
				trainingsGeschwindigkeitSKm = gewichtung * athlet.getAnaerobeSchwelle();
				data[z�hler][0] = String.valueOf(i) + "%";
				data[z�hler][1] = l.parseSecInMinutenstring(trainingsGeschwindigkeitSKm);	
				data[z�hler][2] = f.format(UnitsHelper.toKmH(trainingsGeschwindigkeitSKm));
				data[z�hler][3] = f.format(UnitsHelper.toMS(trainingsGeschwindigkeitSKm));
				z�hler++;
			} catch (Exception e) {
			}			
		}
		return data;	
	}
	
	/**
	 * Berechnen der Trainingsbereiche, die direkt an die JTable �bergeben werden
	 * @return: [x][y] --> x enth�lt die verschiedenen Strecke,
	 * y die entsprechenden Geschwindigkeiten in kmh, ms, minkm, rundengeschwindigkeit
	 */
	protected Object[][] berechneTrainingsBereicheProfiliert(int rundenAnzahl) {	
		LeistungHelper l = new LeistungHelper();
		DecimalFormat f = new DecimalFormat("#0.00");
		Object[][] data = new Object [getAnzahlBereiche()][5];
		int z�hler = 0;
		double gewichtung, trainingsGeschwindigkeitSKm, winzererRundenZeitSec;
		for (int i = UNTERE_SCHRANKE_TRAININGSBEREICHE; i <= OBERE_SCHRANKE_TRAININGSBEREICHE; 
				i = i + SCHRITT_TRAININGSBEREICH) {
			gewichtung = (200-i)/100D;
			try {
				trainingsGeschwindigkeitSKm = gewichtung * athlet.getAnaerobeSchwelle() * WINZERER_AUFSCHLAG;
				winzererRundenZeitSec = trainingsGeschwindigkeitSKm*WINZERER_RUNDEN_L�NGE;
				data[z�hler][0] = String.valueOf(i) + "%";
				data[z�hler][1] = l.parseSecInMinutenstring(trainingsGeschwindigkeitSKm);	
				data[z�hler][2] = f.format(UnitsHelper.toKmH(trainingsGeschwindigkeitSKm));
				data[z�hler][3] = f.format(UnitsHelper.toMS(trainingsGeschwindigkeitSKm));
				data[z�hler][4] = l.parseSecInMinutenstring(winzererRundenZeitSec*rundenAnzahl);
				z�hler++;
			} catch (Exception e) {
			}	
		}		
		return data;	
	}
	
	/**
	 * Updaten der einzelenen Rundengeschwindigkeit in Abh�ngigkeit der Rundenanzahl
	 * @param rundenAnzahl: aktuell im Slider eingestellte Rundenzahl
	 */
	protected void updateRundenzeit (int rundenAnzahl) {	
		LeistungHelper l = new LeistungHelper();
		int z�hler = 0;
		double gewichtung, trainingsGeschwindigkeitSKm, winzererRundenZeitSec;
		for (int i = UNTERE_SCHRANKE_TRAININGSBEREICHE; i <= OBERE_SCHRANKE_TRAININGSBEREICHE; 
				i = i + SCHRITT_TRAININGSBEREICH) {
			gewichtung = (200-i)/100D;
			try {
				trainingsGeschwindigkeitSKm = gewichtung * athlet.getAnaerobeSchwelle() * WINZERER_AUFSCHLAG;
				winzererRundenZeitSec = trainingsGeschwindigkeitSKm*WINZERER_RUNDEN_L�NGE;
				String MinutenString = l.parseSecInMinutenstring(winzererRundenZeitSec*rundenAnzahl);
				view.trainingsTabelle.setValueAt(MinutenString, z�hler, 4);
				z�hler++;
			} catch (Exception e) {
			}
		}		
	}
}