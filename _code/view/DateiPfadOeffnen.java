package view;

import javax.swing.*;
import javax.swing.filechooser.*;

import main.Main;

/**
 * Dialog zum Ausw�hlen einer zu �ffnenden CSV-Datei
 * @author Honors-WInfo-Projekt (Fabian B�hm, Alexander Puchta)
 */
public class DateiPfadOeffnen {

	private JFileChooser chooser = new JFileChooser();
	private FileFilter filter = new FileNameExtensionFilter("CSV Dateien","csv");	
	
	public DateiPfadOeffnen() {
		chooser.removeChoosableFileFilter(chooser.getChoosableFileFilters()[0]);
        chooser.addChoosableFileFilter(filter); 
	}
	
	/**
	 * Methode die den Pfad einer zu �ffnenden Datei als String zur�ckgibt
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