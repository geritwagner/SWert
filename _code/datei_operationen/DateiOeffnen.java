package datei_operationen;

import java.io.*;
import java.util.*;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import au.com.bytecode.opencsv.CSVReader;
import main.Hauptfenster;
import globale_helper.*;
import model.*;

/**
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta), Gerit Wagner
 */
public class DateiOeffnen {

	private String nameAthlet;
	private long idAthlet;
	private LinkedList<Leistung> leistungen;
	protected AthletenListe athletenliste;

	public DateiOeffnen(AthletenListe athletenliste) throws FileNotFoundException, 
								IOException, AlreadyOpenException, SyntaxException   {
		
		this.athletenliste = athletenliste;
		String pfad = getCSVPfadFromUserDialog();
		if (! (pfad == null)){
			Athlet geöffneterAthlet = openAthletFromCSVFile(pfad);
		    geöffneterAthlet.setSpeicherpfad(pfad);
		    athletenliste.addAthlet(geöffneterAthlet);			
		}
		release();
	}
	
	private Athlet openAthletFromCSVFile (String pfad) throws FileNotFoundException, 
									IOException, AlreadyOpenException, SyntaxException {	
		
	    CSVReader reader = new CSVReader(new FileReader(pfad), ';', '\0');
	    if (!ValidatorHelper.isSyntacticallyCorrect(pfad)) {
	    	reader.close();
	    	throw new SyntaxException();
	    }
	    kopfzeileAuslesen(reader);
	    if (Hauptfenster.aktuellesHauptfenster.checkAthletGeöffnet(nameAthlet,idAthlet)) {
	    	JOptionPane.showMessageDialog(Hauptfenster.aktuellesHauptfenster,
	    			"Das ausgewählte Athletenprofil ist bereit geöffnet!",
	    			"Athletenprofil bereits geöffnet", JOptionPane.WARNING_MESSAGE);
	    	reader.close();
	    	throw new AlreadyOpenException();
	    }
	    leistungen = getLeistungen(reader);
	    return new Athlet(idAthlet, nameAthlet, leistungen);
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
    	String zeitString = leistung[3];
    	LeistungHelper leistungHelper = new LeistungHelper();
    	double zeit = leistungHelper.parseZeitInSec(zeitString);
    	double geschwindigkeit = leistungHelper.berechneGeschwindigkeit(streckenlänge, zeit);
    	return new Leistung(streckenId, idAthlet, bezeichnung, datum, geschwindigkeit);
	}

	private String getCSVPfadFromUserDialog() {
		JFileChooser chooser = new JFileChooser();
		chooser.removeChoosableFileFilter(chooser.getChoosableFileFilters()[0]);
		FileFilter filter = new FileNameExtensionFilter("CSV Dateien","csv");chooser.addChoosableFileFilter(filter); 
		if (chooser.showOpenDialog(Hauptfenster.aktuellesHauptfenster) == JFileChooser.APPROVE_OPTION){
			return chooser.getSelectedFile().getAbsolutePath();        
		}else {
			return null;
		}
	}
	
	private void release(){
		nameAthlet = null;
		leistungen = null;
		athletenliste = null;
	}
}