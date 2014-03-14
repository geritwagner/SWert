package datei_operationen;

import java.io.*;
import java.util.LinkedList;
import au.com.bytecode.opencsv.*;

import model.*;

/**
 * Controller für das Speichern in CSV-Dateien
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta)
 */
public class DateiSpeichernCSVController {

	private String pfad;

	public void setPfad(String pfad) {
		this.pfad = pfad;
	}

	public boolean isSetPfad(){
		if (pfad != null && pfad != "")
			return true;
		return false;
	}
	
	/**
	 * Methode, die ein übergebenes Profil-Tab unter der Pfadangabe
	 * als CSV-Datei erstellt
	 * @return: TRUE für erfolgreiches Erstellen der CSV
	 * @throws IOException 
	 */	
	public void schreiben(Athlet athlet) throws IOException {
		     CSVWriter writer = new CSVWriter(new FileWriter(pfad), ';', '\0');
		     String[] entries = generateAthletenInfo(athlet);
		     writer.writeNext(entries);	     
		     schreibeLeistungen(writer,athlet.getLeistungen());
		     writer.close();
		     if (ValidatorHelper.isSyntacticallyCorrect(pfad)) {	    	 
		    	 // TODO: SyntaxException()
		     }		    
	}	

	private String[] generateAthletenInfo(Athlet athlet) {
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
			eingaben[1] = String.valueOf(aktuelleLeistung.getStreckenString());
			eingaben[2] = aktuelleLeistung.getBezeichnung();
			eingaben[3] = String.valueOf(aktuelleLeistung.getZeit());
			writer.writeNext(eingaben);
		} 
	}
	

}