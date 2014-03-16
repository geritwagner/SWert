package main;

import java.awt.event.*;
import java.awt.Font;
import java.io.IOException;
import java.util.*;
import javax.swing.*;
import javax.swing.RowSorter.*;
import javax.swing.event.*;
import javax.swing.table.*;

import net.miginfocom.swing.MigLayout;

import globale_helper.*;
import model.*;

public class ProfilTab extends JPanel implements TableModelListener, Observer {
	
	private static final long serialVersionUID = 1L;
	private static final int BOOLEAN_SPALTE = 7;
	
	private JLabel lblAthletName;
	private JButton btnBestzeiten;
	private JButton btnTrainingsbereich;
	private JButton btnLeistungskurve;
	private JScrollPane scrollPane;
	private JTextField textFieldSchwelle;
	private JCheckBox chckbxLeistungenAuswahl;
	private JTable leistungenTabelle;
	public TableRowSorter<TableModel> sorter;
	private JButton btnLeistungBearbeiten;
	private JButton btnLeistungLöschen;
	private JButton btnTabSchlieen;
	
	private boolean gespeichert = false;
	private boolean automatischeVerarbeitung = false;
	private Hauptfenster mainFrame = Hauptfenster.aktuellesHauptfenster;
	private Athlet athlet;
	private ProfilTabController controller;
	private AthletenListe athletenListe;

	// TODO: auslagern!?!?!??
	public Athlet getAthlet(){return athlet;}
	protected boolean getSpeicherStatus() {	return gespeichert;}
	
	public ProfilTab(AthletenListe athletenliste, Athlet athlet) {
		this.athletenListe = athletenliste;
		this.athlet = athlet;
		controller = new ProfilTabController(athletenliste, athlet, this);
		initLayout(athlet.getName());
		setAnalysenVerfügbar(false);
		initJTable();
		setAlleLeistungen();
		athlet.addObserver(this);
		try {
			controller.automatischAuswählen();
			chckbxLeistungenAuswahl.setSelected(true);
		} catch (Exception e) {
		}
		setLeistungBearbeitenAvailable(false);
	}
		
	public void triggerTableChanged(int zeileView, int spalte, Object data){
		// automatischeVerarbeitung: bricht den Methodenaufruf hier ab.
		if (automatischeVerarbeitung)
        	return;
        if (spalte == BOOLEAN_SPALTE )
        	auswahlFürSlopeFaktorÄndern(zeileView, spalte, (boolean) data);
	}
	
	private void auswahlFürSlopeFaktorÄndern (int zeileView, int spalte, boolean setTo) {
    	automatischeVerarbeitung = true;
    	Leistung leistung = getLeistungInZeile(zeileView);
    	try{
    		controller.auswahlFürSlopeFaktorÄndern(leistung, setTo);
	    	chckbxLeistungenAuswahl.setSelected(false);
    	} catch (ThreeLeistungenForSlopeFaktorException e){
        	DefaultTableModel tableModel = (DefaultTableModel) leistungenTabelle.getModel();
        	tableModel.setValueAt(false, sorter.convertRowIndexToModel(zeileView), spalte);
			JOptionPane.showMessageDialog(this, "Zum Berechnen der Werte dürfen nur zwei Leistungen ausgewählt werden!"
					, "Zwei Leistungen auswählen",JOptionPane.ERROR_MESSAGE);
    	} catch (GleicheStreckeException e){
        	DefaultTableModel tableModel = (DefaultTableModel) leistungenTabelle.getModel();
        	tableModel.setValueAt(false, sorter.convertRowIndexToModel(zeileView), spalte);
			JOptionPane.showMessageDialog(this, "Zum Berechnen der Werte müssen zwei Leistungen über unterschiedliche " +
					"Distanzen ausgewählt werden!", "Unterschiedliche Strecken wählen",JOptionPane.ERROR_MESSAGE);
    	}
    	automatischeVerarbeitung = false;
	}
	
