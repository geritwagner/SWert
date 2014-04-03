package analyse_bestzeiten;

import globale_helper.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.event.*;
import model.*;

/**
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta), Gerit Wagner
 */
public class BestzeitenDialogController implements ActionListener, DocumentListener {
	
	private Athlet athlet;
	private BestzeitenDialog view;
	private LeistungHelper lHelper;
	private final int MINIMALE_STRECKENLAENGE = 300;
	
	protected BestzeitenDialogController(Athlet athlet, BestzeitenDialog view){
		this.athlet = athlet;
		this.view = view;
		this.lHelper = new LeistungHelper();
	}
	
	protected void release(){
		view = null;
		athlet = null;
	}
	
	protected String berechneBestzeit(String streckenString) {
		if (streckenString!= null) {
    		int strecke = -1;
    		try{
    			strecke = Integer.parseInt(streckenString);
    		} catch (Exception h) {
    			return "-";
    		}  
    		if (strecke < MINIMALE_STRECKENLAENGE){
    			// TODO: ggf. Info anzeigen
    			return "-";    			
    		}
    		
    		double bestzeit;
			try {
				bestzeit = athlet.getTime(strecke);
				return lHelper.parseSecInMinutenstring(bestzeit);
			} catch (Exception e) {
				return "-";
			}
    	}
		return "-";
	}
	
	/**
	 * @return: Array mit [][0] als Streckenlänge (String) und [][1] als Bestzeit (String: 00:00,00)
	 */
	protected Object[][] generateBestzeitenTableFormat() {
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
			e.printStackTrace();
		}
		return data;
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command == "Cancel"){
			view.release();
		}	
	}

	public void insertUpdate(DocumentEvent e) {
		String berechneteBestzeit =  berechneBestzeit(view.txtFieldStrecke.getText());
		view.txtFieldZeit.setText(berechneteBestzeit);
	}
	
	public void removeUpdate(DocumentEvent e) {
		String berechneteBestzeit =  berechneBestzeit(view.txtFieldStrecke.getText());
		view.txtFieldZeit.setText(berechneteBestzeit);
	}
	
	public void changedUpdate(DocumentEvent e) {
		String berechneteBestzeit =  berechneBestzeit(view.txtFieldStrecke.getText());
		view.txtFieldZeit.setText(berechneteBestzeit);
	}
}