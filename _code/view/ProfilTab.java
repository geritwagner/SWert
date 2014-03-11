package view;

import java.awt.event.*;
import java.awt.Font;
import java.util.*;
import javax.swing.*;
import javax.swing.RowSorter.*;
import javax.swing.event.*;
import javax.swing.table.*;
import net.miginfocom.swing.MigLayout;

import helper.*;
import main.Main;
import model.*;

public class ProfilTab extends JPanel implements TableModelListener {
	
	private static final long serialVersionUID = 1L;
	private static final int BOOLEAN_SPALTE = 7;

	private Athlet athlet;

	private MainFrame mainFrame = Main.mainFrame;
	
	private JLabel lblAthletName;
	private JButton btnBestzeiten;
	private JButton btnTrainingsbereich;
	private JButton btnLeistungskurve;
	private JScrollPane scrollPane;
	private JTextField textFieldSchwelle;
	private JCheckBox chckbxLeistungenAuswahl;
	private boolean leistungenAuswahlCheck = false;
	
	//TODO ggf. leistungenTabelle als View-Komponente auslagern?
	private JTable leistungenTabelle;
	private boolean verändert = false;
	public TableRowSorter<TableModel> sorter;
	private String speicherPfad;
	private JButton btnLeistungBearbeiten;
	private JButton btnLeistungLöschen;
	
	private boolean gespeichert = false;
	private boolean automatischeVerarbeitung = false;
	private boolean ausDialog = false;
	private JButton btnTabSchlieen;
	
	//----------------------- für presenter/controller -----------------------

	public ProfilTab(Athlet athlet) {
		// TODO: Athlet nicht speichern - nur darstellen!
		this.athlet = athlet;
		this.speicherPfad = null;
		initLayout(athlet.getName());
		setAnalysenVerfügbar(false);
		initJTable();
		setBearbeitenStatus(false);
	}
	
	public Athlet getAthlet(){
		return athlet;
	}
	
	public String getSpeicherPfad() {
		return speicherPfad;
	}
	
	public void bestzeitenButtonPressed(){
		new BestzeitenDialog(athlet);
	}

	public void leistungskurveButtonPressed(){
		mainFrame.diagrammController.DiagrammOeffnen();
	}
	