	private void checkboxLeistungenAutomatischWählenClicked() {		
		try{
			if (chckbxLeistungenAuswahl.isSelected()){
				controller.automatischAuswählen();
			}
    	} catch (ThreeLeistungenForSlopeFaktorException e){
			JOptionPane.showMessageDialog(this, "Zum Berechnen der Werte dürfen nur zwei Leistungen ausgewählt werden!"
					, "Zwei Leistungen auswählen",JOptionPane.ERROR_MESSAGE);
    	} catch (GleicheStreckeException e){
			JOptionPane.showMessageDialog(this, "Zum Berechnen der Werte müssen zwei Leistungen über unterschiedliche " +
					"Distanzen ausgewählt werden!", "Unterschiedliche Strecken wählen",JOptionPane.ERROR_MESSAGE);
    	}
	}
		
	protected void speichernClicked(boolean forceSpeichernUnter){
		try{
			controller.speichern(forceSpeichernUnter);
		}catch (IOException e) {
			JOptionPane.showMessageDialog(this, 
				"Es ist ein Fehler beim Speichern der Datei aufgetreten, bitte probieren Sie es noch einmal.", 
				"Fehler beim Speichern", JOptionPane.ERROR_MESSAGE);
		}
	}
		
	protected void tabSchließenClicked() {
		// TODO: über update (Haupfenster) realisieren
        int tabNumber = mainFrame.tabbedPane.getSelectedIndex();
        if (tabNumber != -1) {
        	if ( ! gespeichert){
        		int nutzerauswahlSpeichern = JOptionPane.showConfirmDialog(this, "Wollen Sie die Änderungen am Profil '"+
        				athlet.getName()+"' speichern?", "Achtung!", JOptionPane.YES_NO_CANCEL_OPTION);
        		if (nutzerauswahlSpeichern == 0) {
        			// TODO: speichern besser lösen!!
        			speichernClicked(false);
        		} 
        	}
        	athletenListe.removeAthlet(athlet);
//        	release();
        }
	}
	
	private void setAlleLeistungen(){
		// TODO: ggf. nicht alles löschen & neu eintragen... - über observing Leisung arbeiten?!!?
		DefaultTableModel model = (DefaultTableModel) leistungenTabelle.getModel();
		if (model.getRowCount() > 0) {
		    for (int i = model.getRowCount() - 1; i > -1; i--) {
		    	model.removeRow(i);
		    }
		}
		for (Leistung aktuelleLeistung: athlet.getLeistungen()){
			model.addRow(aktuelleLeistung.getObjectDataForTable());
		}			
	}
	
