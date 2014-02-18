package view;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import main.Main;

/**
 * Dialog zum Auswählen einer zu öffnenden CSV-Datei
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta)
 */
public class DateiPfadOeffnen {

//----------------------- VARIABLEN -----------------------	
	private JFileChooser chooser = new JFileChooser();
	private FileFilter filter = new FileNameExtensionFilter("CSV Dateien","csv");	
	
//----------------------- KONSTRUKTOREN -----------------------
	/**
	 * Standard-Konstruktor
	 */
	public DateiPfadOeffnen() {
		chooser.removeChoosableFileFilter(chooser.getChoosableFileFilters()[0]);
        chooser.addChoosableFileFilter(filter); 
	}
	
//----------------------- ÖFFENTLICHE METHODEN -----------------------	
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
