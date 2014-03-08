package view;

import javax.swing.*;
import javax.swing.filechooser.*;

import main.Main;

/**
 * Dialog zum Auswählen einer zu öffnenden CSV-Datei
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta)
 */
public class DateiPfadOeffnen {

	private JFileChooser chooser = new JFileChooser();
	private FileFilter filter = new FileNameExtensionFilter("CSV Dateien","csv");	
	
	public DateiPfadOeffnen() {
		chooser.removeChoosableFileFilter(chooser.getChoosableFileFilters()[0]);
        chooser.addChoosableFileFilter(filter); 
	}
	
	/**
	 * Methode die den Pfad einer zu öffnenden Datei als String zurückgibt
	 */
	public String open() {         
        // Dialog zum Oeffnen von Dateien anzeigen
		if (chooser.showOpenDialog(Main.mainFrame.getContext()) == JFileChooser.APPROVE_OPTION){
			return chooser.getSelectedFile().getAbsolutePath();        
		}else {
			return null;
		}
	}
}