	public void trainingsBereichButtonPressed(){
		try {
			new TrainingsbereichDialog(athlet.getAnaerobeSchwelle());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Für die Berechnung der Trainingsbereiche müssen zwei Leistungen für die Berechnung" +
					"des Slope-Faktors ausgewählt werden.", "Leistungen auswählen",JOptionPane.ERROR_MESSAGE);
		}
	}
	
	// ----------------- Actions ------------------------------
	
	public void triggerTableChanged(int zeile, int spalte, Object data){
    	// TODO: logik und setter extrahieren (getLeistung -> setAuswahlforSlopeFaktor -> setTablechanges):

		if (automatischeVerarbeitung) {
        	return;
        }
		//Verhindern, dass mehr als 2 Leistungen ausgewählt werden
        if (spalte == BOOLEAN_SPALTE && !tabelleAuswahlZulässig()) {
        // if (spalte == booleanSpalte && !tabelleAuswahlZulässig()) {
        	automatischeVerarbeitung = true;
        	DefaultTableModel model = (DefaultTableModel) leistungenTabelle.getModel();
        	model.setValueAt(false, zeile, spalte);
        	automatischeVerarbeitung = false;
        	return;
        }
        aktualisiereSpeicherStatus(spalte);
	    uncheckAutoAuswahl(spalte);
        berechneWerte(); 
	}
	
	/**
	 * Prüft, ob die Auswahl richtig ist und gibt entsprechend eine Meldung zurück oder berechnet die Werte
	 */
	public void checkboxLeistungenAuswahlprüfenClicked() {		
		// TODO: refactor: check or calculate!
		if (chckbxLeistungenAuswahl.isSelected()) {
        	if (!automatischeAuswahlZulässig()) {
				JOptionPane.showMessageDialog(this, "Zum Berechnen der Werte werden zwei Leistungen mit unterschiedlicher Laufstrecke benötigt!"
						, "Laufstrecken identisch",JOptionPane.ERROR_MESSAGE);
				chckbxLeistungenAuswahl.setSelected(false);
        	} else {
        		// TODO: Unterschied?: leistungenAuswahlCheck, chckbxLeistungenAuswahl.isSelected(), automatischeVerarbeitung        		
        		leistungenAuswahlCheck = true;
        		tabelleCheckboxenFürSlopeFaktorAuswahlAufheben(); 
        		leistungenAuswahlCheck = false;
        		setLeistungAutomatisch();
        		chckbxLeistungenAuswahl.setSelected(true);
        		werteBerechnen();
        	}
    	}
	}

	/**
	 * Wählt die Leistungen über die kürzeste und längste Distanz für die Berechnung des Slope-Faktors aus.
	 */	
	private void setLeistungAutomatisch() {		
		int kürzereStrecke = 0;
		int längereStrecke = getZeilenAnzahl() - 1;

		tabelleCheckboxenFürSlopeFaktorAuswahlAufheben();		
		
		Leistung leistungKurzeStrecke = getLeistungInZeile(kürzereStrecke);
		Leistung leistungLangeStrecke = getLeistungInZeile(längereStrecke);

		athlet.resetLeistungAuswahlForSlopeFaktor();
		try {
			athlet.setLeistungToAuswahlForSlopeFaktor(leistungLangeStrecke);		
			athlet.setLeistungToAuswahlForSlopeFaktor(leistungKurzeStrecke);
			automatischeVerarbeitung = true;
			leistungenTabelle.setValueAt(true, kürzereStrecke, BOOLEAN_SPALTE);
			leistungenTabelle.setValueAt(true, längereStrecke, BOOLEAN_SPALTE);
			automatischeVerarbeitung = false;
		} catch (Exception e) {
			// TODO gleiche Leistung ausgewählt oder schon alle Leistungen für Berechnung des Slope-Faktors gesetzt
			e.printStackTrace();
		}
	}
	
	public void leistungBearbeitenPressed() {
		Leistung leistung = getLeistungInZeile(leistungenTabelle.getSelectedRow());
		if (leistung.getId_strecke() == -1) {
			@SuppressWarnings("unused")
			SchwellenDialog dialog = new SchwellenDialog(leistung);
		} else {
			@SuppressWarnings("unused")
			LeistungDialog dialog = new LeistungDialog(leistung);
		}
	}

	public void deleteZeileButtonPressed() {
		if (JOptionPane.showConfirmDialog(this, "Wollen Sie die Leistung wirklich löschen?", "Leistung löschen", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION){
			deleteLeistung();
		}
	}
	
	public void neueLeistungButtonPressed(){
		setBearbeitenStatus(false);
		@SuppressWarnings("unused")
		LeistungDialog dialog = new LeistungDialog();		
	}

	public void tabSchließen() {
		setBearbeitenStatus(false);
        int i = mainFrame.tabbedPane.getSelectedIndex();
        // TODO: welcher Fall ist i=-1 bzw !=-1 ??? ggf. schon gespeichert???
        if (i != -1) {
        	if (gespeichert) {
        		mainFrame.tabbedPane.remove(i); 
        		mainFrame.tabList.remove(i);
        	} else {
        		int nutzerauswahlSpeichern = JOptionPane.showConfirmDialog(this, "Wollen Sie die Änderungen am Profil '"+athlet.getName()+"' speichern?", "Achtung!", JOptionPane.YES_NO_CANCEL_OPTION);
        		if (nutzerauswahlSpeichern == 0) {
        			mainFrame.speichern();
        			mainFrame.tabbedPane.remove(i); 
            		mainFrame.tabList.remove(i);
        		} else if (nutzerauswahlSpeichern == 1) {
        			mainFrame.tabbedPane.remove(i); 
            		mainFrame.tabList.remove(i);
        		}
        	}
        }
	}
	
	// ------------------ Status functions ----------------------------------

	/**
	 * Boolean umschalten, falls Checkboxen der Tabelle geändert wurden
	 */
	private void aktualisiereSpeicherStatus(int spalte) {         
		if (spalte != BOOLEAN_SPALTE) {
        	setSpeicherStatus(false);
        }
	}
	
	public void setSpeicherPfad(String speicherPfad) {
		this.speicherPfad = speicherPfad;
	}	
	
	public boolean getSpeicherStatus() {
		return gespeichert;
	}

	public void setSpeicherStatus (boolean gespeichert) {
		this.gespeichert = gespeichert;
		int tabStelle = mainFrame.tabbedPane.getSelectedIndex();
		if (gespeichert) {
			mainFrame.tabbedPane.setTitleAt(tabStelle, athlet.getName());
		} else {
			mainFrame.tabbedPane.setTitleAt(tabStelle, "* "+athlet.getName());
		}
	}

	public void setBearbeitenStatus(boolean editable){
		if (editable){
			btnLeistungBearbeiten.setEnabled(true);
			btnLeistungLöschen.setEnabled(true);
	        mainFrame.leistungBearbeitenMenüVerfügbar();
	        mainFrame.leistungLöschenMenüVerfügbar();
		} else {
			leistungenTabelle.getSelectionModel().clearSelection();
			mainFrame.leistungBearbeitenMenüAusgrauen();
			mainFrame.leistungLöschenMenüAusgrauen();
			btnLeistungBearbeiten.setEnabled(false);
			btnLeistungLöschen.setEnabled(false);
		}
	}
	
	private void setAnalysenVerfügbar(boolean AnalyseVerfügbar){
		if(AnalyseVerfügbar){
			btnBestzeiten.setEnabled(true);
			btnLeistungskurve.setEnabled(true);
			btnTrainingsbereich.setEnabled(true);										
		} else {
			btnBestzeiten.setEnabled(false);
			btnLeistungskurve.setEnabled(false);
			btnTrainingsbereich.setEnabled(false);										
		}
	}
	
	// ----------------------------- Getter and Setter -----------------------
	
	/**
	 * Methoden um eine neue Zeile in der Tabelle hinzuzufügen oder bestehende zu löschen
	 */
	public void addZeile(Leistung leistung) {
		// TODO: athlet.addLeistung in controller extrahieren!
		athlet.addLeistung(leistung);
		
		DefaultTableModel model = (DefaultTableModel) leistungenTabelle.getModel();
		model.addRow(leistung.getObjectDataForTable());
		
		// TODO: ggf. wieder einkommentieren:
//		leistungsAuswahlBeiZeileHinzufügen();
	}
	
	private Leistung[] getAusgewählteLeistungenForSlopeFaktorFromCheckbox() {
		int zeilenAnzahl = getZeilenAnzahl();
		int spalte = BOOLEAN_SPALTE;
		Leistung[] ausgewählteLeistungen = new Leistung[2];
		int counter = 0;
		for (int i = 0; i < zeilenAnzahl; i++) {
			if (true == getBooleanAt(i, spalte) && counter < 2){
				ausgewählteLeistungen[counter] = getLeistungInZeile(i);
				counter++;
			} 
		}
		return ausgewählteLeistungen;
	}
	
	/**
	 * Löscht eine ausgewählte Zeile, wenn von Schwellen- oder Leistungsdialog gefordert,
	 * ohne eine automatische Auswahl triggern zu können
	 */
	// is called from SchwellenDialog
	public void deleteZeileAusDialog() {
		ausDialog = true;
		deleteLeistung();
		ausDialog = false;
	}
	
	/**
	 * Löscht eine ausgewählte Leistung 
	 */
	public void deleteLeistung() {
		DefaultTableModel model = (DefaultTableModel) leistungenTabelle.getModel();
		int rowToDelete = leistungenTabelle.getSelectedRow();
		
		// deckt den Fall ab, wenn neue Leistungen hinzugefügt werden (es wird immer die markierte gelöscht und durch die neue
		// ersetzt - bei neuen Leistungen ist aber standardmäßig keine bzw. Zeile -1 markiert, die aber nicht gelöscht werden kann)
		// TODO: sollte eleganter umgesetzt werden!
		if (-1 == rowToDelete) {
			return;
		}
		Leistung leistungToRemove = getLeistungInZeile(rowToDelete);

		model.removeRow(leistungenTabelle.convertRowIndexToModel(rowToDelete));	
		
		// TODO: umstrukturieren!!
		// ausDialog: wird dann auf true gesetzt, wenn deleteLeistungDialog() vom LeistungDialog aufgerufen wird
		if (!ausDialog) {

			//athlet.removeLeistungFromAuswahlForSlopeFaktor(leistungToRemove);
			athlet.removeLeistung(leistungToRemove);
			leistungsAuswahlBeiZeileLöschenBeiAutomatischerAuswahlNeuBerechnen();
		}
			
		setBearbeitenStatus(false);		
	}
	
	/**
	 * Sobald 2 Checkboxen ausgewählt sind, wird das Auswählen einer dritten wieder rückgängig gemacht
	 */
	private void uncheckAutoAuswahl(int spalte) {
        if (!verändert && spalte == BOOLEAN_SPALTE && !leistungenAuswahlCheck) {
        	verändert = true;
        	chckbxLeistungenAuswahl.setSelected(false);
        	verändert = false;        	
        } 
 	}
	
	// ----------------------------- Validators -----------------------
	
	/**
	 * Prüft, ob mehr als 2 Haken in die letzte Spalte gesetzt werden wollen
	 */	
	private boolean tabelleAuswahlZulässig() {
		int zeilenAnzahl = getZeilenAnzahl();
		int zähler = 0;
		
		for (int i = 0; i < zeilenAnzahl; i++) {
			if (true == getBooleanAt(i, BOOLEAN_SPALTE)){
				zähler++;
			}
			if (zähler >= 3) {
				return false;
			}			
		}
		return true;	
	}
	
	/**
	 * Prüft, ob genau 2 Haken in der letzte Spalte gesetzt sind
	 */	
	private boolean tabelleAuswahlRichtig() {
		try{
			int zeilenAnzahl = getZeilenAnzahl();
			int zähler = 0;
			
			for (int i = 0; i < zeilenAnzahl; i++) {
				if (true == getBooleanAt(i, BOOLEAN_SPALTE)){
					zähler++;
				}
			}
			if (zähler == 2) {
				return true;
			} else {			
				return false;	
			}
		} catch (Exception e) {
			return false;
		}
	}
	

	/**
	 * Prüft, ob eine automatische Auswahl zulässig ist
	 */
	private boolean automatischeAuswahlZulässig() {
		int zeilenAnzahl = getZeilenAnzahl();
		int streckenIdSpalte = 8;
		
		if (!chckbxLeistungenAuswahl.isSelected()) {
			return false;
		}
		
		if (zeilenAnzahl>1) {
			for (int i = 0; i < zeilenAnzahl-1; i++) {
				int zahl1 = getIntAt(i, streckenIdSpalte);				
				for (int j = i; j < zeilenAnzahl; j++) {
					int zahl2 = getIntAt(j, streckenIdSpalte);
					if (zahl1 != zahl2) {
						return true;
					}
					if (zahl1 == -1 && zahl2 == -1) {
						Leistung zahl1Leistung = getLeistungInZeile(i);
						Leistung zahl2Leistung = getLeistungInZeile(j);
						double zahl1Strecke = zahl1Leistung.getStrecke();
						double zahl2Strecke = zahl2Leistung.getStrecke();
						if (zahl1Strecke != zahl2Strecke) {
							return true;
						}
					}
				}
			}
			return false;
		} else {
			return false;
		}
	}
	
	// ------------------------ to consolidate --------------------------
	/**
	 * Hebt die Auswahl auf
	 */	
	private void tabelleCheckboxenFürSlopeFaktorAuswahlAufheben() {
		int zeilenAnzahl = getZeilenAnzahl();
		for (int i = 0; i < zeilenAnzahl; i++) {
			if (true == getBooleanAt(i, BOOLEAN_SPALTE)){
				leistungenTabelle.setValueAt(false, i, BOOLEAN_SPALTE);
			}
		}		
	}
	
	/**
	 * Prüft, ob automatische Auswahl bei einer neuen Zeile durchgeführt werden soll
	 */
	private void leistungsAuswahlBeiZeileHinzufügen() {
		if (automatischeAuswahlZulässig()) {
			checkboxLeistungenAuswahlprüfenClicked();
		}	
	}
	
	/**
	 * Schaltet eine automatische Auswahl ab, falls diese nicht möglich ist und
	 * berechnet ansonsten die neuen Werte
	 */
	private void leistungsAuswahlBeiZeileLöschenBeiAutomatischerAuswahlNeuBerechnen() {
		if (!automatischeAuswahlZulässig()) {
			chckbxLeistungenAuswahl.setSelected(false);
		} else {
			checkboxLeistungenAuswahlprüfenClicked();
		}
	}
	
	/**
	 * Erstellt Referenz-Leistungen mit den beiden manuell gewählten Leistungen und berechnet dann die Werte
	 */	
	private void berechneWerte() {
        if (!chckbxLeistungenAuswahl.isSelected()) {  
    		if (tabelleAuswahlRichtig()){
    			athlet.resetLeistungAuswahlForSlopeFaktor();
        		try {
					athlet.setLeistungToAuswahlForSlopeFaktor(getAusgewählteLeistungenForSlopeFaktorFromCheckbox()[0]);
	               	athlet.setLeistungToAuswahlForSlopeFaktor(getAusgewählteLeistungenForSlopeFaktorFromCheckbox()[1]);
				} catch (Exception e) {
					// TODO gleiche Leistung ausgewählt oder schon alle Leistungen für Berechnung des Slope-Faktors gesetzt
					e.printStackTrace();
				}
    			werteBerechnen();        		
        	}
        }
	}
	
	/**
	 * Berechnet die nötigen Werte bzw. gibt entsprechende Fehlermeldungen zurück
	 */
	private void werteBerechnen() {
		// TODO: split and simplify? ggf. mit exceptions oder status-codes arbeiten, die im model abgefragt werden...
		// gleiche Leistungen: sollte vorher schon abgefangen werdne?!??!
		// if (athlet.getSlopeFaktor() ==- 1) {
		if ("set" != athlet.getSlopeFaktorStatus()) {
			JOptionPane.showMessageDialog(this, "Zum Berechnen der Werte werden zwei Leistungen mit unterschiedlicher Laufstrecke benötigt!"
					, "Laufstrecken identisch",JOptionPane.ERROR_MESSAGE);
			tabelleCheckboxenFürSlopeFaktorAuswahlAufheben();
			chckbxLeistungenAuswahl.setSelected(false);
			return;
		} 
		double anaerobeSchwelle;
		try {
			anaerobeSchwelle = athlet.getAnaerobeSchwelle();
			LeistungHelper l = new LeistungHelper();
			textFieldSchwelle.setText(l.parseSecInMinutenstring(anaerobeSchwelle));
			
			setAnalysenVerfügbar(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
		
	
	
	
	// ---------------------------------- TABLE-METHODS: GET/SET ----------------------------
	
	@Override
	public void tableChanged(TableModelEvent e) {
        int zeile = e.getFirstRow();
        int spalte = e.getColumn();
        // beim Laden eines neuen Profils: trigerTableChanged nicht aufrufen
        if (spalte<0 || zeile<0){
        	return;
        }
		TableModel model = (TableModel)e.getSource();
		Object data = model.getValueAt(zeile, spalte);
        triggerTableChanged(zeile, spalte, data);
    }
	
	private Leistung getLeistungInZeile(int zeile) {
		zeile = leistungenTabelle.convertRowIndexToModel(zeile);
		String datum = getStringAt(zeile, 0);
		String streckenlaenge = getStringAt(zeile, 1);
		int streckenId = Strecken.getStreckenIdByString(streckenlaenge);
		String bezeichnung = getStringAt(zeile, 2);
		LeistungHelper l = new LeistungHelper();
		double zeit =   l.parseZeitInSec(getStringAt(zeile, 3));
		int streckenlänge = Strecken.getStreckenlaengeById(streckenId);
		double geschwindigkeit = l.berechneGeschwindigkeit(streckenlänge, zeit);
		Leistung leistung = new Leistung(streckenId, athlet.getId(), bezeichnung, datum, geschwindigkeit);
		return leistung;
	}
	
	private int getZeilenAnzahl() {
		return leistungenTabelle.getRowCount();
	}
	
	private String getStringAt (int zeile, int spalte) {
		DefaultTableModel model = (DefaultTableModel) leistungenTabelle.getModel();
		return (String) model.getValueAt(zeile, spalte);		
	}
	
	private boolean getBooleanAt (int zeile, int spalte) {
		DefaultTableModel model = (DefaultTableModel) leistungenTabelle.getModel();
		return (boolean) model.getValueAt(zeile, spalte);		
	}
	
	private int getIntAt (int zeile, int spalte) {
		DefaultTableModel model = (DefaultTableModel) leistungenTabelle.getModel();
		return (int) model.getValueAt(zeile, spalte);		
	}
	
	private double getDoubleAt(int zeile, int spalte){
		DefaultTableModel model = (DefaultTableModel) leistungenTabelle.getModel();
		return Double.parseDouble((String) model.getValueAt(zeile, spalte));				
	}

	//----------------------- view darstellung -----------------------

	private void initLayout(String Athletenname) {
		
		setLayout(new MigLayout("", "[grow]", "[grow][][][grow]"));
		
		JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		add(splitPane, "cell 0 0,grow");
		
		
		JPanel panel = new JPanel();
		splitPane.setLeftComponent(panel);
		panel.setLayout(new MigLayout("", "[100][100][100][grow][100]", "[][][][][][]"));
		
		btnTabSchlieen = new JButton("");
		btnTabSchlieen.setToolTipText("Tab schlie\u00DFen");
		btnTabSchlieen.setIcon(new ImageIcon(ProfilTab.class.getResource("/bilder/Abbrechen_16x16.png")));
		btnTabSchlieen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int i = mainFrame.tabbedPane.getSelectedIndex();
				ProfilTab tab = (ProfilTab) mainFrame.tabbedPane.getComponentAt(i);
				tab.tabSchließen();
			}
		});
		panel.add(btnTabSchlieen, "cell 4 0,alignx right");
		
		lblAthletName = new JLabel("<html><u>"+Athletenname+"</u></<html>");
		lblAthletName.setFont(new Font("Tahoma", Font.BOLD, 16));
		panel.add(lblAthletName, "cell 0 0");
		
		JLabel lblAnaerobeSchwelle = new JLabel("Aerob/Anaerobe Schwelle:");
		panel.add(lblAnaerobeSchwelle, "cell 0 2,alignx left");
		
		textFieldSchwelle = new JTextField();
		textFieldSchwelle.setEditable(false);
		panel.add(textFieldSchwelle, "cell 1 2,growx");
		textFieldSchwelle.setColumns(10);
		
		chckbxLeistungenAuswahl = new JCheckBox("Leistungen automatisch w\u00E4hlen");
		// TODO: entsprechend der Logik ggf. wieder standardmäßig auf true setzen...
		chckbxLeistungenAuswahl.setSelected(false);
		chckbxLeistungenAuswahl.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { 
            	checkboxLeistungenAuswahlprüfenClicked();            	
            }
        });
		panel.add(chckbxLeistungenAuswahl, "cell 2 2");
		
		JSeparator separator = new JSeparator();
		panel.add(separator, "cell 0 4 5 1,growx");
		
		btnBestzeiten = new JButton("Mögliche Bestzeiten");
		btnBestzeiten.setIcon(new ImageIcon(ProfilTab.class.getResource("/bilder/Pokal_24x24.png")));
		btnBestzeiten.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				bestzeitenButtonPressed();				
			}
		});
		panel.add(btnBestzeiten, "cell 0 5,alignx right");
		
		btnTrainingsbereich = new JButton("Trainingsbereiche");
		btnTrainingsbereich.setIcon(new ImageIcon(ProfilTab.class.getResource("/bilder/Berechnen_24x24.png")));
		btnTrainingsbereich.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				trainingsBereichButtonPressed();
			}
		});
		panel.add(btnTrainingsbereich, "cell 1 5");
		
		btnLeistungskurve = new JButton("Leistungskurve als Grafik");
		btnLeistungskurve.setIcon(new ImageIcon(ProfilTab.class.getResource("/bilder/Diagramm_24x24.png")));
		btnLeistungskurve.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				leistungskurveButtonPressed();
			}
		});
		panel.add(btnLeistungskurve, "cell 2 5,alignx left");

		scrollPane = new JScrollPane();
		splitPane.setRightComponent(scrollPane);
	}
	
	private void initJTable() {
		
		leistungenTabelle = initLeistungsTabelle(leistungenTabelle);	
		scrollPane.setViewportView(leistungenTabelle);
		
		JButton btnLeistungHinzufügen = new JButton("Leistung hinzuf\u00FCgen");
		btnLeistungHinzufügen.setIcon(new ImageIcon(ProfilTab.class.getResource("/bilder/NeueLeistung_24x24.png")));
		add(btnLeistungHinzufügen, "flowx,cell 0 2");
		btnLeistungHinzufügen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				neueLeistungButtonPressed();
			}
		});
		
		btnLeistungBearbeiten = new JButton("Leistung bearbeiten");
		btnLeistungBearbeiten.setIcon(new ImageIcon(ProfilTab.class.getResource("/bilder/EditLeistung_24x24.png")));
		add(btnLeistungBearbeiten, "cell 0 2");
		btnLeistungBearbeiten.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				leistungBearbeitenPressed();
			}
		});
		
		btnLeistungLöschen = new JButton("Leistung l\u00F6schen");
		btnLeistungLöschen.setIcon(new ImageIcon(ProfilTab.class.getResource("/bilder/LeistungLoeschen_24x24.png")));
		add(btnLeistungLöschen, "cell 0 2");
		btnLeistungLöschen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteZeileButtonPressed();
			}
		});
	}
	
	// hier wird null übergeben, den parameter könnte man folglich löschen, er stellt jedoch die Aufruf-Reihenfolge der Methoden sicher...
	private JTable initLeistungsTabelle(JTable leistungenTabelle) {
		// Spalten definieren; die letzten beiden Spalten sind unsichtbar
		// BOOLEAN_SPALTE wird auf 7 gesetzt - müsste angepasst werden, wenn sich solumnNames ändert.
		final String[] columnNames = {"Datum",
				"Streckenlänge",
                "Bezeichnung",
                "Zeit",
                "km/h",
                "m/s",
                "1.000m Zeit",
                "für Slope-Faktor ausgewählt",
                "StreckenId",
                "s/km"};
		
		//Anfangswerte definieren
		Object[][] data = null;
		
		leistungenTabelle = new JTable(new DefaultTableModel(data,columnNames)) {
			private static final long serialVersionUID = -3228449157468360581L;
						
			@Override
			public Class<?> getColumnClass(int columnIndex) { 
				if (columnIndex == BOOLEAN_SPALTE) { 
					return Boolean.class; 
				} 
				if (columnIndex == 8) { 
					return Integer.class; 
				}
				if (columnIndex == 9) {
					return String.class;
				}
				else 
					return super.getColumnClass(columnIndex); 
			}
			
			//nur mit der letzten Spalte (Kästchen) soll Interaktion stattfinden 
			@Override
			public boolean isCellEditable(int zeile, int spalte) {
              	if (spalte == BOOLEAN_SPALTE) {
            		return true;
            	} else {
            		return false;
            	}
            }            
        };        
        //Listener und Sortierung definieren
        leistungenTabelle.getModel().addTableModelListener(this);
        leistungenTabelle.getSelectionModel().addListSelectionListener(new TabelleLeistungsListener(leistungenTabelle,this));        
        leistungenTabelle.setSelectionMode(0);
		leistungenTabelle.setFillsViewportHeight(true);		
		leistungenTabelle.getTableHeader().setReorderingAllowed(false);
		
		leistungenTabelle.getColumnModel().getColumn(columnNames.length-2).setMinWidth(0);
		leistungenTabelle.getColumnModel().getColumn(columnNames.length-2).setMaxWidth(0);
		leistungenTabelle.getColumnModel().getColumn(columnNames.length-2).setWidth(0);
		leistungenTabelle.getColumnModel().getColumn(columnNames.length-1).setMinWidth(0);
		leistungenTabelle.getColumnModel().getColumn(columnNames.length-1).setMaxWidth(0);
		leistungenTabelle.getColumnModel().getColumn(columnNames.length-1).setWidth(0);
		        
		sorter = new TableRowSorter<TableModel>();
        leistungenTabelle.setRowSorter(sorter);
        sorter.setModel(leistungenTabelle.getModel());               
        sorter.setComparator(8, new IntegerComparator());
        List<SortKey> sortKeys = new ArrayList<SortKey>();
        sortKeys.add(new RowSorter.SortKey(columnNames.length-2, SortOrder.ASCENDING));
        
        sorter.setSortKeys(sortKeys);
		sorter.sort();
		
		for (int i = 0; i < columnNames.length; i++) {
			sorter.setSortable(i, false);
		}
		return leistungenTabelle;
	}
}