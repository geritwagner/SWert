package view;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import main.Main;

/**
 * Dialog zum Ausw�hlen einer zu �ffnenden CSV-Datei
 * @author Honors-WInfo-Projekt (Fabian B�hm, Alexander Puchta)
 */
public class DateiPfad�ffnen {

//----------------------- VARIABLEN -----------------------	
	private JFileChooser chooser = new JFileChooser();
	private FileFilter filter = new FileNameExtensionFilter("CSV Dateien","csv");	
	
//----------------------- KONSTRUKTOREN -----------------------
	/**
	 * Standard-Konstruktor
	 */
	public DateiPfad�ffnen() {
		chooser.removeChoosableFileFilter(chooser.getChoosableFileFilters()[0]);
        chooser.addChoosableFileFilter(filter); 
	}
	
//----------------------- �FFENTLICHE METHODEN -----------------------	
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
