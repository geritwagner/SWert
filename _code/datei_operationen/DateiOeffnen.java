package datei_operationen;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.*;
import javax.swing.filechooser.*;

import main.Hauptfenster;

/**
 * Dialog zum öffnen einer Datei
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta)
 */
public class DateiOeffnen {

	DateiOeffnenController controller;
	
	public DateiOeffnen() throws FileNotFoundException, IOException, AlreadyOpenException, SyntaxException   {
		controller = new DateiOeffnenController(this);
		String pfad = getPfadFromUserDialog();
		controller.openAthletFromCSVFile(pfad);
		controller.openAthletInTab(pfad);
		release();
	}
	
	public String getPfadFromUserDialog() {
		JFileChooser chooser = new JFileChooser();
		chooser.removeChoosableFileFilter(chooser.getChoosableFileFilters()[0]);
		FileFilter filter = new FileNameExtensionFilter("CSV Dateien","csv");chooser.addChoosableFileFilter(filter); 
		if (chooser.showOpenDialog(Hauptfenster.aktuellesHauptfenster.getContext()) == JFileChooser.APPROVE_OPTION){
			return chooser.getSelectedFile().getAbsolutePath();        
		}else {
			return null;
		}
	}
	
	protected void release(){
		controller.release();
		controller = null;
	}
}