package datei_operationen;

import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.*;
import javax.swing.filechooser.*;

import main.Main;

/**
 * Dialog zum öffnen einer Datei
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta)
 */
public class DateiPfadOeffnen {

	DateiOeffnenController controller;
	
	public DateiPfadOeffnen() throws FileNotFoundException, IOException, AlreadyOpenException, SyntaxException   {
		controller = new DateiOeffnenController(this);
		String pfad = open();
		controller.openAthletFromCSVFile(pfad);
		controller.openAthlet();
		release();
	}
	
	public String open() {
		JFileChooser chooser = new JFileChooser();
		chooser.removeChoosableFileFilter(chooser.getChoosableFileFilters()[0]);
		FileFilter filter = new FileNameExtensionFilter("CSV Dateien","csv");chooser.addChoosableFileFilter(filter); 
		if (chooser.showOpenDialog(Main.mainFrame.getContext()) == JFileChooser.APPROVE_OPTION){
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