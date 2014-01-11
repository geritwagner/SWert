package view;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import main.Main;

/**
 * Dialog zum Auswählen des Names und Pfades einer zu speichernden CSV-Datei
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta)
 */
public class DateiPfadSpeichern {
	
//----------------------- VARIABLEN -----------------------	
	private MainFrame mainFrame = Main.mainFrame;
	private JFileChooser chooser;
	private FileFilter filter = new FileNameExtensionFilter("CSV Dateien","csv");	
	
//----------------------- KONSTRUKTOREN -----------------------	
	/**
	 * Konstruktor
	 */
	public DateiPfadSpeichern() {
		initFileChooser();
		chooser.removeChoosableFileFilter(chooser.getChoosableFileFilters()[0]);
        chooser.addChoosableFileFilter(filter);        
	}
	
//----------------------- PRIVATEN METHODEN -----------------------	
	/**
	 * Initialisiert den FileChooser, der zusätzlich prüft, ob der eingegebene Pfad bereits vorhanden ist
	 */
	private void initFileChooser() {
		chooser = new JFileChooser(){
			private static final long serialVersionUID = 1L;

			@Override
			public void approveSelection() {
				File file = getSelectedFile();
				if (file.exists()) {
					if (JOptionPane.showConfirmDialog(this, file.getName() + " ist bereits vorhanden." +
							System.getProperty("line.separator") + "Möchten Sie sie ersetzen?", "Speichern bestätigen",
							JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
						super.approveSelection();
						((ProfilTab) mainFrame.tabbedPane.getComponentAt(mainFrame.tabbedPane.getSelectedIndex())).setSpeicherStatus(true);						
					}
				} else {
					super.approveSelection();
					((ProfilTab) mainFrame.tabbedPane.getComponentAt(mainFrame.tabbedPane.getSelectedIndex())).setSpeicherStatus(true);
				}
			}
		};;
	}
	
//----------------------- ÖFFENTLICHE METHODEN -----------------------	
	/**
	 * Methode, die den Speicherpfad inklusive der Endung ".csv" als String zurückgibt
	 */
	public String save(String name) {		
		String saveString = "Profil '"+name+"' speichern";
		if (chooser.showDialog(mainFrame.getContext(), saveString) == JFileChooser.APPROVE_OPTION){		
			String ausgewählterPfad = chooser.getSelectedFile().getAbsolutePath();
			if (ausgewählterPfad.contains(".csv")) {
				return ausgewählterPfad;
			} else {				
				return ausgewählterPfad+".csv";
			}		
		} else {
			return null;
		}
	}
}
