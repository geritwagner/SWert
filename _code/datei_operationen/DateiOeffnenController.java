package datei_operationen;

import globale_helper.LeistungHelper;

import java.io.*;
import java.util.LinkedList;
import au.com.bytecode.opencsv.CSVReader;
import javax.swing.JOptionPane;

import main.Main;
import model.*;

public class DateiOeffnenController {

	private String nameAthlet;
	private long idAthlet;
	private LinkedList<Leistung> leistungen;
	DateiPfadOeffnen view;
	
	public DateiOeffnenController(DateiPfadOeffnen view){
		this.view = view;
	}
	
	protected void release(){
		this.nameAthlet = null;
		this.idAthlet = -1;
		this.leistungen = null;
		this.view = null;
	}
	/**
	 * Methode die eine CSV-Datei einliest und ggf. Exceptions wirft.
	 */
	// TODO: sollte ein Athleten-Objekt zur�ckgeben, �ffnen von Tabs sollte im Controller realisiert werden!
	public void lesen (String pfad) throws FileNotFoundException, IOException, AlreadyOpenException, SyntaxException {	
	    CSVReader reader = new CSVReader(new FileReader(pfad), ';', '\0');
	    if (!ValidatorHelper.isSyntacticallyCorrect(pfad)) {
	    	reader.close();
	    	throw new SyntaxException();
	    }
	    kopfzeileAuslesen(reader);
	    if (Main.mainFrame.checkAthletGe�ffnet(nameAthlet,idAthlet)) {
	    	JOptionPane.showMessageDialog(Main.mainFrame.getContext(),
	    			"Das ausgew�hlte Athletenprofil ist bereit ge�ffnet!",
	    			"Athletenprofil bereits ge�ffnet",
					JOptionPane.WARNING_MESSAGE);
	    	reader.close();
	    	throw new AlreadyOpenException();
	    }
	    leistungen = getLeistungen(reader);
	}

	protected void openAthlet(){
	    Main.mainFrame.createTab(nameAthlet, idAthlet, leistungen);
	}
	
	private void kopfzeileAuslesen(CSVReader reader) throws IOException{
	    String [] aktuelleZeile;
	    aktuelleZeile = reader.readNext();
	    nameAthlet = aktuelleZeile[1];
	    idAthlet = Long.parseLong(aktuelleZeile[0]);
	}
	
	private LinkedList<Leistung> getLeistungen(CSVReader reader) throws IOException{
		String [] aktuelleZeile;
		LinkedList<Leistung> leistungen = new LinkedList<>();
		while ((aktuelleZeile = reader.readNext()) != null) {
			leistungen.add (leistungAuslesen(aktuelleZeile));
	    }
		reader.close();
		return leistungen;
	}
	
	private Leistung leistungAuslesen (String[] leistung){
		String datum = leistung[0];
    	String strecke = leistung[1];
    	int streckenId = Strecken.getStreckenIdByString(strecke);
    	int streckenl�nge = Strecken.getStreckenlaengeById(streckenId);
    	String bezeichnung = leistung[2];
    	double zeit = Double.parseDouble(leistung[3]);
    	LeistungHelper leistungHelper = new LeistungHelper();
    	double geschwindigkeit = leistungHelper.berechneGeschwindigkeit(streckenl�nge, zeit);
    	return new Leistung(streckenId, idAthlet, bezeichnung, datum, geschwindigkeit);
	}
}