package controller;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JOptionPane;

import main.Main;
import model.Leistung;
import view.ProfilTab;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

/**
 * Controller für alle Aktionen, die die CSV-Dateien betreffen
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta)
 */
public class CSVController {

//----------------------- Variablen -----------------------
	private String nameAthlet;
	private long idAthlet;
	private StreckenController sController;
	private LeistungController leistungController;
	
//----------------------- ÖFFENTLICHE METHODEN -----------------------
	/**
	 * Methode die eine CSV-Datei einliest und daraus ein Athleten-Profil erstellt;
	 * false wird zurückgegeben, falls dabei eine Fehler entsteht
	 * @param pfad: Pfad der einzulesenden CSV-Datei
	 * @return: TRUE für erfolgreiches Einlesen der CSV
	 */
	public boolean lesen (String pfad) {	
		sController = new StreckenController();
		leistungController = Main.leistungController;
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
	 * @param pfad: Pfad an dem die CSV erstellt werden soll
	 * @param tab: Tab von dem die CSV erstellt werden soll
	 * @return: TRUE für erfolgreiches Erstellen der CSV
	 */
	public boolean schreiben(String pfad, ProfilTab tab) {
		try{		
		     CSVWriter writer = new CSVWriter(new FileWriter(pfad), ';', '\0');
		     String[] entries = getAthletenInfo(tab);
		     writer.writeNext(entries);	     
		     schreibeLeistungen(writer,tab);
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

//----------------------- PRIVATE METHODEN -----------------------
	
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
    	int streckenId = sController.getStreckenIdByString(strecke);
    	String bezeichnung = leistung[2];
    	double geschwindigkeit = Double.parseDouble(leistung[3]);
    	return leistungController.neueLeistung(streckenId, idAthlet, geschwindigkeit, bezeichnung, datum);
    	
	}
	
	/**
	 * Liest Name und ID des Athlten in dem gegebenen Tab aus 
	 * @param tab: Tab von dem Name und ID geholt werden soll
	 * @return: [0] enthält die ID, [1] den Namen 
	 */
	private String[] getAthletenInfo(ProfilTab tab) {
		String[] athletInfo = new String[4];
		athletInfo[0] = String.valueOf(tab.getAthletenId());
		athletInfo[1] = tab.getAthletenName();
		return athletInfo;
	}
	
	/**
	 * Schreiben aller Leistungen eines Athleten in die CSV
	 * @param writer
	 * @param tab: Tab von dem die Leistungen geschrieben werden sollen
	 */
	private void schreibeLeistungen (CSVWriter writer, ProfilTab tab) {
		for (int i = 0; i < tab.getZeilenAnzahl(); i++) {
			String[] eingaben = new String[4];
			eingaben[0] = tab.getValueAt(i, 0);
			eingaben[1] = tab.getValueAt(i, 1);
			eingaben[2] = tab.getValueAt(i, 2);
			eingaben[3] = tab.getValueAt(i, 9);
			writer.writeNext(eingaben);
		} 
	}
	
	/**
	 * Öffnen und Fehlerüberprüfung einer CSV
	 * @param pfad: Pfad an dem die CSV, die geprüft werden soll, liegt
	 * @return TRUE falls CSV erfolgreich verifiziert wurde
	 */
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
