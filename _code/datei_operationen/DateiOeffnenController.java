package datei_operationen;

import globale_helper.LeistungHelper;

import java.io.*;
import java.util.LinkedList;
import au.com.bytecode.opencsv.CSVReader;
import javax.swing.JOptionPane;

import main.Hauptfenster;
import main.ProfilTab;
import model.*;

public class DateiOeffnenController {

	private String nameAthlet;
	private long idAthlet;
	private LinkedList<Leistung> leistungen;
	DateiOeffnen view;
	
	protected DateiOeffnenController(DateiOeffnen view){
		this.view = view;
	}
	
	protected void release(){
		this.nameAthlet = null;
		this.idAthlet = -1;
		this.leistungen = null;
		this.view = null;
	}

	protected void openAthletFromCSVFile (String pfad) throws FileNotFoundException, IOException, AlreadyOpenException, SyntaxException {	
	    CSVReader reader = new CSVReader(new FileReader(pfad), ';', '\0');
	    if (!ValidatorHelper.isSyntacticallyCorrect(pfad)) {
	    	reader.close();
	    	throw new SyntaxException();
	    }
	    kopfzeileAuslesen(reader);
	    if (Hauptfenster.aktuellesHauptfenster.checkAthletGeöffnet(nameAthlet,idAthlet)) {
	    	JOptionPane.showMessageDialog(Hauptfenster.aktuellesHauptfenster.getContext(),
	    			"Das ausgewählte Athletenprofil ist bereit geöffnet!",
	    			"Athletenprofil bereits geöffnet",
					JOptionPane.WARNING_MESSAGE);
	    	reader.close();
	    	throw new AlreadyOpenException();
	    }
	    leistungen = getLeistungen(reader);
	}

	protected void openAthletInTab(String pfad){
		// TODO: eleganter!! zu athletenliste hinzufügen, die von Mainframe observed wird (tab automatisch öffnen!!!)
	    ProfilTab tab = Hauptfenster.aktuellesHauptfenster.createTab(nameAthlet, idAthlet, leistungen);
	    tab.setSpeicherPfad(pfad);
	    tab.setSpeicherStatus(true);
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
    	int streckenlänge = Strecken.getStreckenlaengeById(streckenId);
    	String bezeichnung = leistung[2];
    	double zeit = Double.parseDouble(leistung[3]);
    	LeistungHelper leistungHelper = new LeistungHelper();
    	double geschwindigkeit = leistungHelper.berechneGeschwindigkeit(streckenlänge, zeit);
    	return new Leistung(streckenId, idAthlet, bezeichnung, datum, geschwindigkeit);
	}
}