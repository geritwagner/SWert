package datei_operationen;

import java.io.*;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import main.Hauptfenster;
import main.ProfilTab;
import model.Athlet;

/**
 * Dialog zum Auswählen des Names und Pfades einer zu speichernden CSV-Datei
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta)
 */
public class DateiSpeichern {
		
	private Hauptfenster mainFrame = Hauptfenster.aktuellesHauptfenster;
	private JFileChooser chooser;
	private FileFilter filter = new FileNameExtensionFilter("CSV Dateien","csv");	
	DateiSpeichernCSVController controller;
	Athlet athlet;
	
	
	public DateiSpeichern(Athlet athlet, String pfad) {
		controller = new DateiSpeichernCSVController();
		this.athlet = athlet;
		if (pfad != null)
			controller.setPfad(pfad);
	}

	public boolean isSetPfad(){
		return controller.isSetPfad();
	}
	
	public void setSpeicherPfad(String pfad){
		controller.setPfad(pfad);
	}
	
	public void speichern(boolean forceSpeichernUnter) throws IOException{
		if (!isSetPfad() || forceSpeichernUnter)
			setPfadFromUserDialog(athlet.getName());
		controller.schreiben(athlet);
	}
	
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
	
	public void setPfadFromUserDialog (String athletName) {		
		initFileChooser();
		chooser.removeChoosableFileFilter(chooser.getChoosableFileFilters()[0]);
        chooser.addChoosableFileFilter(filter);   

		String saveString = "Profil '"+athletName+"' speichern";
		if (chooser.showDialog(mainFrame.getContext(), saveString) == JFileChooser.APPROVE_OPTION){		
			String ausgewählterPfad = chooser.getSelectedFile().getAbsolutePath();
			if (ausgewählterPfad.contains(".csv")) {
				controller.setPfad(ausgewählterPfad);
			} else {				
				controller.setPfad(ausgewählterPfad+".csv");
			}		
		}
	}
}
