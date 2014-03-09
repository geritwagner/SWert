package controller;

import java.io.*;
import java.util.LinkedList;
import javax.swing.JOptionPane;
import main.Main;
import model.*;
import view.ProfilTab;
import au.com.bytecode.opencsv.*;

/**
 * Controller für alle Aktionen, die die CSV-Dateien betreffen
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta)
 */
public class CSVController {

	private String nameAthlet;
	private long idAthlet;
	
	/**
	 * Methode die eine CSV-Datei einliest und daraus ein Athleten-Profil erstellt;
	 * false wird zurückgegeben, falls dabei eine Fehler entsteht
	 * @return: TRUE für erfolgreiches Einlesen der CSV
	 */
	// TODO: sollte ein Athleten-Objekt zurückgeben, öffnen von Tabs sollte im Controller realisiert werden!
	public boolean lesen (String pfad) {	
		try{			
		    CSVReader reader = new CSVReader(new FileReader(pfad), ';', '\0');
		    if (!isSyntacticallyCorrect(pfad)) {
		    	reader.close();
		    	return false;
		    }
		    kopfzeileAuslesen(reader);
		    if (Main.mainFrame.checkAthletGeöffnet(nameAthlet,idAthlet)) {
		    	JOptionPane.showMessageDialog(Main.mainFrame.getContext(),
		    			"Das ausgewählte Athletenprofil ist bereit geöffnet!",
		    			"Athletenprofil bereits geöffnet",
						JOptionPane.WARNING_MESSAGE);
		    	reader.close();
		    	return true;
		    }
		    ProfilTab tab = Main.mainFrame.createTab(nameAthlet,idAthlet);		    
		    restlicheLeistungenImTabOeffnen(reader, tab);
		    tab.setSpeicherPfad(pfad);
		    tab.setSpeicherStatus(true);
		    reader.close();
		    return true;
		}catch (Exception e) {
			 e.printStackTrace();
			 return false;
		}
	}
	
	/**
	 * Methode, die ein übergebenes Profil-Tab unter der Pfadangabe
	 * als CSV-Datei erstellt
	 * @return: TRUE für erfolgreiches Erstellen der CSV
	 */	public boolean schreiben(String pfad, Athlet athlet) {
		try{
		     CSVWriter writer = new CSVWriter(new FileWriter(pfad), ';', '\0');
		     String[] entries = getAthletenInfo(athlet);
		     writer.writeNext(entries);	     
		     schreibeLeistungen(writer,athlet.getLeistungen());
		     writer.close();
		     if (isSyntacticallyCorrect(pfad)) {	    	 
		    	 return true;
		     } else {
		    	 return false;
		     }		    
	     }catch (Exception e) {
	    	e.printStackTrace();
			return false;
		}
	}
	
	private void kopfzeileAuslesen(CSVReader reader) throws IOException{
	    String [] aktuelleZeile;
	    aktuelleZeile = reader.readNext();
	    nameAthlet = aktuelleZeile[1];
	    idAthlet = Long.parseLong(aktuelleZeile[0]);
	}
	
	private void restlicheLeistungenImTabOeffnen(CSVReader reader, ProfilTab tab) throws IOException{
		String [] aktuelleZeile;
		while ((aktuelleZeile = reader.readNext()) != null) {
			Leistung naechsteLeistung = leistungAuslesen(aktuelleZeile);
	    	tab.addZeile(naechsteLeistung);
	    }
	}
	
	private Leistung leistungAuslesen (String[] leistung){
		String datum = leistung[0];
    	String strecke = leistung[1];
    	int streckenId = Strecken.getStreckenIdByString(strecke);
    	String bezeichnung = leistung[2];
    	double zeit = Double.parseDouble(leistung[3]);
    	return new Leistung(streckenId, idAthlet, zeit, bezeichnung, datum);
	}
	
	private String[] getAthletenInfo(Athlet athlet) {
		String[] athletInfo = new String[4];
		athletInfo[0] = String.valueOf(athlet.getId());
		athletInfo[1] = athlet.getName();
		return athletInfo;
	}
	
	private void schreibeLeistungen (CSVWriter writer, LinkedList<Leistung> leistungen) {
		// TODO: auch die getObjectData-Methode verwenden?
		// TODO: besser lesbare Formate verwenden (1.000m und 12 km/h) - dann müsste aber die reader-Methoden ebenfalls angepasst werden.
		
		for (Leistung aktuelleLeistung : leistungen){
			String[] eingaben = new String[4];
			eingaben[0] = aktuelleLeistung.getDatum();
			eingaben[1] = String.valueOf(aktuelleLeistung.getStrecke());
			eingaben[2] = aktuelleLeistung.getBezeichnung();
			eingaben[3] = String.valueOf(aktuelleLeistung.getZeit());
			writer.writeNext(eingaben);
		} 
	}
	
	private boolean isSyntacticallyCorrect (String pfad) {
		try{
			CSVReader reader = new CSVReader(new FileReader(pfad), ';', '\0');
			if(!isSyntacticallyCorrectHeading(reader))	{
				reader.close();
				return false;
			}
			if(!isSyntacticallyCorrectLeistungen(reader))	{
				reader.close();
				return false;
			}
			reader.close();
			return true;		
		}catch(Exception e) {
			e.printStackTrace();
			return false;
		}		
	}
	
	private boolean isSyntacticallyCorrectHeading(CSVReader reader) throws IOException{
		String[] aktuelleZeile;	
		aktuelleZeile = reader.readNext();
		if (aktuelleZeile.length != 4) {
			return false;
		}
		return true;
	}
	
	private boolean isSyntacticallyCorrectLeistungen(CSVReader reader) throws IOException{
		String[] aktuelleZeile;	
		while ((aktuelleZeile = reader.readNext()) != null) {
	       if (aktuelleZeile.length != 4) {
	    	   return false;
	       }
	    }
		return true;
	}	
}