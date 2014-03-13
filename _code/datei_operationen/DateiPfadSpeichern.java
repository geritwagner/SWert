package datei_operationen;

import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import view.MainFrame;
import view.ProfilTab;
import main.Main;

/**
 * Dialog zum Ausw�hlen des Names und Pfades einer zu speichernden CSV-Datei
 * @author Honors-WInfo-Projekt (Fabian B�hm, Alexander Puchta)
 */
public class DateiPfadSpeichern {
	
	private MainFrame mainFrame = Main.mainFrame;
	private JFileChooser chooser;
	private FileFilter filter = new FileNameExtensionFilter("CSV Dateien","csv");	
	
	public DateiPfadSpeichern() {
		initFileChooser();
		chooser.removeChoosableFileFilter(chooser.getChoosableFileFilters()[0]);
        chooser.addChoosableFileFilter(filter);        
	}

	private void initFileChooser() {
		chooser = new JFileChooser(){
			private static final long serialVersionUID = 1L;

			@Override
			public void approveSelection() {
				File file = getSelectedFile();
				if (file.exists()) {
					if (JOptionPane.showConfirmDialog(this, file.getName() + " ist bereits vorhanden." +
							System.getProperty("line.separator") + "M�chten Sie sie ersetzen?", "Speichern best�tigen",
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
	
	public String getDateiSpeichernInfo(String name) {		
		String saveString = "Profil '"+name+"' speichern";
		if (chooser.showDialog(mainFrame.getContext(), saveString) == JFileChooser.APPROVE_OPTION){		
			String ausgew�hlterPfad = chooser.getSelectedFile().getAbsolutePath();
			if (ausgew�hlterPfad.contains(".csv")) {
				return ausgew�hlterPfad;
			} else {				
				return ausgew�hlterPfad+".csv";
			}		
		} else {
			return null;
		}
	}
}
