package datei_operationen;

import java.io.*;
import java.util.LinkedList;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import au.com.bytecode.opencsv.CSVWriter;

import main.Hauptfenster;
import main.ProfilTab;
import model.Athlet;
import model.Leistung;

/**
 * Dialog zum Auswählen des Names und Pfades einer zu speichernden CSV-Datei
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta)
 */
public class DateiSpeichern {
		
	private Hauptfenster mainFrame = Hauptfenster.aktuellesHauptfenster;
	private JFileChooser chooser;
	private FileFilter filter = new FileNameExtensionFilter("CSV Dateien","csv");	
	Athlet athlet;
		
	public DateiSpeichern(Athlet athlet) {
		this.athlet = athlet;
	}
	
	public void speichern(boolean forceSpeichernUnter) throws IOException{
		if (!isSetPfad() || forceSpeichernUnter)
			setPfadFromUserDialog();
		schreiben(athlet);
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
						// TODO: ggf. über observer?
						((ProfilTab) mainFrame.tabbedPane.getComponentAt(mainFrame.tabbedPane.getSelectedIndex())).setSpeicherStatus(true);						
					}
				} else {
					super.approveSelection();
					((ProfilTab) mainFrame.tabbedPane.getComponentAt(mainFrame.tabbedPane.getSelectedIndex())).setSpeicherStatus(true);
				}
			}
		};
		chooser.removeChoosableFileFilter(chooser.getChoosableFileFilters()[0]);
        chooser.addChoosableFileFilter(filter);   
	}
	
	public void setPfadFromUserDialog () {		
		initFileChooser();
		
		String info = "Profil '"+ athlet.getName() +"' speichern";
		if (chooser.showDialog(mainFrame, info) == JFileChooser.APPROVE_OPTION){	
			String ausgewählterPfad = chooser.getSelectedFile().getAbsolutePath();
			if ( ! ausgewählterPfad.contains(".csv")) {
				ausgewählterPfad += ".csv";
			}
			athlet.setSpeicherpfad(ausgewählterPfad);
		}
	}
	
	public boolean isSetPfad(){
		String pfad = athlet.getSpeicherpfad();
		if (pfad != null && pfad != "")
			return true;
		return false;
	}
	
	/**
	 * Methode, die ein übergebenes Profil-Tab unter der Pfadangabe
	 * als CSV-Datei erstellt
	 * @throws IOException 
	 */	
	public void schreiben(Athlet athlet) throws IOException {
	     String pfad = athlet.getSpeicherpfad();
	     CSVWriter writer = new CSVWriter(new FileWriter(pfad), ';', '\0');
	     String[] entries = generateAthletenInfo(athlet);
	     writer.writeNext(entries);	     
	     schreibeLeistungen(writer,athlet.getLeistungen());
	     writer.close();
	     if (ValidatorHelper.isSyntacticallyCorrect(pfad)) {	    	 
	    	 // TODO: SyntaxException()
	     }		    
	}	

	private String[] generateAthletenInfo(Athlet athlet) {
		String[] athletInfo = new String[4];
		athletInfo[0] = String.valueOf(athlet.getId());
		athletInfo[1] = athlet.getName();
		return athletInfo;
	}

	private void schreibeLeistungen (CSVWriter writer, LinkedList<Leistung> leistungen) {
		for (Leistung aktuelleLeistung : leistungen){
			String[] eingaben = new String[4];
			eingaben[0] = aktuelleLeistung.getDatum();
			eingaben[1] = String.valueOf(aktuelleLeistung.getStreckenString());
			eingaben[2] = aktuelleLeistung.getBezeichnung();
			eingaben[3] = String.valueOf(aktuelleLeistung.getZeitString());
			writer.writeNext(eingaben);
		} 
	}
}