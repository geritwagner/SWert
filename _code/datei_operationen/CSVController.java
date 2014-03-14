package datei_operationen;

import java.io.*;
import java.util.LinkedList;
import au.com.bytecode.opencsv.*;

import model.*;

/**
 * Controller f�r alle Aktionen, die die CSV-Dateien betreffen
 * @author Honors-WInfo-Projekt (Fabian B�hm, Alexander Puchta)
 */
public class CSVController {

	
	
	/**
	 * Methode, die ein �bergebenes Profil-Tab unter der Pfadangabe
	 * als CSV-Datei erstellt
	 * @return: TRUE f�r erfolgreiches Erstellen der CSV
	 */	
	public boolean schreiben(String pfad, Athlet athlet) {
		try{
		     CSVWriter writer = new CSVWriter(new FileWriter(pfad), ';', '\0');
		     String[] entries = getAthletenInfo(athlet);
		     writer.writeNext(entries);	     
		     schreibeLeistungen(writer,athlet.getLeistungen());
		     writer.close();
		     if (ValidatorHelper.isSyntacticallyCorrect(pfad)) {	    	 
		    	 return true;
		     } else {
		    	 return false;
		     }		    
	     }catch (Exception e) {
	    	e.printStackTrace();
			return false;
		}
	}	

	private String[] getAthletenInfo(Athlet athlet) {
		String[] athletInfo = new String[4];
		athletInfo[0] = String.valueOf(athlet.getId());
		athletInfo[1] = athlet.getName();
		return athletInfo;
	}

	private void schreibeLeistungen (CSVWriter writer, LinkedList<Leistung> leistungen) {
		// TODO: auch die getObjectData-Methode verwenden?
		// TODO: besser lesbare Formate verwenden (1.000m und 12 km/h) - dann m�sste aber die reader-Methoden ebenfalls angepasst werden.
		
		for (Leistung aktuelleLeistung : leistungen){
			String[] eingaben = new String[4];
			eingaben[0] = aktuelleLeistung.getDatum();
			eingaben[1] = String.valueOf(aktuelleLeistung.getStreckenString());
			eingaben[2] = aktuelleLeistung.getBezeichnung();
			eingaben[3] = String.valueOf(aktuelleLeistung.getZeit());
			writer.writeNext(eingaben);
		} 
	}
	

}