	protected void deleteZeileButtonPressed() {
		if (JOptionPane.showConfirmDialog(this, "Wollen Sie die Leistung wirklich löschen?", "Leistung löschen", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION){
			int rowToDelete = leistungenTabelle.getSelectedRow();
			Leistung leistungToRemove = getLeistungInZeile(rowToDelete);
			controller.leistungLöschen(leistungToRemove);
		}
	}
	
	protected void leistungBearbeitenPressed(){
		Leistung leistung = getLeistungInZeile(leistungenTabelle.getSelectedRow());
		controller.leistungBearbeitenPressed(leistung);
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

	public void setLeistungBearbeitenAvailable(boolean editable){
		if (editable){
			mainFrame.setLeistungenMenüVerfügbar(true);
			setLeistungButtonsVerfügbar(true);
	        		} else {
			leistungenTabelle.getSelectionModel().clearSelection();
			mainFrame.setLeistungenMenüVerfügbar(false);
			setLeistungButtonsVerfügbar(false);
		}
	}
	
	private void setLeistungButtonsVerfügbar(boolean setTo){
        btnLeistungBearbeiten.setEnabled(setTo);
		btnLeistungLöschen.setEnabled(setTo);		
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

	public void update(Observable arg0, Object speicherStatusChanged) {
		// TODO: Info, wenn Schwelle auf grund eines zu guten/zu schlechten Slope-Faktors nicht gesetzt wird
		if (athlet.getSlopeFaktorStatus() == "set"){
			LeistungHelper l = new LeistungHelper();
			try {
				textFieldSchwelle.setText(l.parseSecInMinutenstring(athlet.getAnaerobeSchwelle()));
			} catch (Exception e) {
			}			
			setAnalysenVerfügbar(true);
		} else {
			textFieldSchwelle.setText("-");
			setAnalysenVerfügbar(false);
		}
		setAlleLeistungen();
		
		if ((boolean)speicherStatusChanged)
			setSpeicherStatus(false);
	}
	
	protected void release(){
		controller.release();
		controller = null;
		athlet.deleteObserver(this);
		athlet = null;
	}
	
	// ---------------------------------- TABLE-METHODS ----------------------------
	
	@Override
	public void tableChanged(TableModelEvent e) {
        int spalte = e.getColumn();
        int zeileView = e.getFirstRow();
        // beim Laden eines neuen Profils: trigerTableChanged nicht aufrufen
        if (spalte<0 || zeileView<0){
        	return;
        }

        int zeileModel = sorter.convertRowIndexToModel(zeileView);
		TableModel model = (TableModel)e.getSource();
		Object data = model.getValueAt(zeileView, spalte);
        triggerTableChanged(zeileModel, spalte, data);
    }
	
	private Leistung getLeistungInZeile(int zeile) {
		zeile = leistungenTabelle.convertRowIndexToModel(zeile);
		String datum = getStringAt(zeile, 0);
		String streckenlaenge = getStringAt(zeile, 1);
		int streckenId = Strecken.getStreckenIdByString(streckenlaenge);
		String bezeichnung = getStringAt(zeile, 2);
		double geschwindigkeit =  getDoubleAt(zeile, 9);
		Leistung vergleichsLeisung = new Leistung(streckenId, athlet.getId(), bezeichnung, datum, geschwindigkeit);
		for (Leistung aktuelleLeistung : athlet.getLeistungen()){
			if (aktuelleLeistung.equals(vergleichsLeisung))
				return aktuelleLeistung;
		}
		return null;
	}
	
	private double getDoubleAt(int zeile, int spalte){
		DefaultTableModel model = (DefaultTableModel) leistungenTabelle.getModel();
		return Double.parseDouble((String) model.getValueAt(zeile, spalte));	
	}

	private String getStringAt (int zeile, int spalte) {
		DefaultTableModel model = (DefaultTableModel) leistungenTabelle.getModel();
		return (String) model.getValueAt(zeile, spalte);		
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
//				mainFrame.tabSchließenClicked();
				tabSchließenClicked();
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
		chckbxLeistungenAuswahl.setSelected(false);
		chckbxLeistungenAuswahl.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { 
            	checkboxLeistungenAutomatischWählenClicked();            	
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
				controller.bestzeitenButtonPressed();				
			}
		});
		panel.add(btnBestzeiten, "cell 0 5,alignx right");
		
		btnTrainingsbereich = new JButton("Trainingsbereiche");
		btnTrainingsbereich.setIcon(new ImageIcon(ProfilTab.class.getResource("/bilder/Berechnen_24x24.png")));
		btnTrainingsbereich.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.trainingsBereichButtonPressed();
			}
		});
		panel.add(btnTrainingsbereich, "cell 1 5");
		
		btnLeistungskurve = new JButton("Leistungskurve als Grafik");
		btnLeistungskurve.setIcon(new ImageIcon(ProfilTab.class.getResource("/bilder/Diagramm_24x24.png")));
		btnLeistungskurve.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				controller.leistungskurveButtonPressed();
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
				controller.neueLeistungButtonPressed();
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
		// die letzten beiden Spalten sind unsichtbar
		// BOOLEAN_SPALTE wird auf 7 gesetzt - müsste angepasst werden, wenn sich solumnNames ändert.
		final String[] columnNames = {"Datum",
				"Streckenlänge",
                "Bezeichnung",
                "Zeit",
                "km/h",
                "m/s",
                "1.000m Zeit",
                "für Analysen ausgewählt",
                "StreckenId",
                "s/km"};
		
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