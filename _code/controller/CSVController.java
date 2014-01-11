package controller;

import java.io.FileReader;
import java.io.FileWriter;

import javax.swing.JOptionPane;

import main.Main;
import model.Leistung;
import view.ProfilTab;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

/**
 * Controller zum Handlen aller Aktionen, die die CSV-Dateien betreffen
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta)
 */
public class CSVController {

//----------------------- ÖFFENTLICHE METHODEN -----------------------
	/**
	 * Methode die eine CSV-Datei einliest und daraus ein Athleten-Profil erstellt;
	 * false wird zurückgegeben, falls dabei eine Fehler entsteht
	 * @param pfad: Pfad der einzulesenden CSV-Datei
	 * @return: TRUE für erfolgreiches Einlesen der CSV
	 */
	public boolean lesen (String pfad) {	
		StreckenController sController = new StreckenController();
		LeistungController leistungController = Main.leistungController;
		try{			
		    CSVReader reader = new CSVReader(new FileReader(pfad), ';', '\0');
		    String [] aktuelleZeile;
		    if (!verifizieren(pfad)) {
		    	reader.close();
		    	return false;
		    }
		    //Kopfzeile auslesen
		    aktuelleZeile = reader.readNext();
		    String name = aktuelleZeile[1];
		    long id = Long.parseLong(aktuelleZeile[0]);
		    if (Main.mainFrame.checkAthletGeöffnet(name,id)) {
		    	JOptionPane.showMessageDialog(Main.mainFrame.getContext(),
		    			"Das ausgewählte Athletenprofil ist bereit geöffnet!",
		    			"Athletenprofil bereits geöffnet",
						JOptionPane.WARNING_MESSAGE);
		    	reader.close();
		    	return true;
		    }
		    Main.mainFrame.createTab(name,id);		    
		    int aktivesTab = Main.mainFrame.tabbedPane.getSelectedIndex();
			ProfilTab tab = (ProfilTab) Main.mainFrame.tabbedPane.getComponentAt(aktivesTab);
		    
			//restlichen Leistungen auslesen
		    while ((aktuelleZeile = reader.readNext()) != null) {		    	
		    	String datum = aktuelleZeile[0];
		    	String strecke = aktuelleZeile[1];
		    	int streckenId = sController.getStreckenIdByString(strecke);
		    	String bezeichnung = aktuelleZeile[2];
		    	double geschwindigkeit = Double.parseDouble(aktuelleZeile[3]);
		    	Leistung aktuelleLeistung = leistungController.neueLeistung(streckenId, id, geschwindigkeit, bezeichnung, datum);
		    	tab.addZeile(aktuelleLeistung);
		    }
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
		     if (verifizieren(pfad)) {	    	 
		    	 return true;
		     } else {
		    	 return false;
		     }		    
	     }catch (Exception e) {
	    	 //TODO
	    	e.printStackTrace();
			return false;
		}
	}

//----------------------- PRIVATE METHODEN -----------------------
	/**
	 * Lest Name und ID des Athlten in dem gegebenen Tab aus 
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
	private boolean verifizieren (String pfad) {
		try{
			CSVReader reader = new CSVReader(new FileReader(pfad), ';', '\0');
			String[] aktuelleZeile;			
			//Überschrift prüfen
			aktuelleZeile = reader.readNext();
			if (aktuelleZeile.length != 4) {
				reader.close();
				return false;
			}			
			//Restlichen Leistungen prüfen
			while ((aktuelleZeile = reader.readNext()) != null) {
		       if (aktuelleZeile.length != 4) {
		    	   reader.close();
		    	   return false;
		       }
		    }
			reader.close();
			return true;		
		}catch(Exception e) {
			//TODO
			e.printStackTrace();
			return false;
		}		
	}
	
}
