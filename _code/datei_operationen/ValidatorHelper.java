package datei_operationen;

import java.io.FileReader;
import java.io.IOException;

import au.com.bytecode.opencsv.CSVReader;

public abstract class ValidatorHelper {

	protected static boolean isSyntacticallyCorrect (String pfad) {
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
	
	private static boolean isSyntacticallyCorrectHeading(CSVReader reader) throws IOException{
		String[] aktuelleZeile;	
		aktuelleZeile = reader.readNext();
		if (aktuelleZeile.length != 4) {
			return false;
		}
		return true;
	}
	
	private static boolean isSyntacticallyCorrectLeistungen(CSVReader reader) throws IOException{
		String[] aktuelleZeile;	
		while ((aktuelleZeile = reader.readNext()) != null) {
	       if (aktuelleZeile.length != 4) {
	    	   return false;
	       }
	    }
		return true;
	}	
}
