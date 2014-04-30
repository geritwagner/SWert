package main;

import java.awt.Font;
import java.io.IOException;
import java.util.*;
import javax.swing.*;
import javax.swing.RowSorter.*;
import javax.swing.event.*;
import javax.swing.table.*;
import datei_operationen.*;
import net.miginfocom.swing.MigLayout;
import globale_helper.*;
import model.*;

/**
 * @author Honors-WInfo-Projekt (Fabian B�hm, Alexander Puchta), Gerit Wagner
 */

public class ProfilTab extends JPanel implements TableModelListener, Observer {
	
	private static final long serialVersionUID = 1L;
	private static final int BOOLEAN_SPALTE = 7;
	
	private JLabel lblAthletName;
	protected JButton btnBestzeiten;
	private JButton btnTrainingsbereich;
	private JButton btnLeistungskurve;
	private JScrollPane scrollPane;
	private JTextField textFieldSchwelle;
	private JButton btnLeistungenAuswahl;
	private JTable leistungenTabelle;
	private TableRowSorter<TableModel> sorter;
	private JButton btnLeistungBearbeiten;
	private JButton btnLeistungL�schen;
	private JButton btnTabSchlieen;
	
	private boolean automatischeVerarbeitung = false;
	private Hauptfenster mainFrame = Hauptfenster.aktuellesHauptfenster;
	private Athlet athlet;
	private ProfilTabController controller;
	
	public ProfilTab(Athlet athlet) {
		this.athlet = athlet;
		controller = new ProfilTabController(athlet, this);
		initTab(athlet.getName());
		initLeistungen();
		athlet.addObserver(this);
	}
	
	private void initLeistungen(){
		setAlleLeistungen();
		setAnalysenVerf�gbar(false);
		setLeistungBearbeitenAvailable(false);
	}
	
	protected void tryAutomatischeAuswahl(){
		try {
			controller.automatischAusw�hlen();
		} catch (Exception e) {
		}
	}
	
	protected Athlet getAthlet(){
		return athlet;
	}
	
	private void setAlleLeistungen(){
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
	
	public void update(Observable arg0, Object o) {
		LeistungHelper l = new LeistungHelper();
		try {
			textFieldSchwelle.setText(l.parseSecInMinutenstring(athlet.getAnaerobeSchwelle()));
			setAnalysenVerf�gbar(true);
		} catch (Exception e) {
			textFieldSchwelle.setText("-");
			setAnalysenVerf�gbar(false);
		}			
		setAlleLeistungen();
		mainFrame.setSpeicherStatus();
	}
	
	protected void release(){
		athlet.deleteObserver(this);
		athlet = null;
		controller.release();
		controller = null;
	}
	
// ---------------------------------- Button-Pressed and Status METHODS ----------------------------
	
	private void triggerTableChanged(int zeileView, int spalte, Object data){
		// automatischeVerarbeitung: bricht den Methodenaufruf hier ab.
		if (automatischeVerarbeitung)
        	return;
        if (spalte == BOOLEAN_SPALTE )
        	auswahlF�rSlopeFaktor�ndern(zeileView, spalte, (boolean) data);
	}
	
	private void auswahlF�rSlopeFaktor�ndern (int zeileView, int spalte, boolean setTo) {
    	automatischeVerarbeitung = true;
    	Leistung leistung = getLeistungInZeile(zeileView);
    	DefaultTableModel tableModel = (DefaultTableModel) leistungenTabelle.getModel();
    	try{
    		controller.auswahlF�rSlopeFaktor�ndern(leistung, setTo);
    	} catch (ThreeLeistungenForSlopeFaktorException e){
        	tableModel.setValueAt(false, sorter.convertRowIndexToModel(zeileView), spalte);
			JOptionPane.showMessageDialog(this, "Zum Berechnen der Werte d�rfen nur zwei Leistungen ausgew�hlt werden!"
					, "Zwei Leistungen ausw�hlen",JOptionPane.ERROR_MESSAGE);
    	} catch (GleicheStreckeException e){
        	tableModel.setValueAt(false, sorter.convertRowIndexToModel(zeileView), spalte);
			JOptionPane.showMessageDialog(this, "Zum Berechnen der Werte m�ssen zwei Leistungen �ber unterschiedliche " +
					"Distanzen ausgew�hlt werden!", "Unterschiedliche Strecken w�hlen",JOptionPane.ERROR_MESSAGE);
    	} catch (TooGoodSlopeFaktorException e) {
    		tableModel.setValueAt(false, sorter.convertRowIndexToModel(zeileView), spalte);
    		JOptionPane.showMessageDialog(this, "Auf Basis der gew�hlten Leistungen w�rden unverh�ltnism��ig gute Zeiten berechnet. ",
					"Unzul�ssige Leistungen",JOptionPane.ERROR_MESSAGE);
		} catch (TooBadSlopeFaktorException e) {
    		tableModel.setValueAt(false, sorter.convertRowIndexToModel(zeileView), spalte);
    		JOptionPane.showMessageDialog(this, "Auf Basis der gew�hlten Leistungen w�rden unverh�ltnism��ig schlechte Zeiten berechnet. ",
					"Unzul�ssige Leistungen",JOptionPane.ERROR_MESSAGE);
    	}
    	automatischeVerarbeitung = false;
	}

	protected void LeistungenAutomatischW�hlenClicked() {
		try{
			controller.automatischAusw�hlen();
    	} catch (Exception e) {
    		JOptionPane.showMessageDialog(this, "Es konnten keine zul�ssigen Leistungen gefunden werden.",
					"Unzul�ssige Leistungen",JOptionPane.ERROR_MESSAGE);
		}
	}
		
	protected void speichernClicked(boolean forceSpeichernUnter){
		try{
			controller.speichern(forceSpeichernUnter);
		}catch (IOException e) {
			JOptionPane.showMessageDialog(this, 
				"Es ist ein Fehler beim Speichern der Datei aufgetreten, bitte probieren Sie es noch einmal.", 
				"Fehler beim Speichern", JOptionPane.ERROR_MESSAGE);
		} catch (NoFileChosenException e) {
			e.printStackTrace();
		} catch (SyntaxException e) {
			JOptionPane.showMessageDialog(this, 
				"Es ist ein Syntax-Fehler beim Speichern der Datei aufgetreten, bitte probieren Sie es noch einmal.", 
				"Fehler beim Speichern", JOptionPane.ERROR_MESSAGE);
		}
	}
		
	protected void tabSchlie�enClicked() {
        if ( ! mainFrame.isAktuellerAthletGespeichert()){
        	int nutzerauswahlSpeichern = JOptionPane.showConfirmDialog(this, "Wollen Sie die �nderungen am Profil '"+
        			athlet.getName()+"' speichern?", "Achtung!", JOptionPane.YES_NO_CANCEL_OPTION);
        	if (nutzerauswahlSpeichern == 0) {
        		speichernClicked(false);
        	} 
        }
        Hauptfenster.athletenListe.removeAthlet(athlet);
	}
	
	protected void leistungLoeschenPressed() {
		if (JOptionPane.showConfirmDialog(this, "Wollen Sie die Leistung wirklich l�schen?", "Leistung l�schen", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION){
			int rowToDelete = leistungenTabelle.getSelectedRow();
			Leistung leistungToRemove = getLeistungInZeile(rowToDelete);
			controller.leistungL�schen(leistungToRemove);
		}
	}
	
	protected void leistungBearbeitenPressed(){
		controller.leistungBearbeitenPressed();
	}

	protected void setLeistungBearbeitenAvailable(boolean editable){
		if (editable){
			mainFrame.setLeistungenMen�Verf�gbar(true);
			setLeistungButtonsVerf�gbar(true);
	        		} else {
			leistungenTabelle.getSelectionModel().clearSelection();
			mainFrame.setLeistungenMen�Verf�gbar(false);
			setLeistungButtonsVerf�gbar(false);
		}
	}
	
	private void setLeistungButtonsVerf�gbar(boolean setTo){
        btnLeistungBearbeiten.setEnabled(setTo);
		btnLeistungL�schen.setEnabled(setTo);		
	}
	
	private void setAnalysenVerf�gbar(boolean AnalyseVerf�gbar){
		if(AnalyseVerf�gbar){
			btnBestzeiten.setEnabled(true);
			btnLeistungskurve.setEnabled(true);
			btnTrainingsbereich.setEnabled(true);										
		} else {
			btnBestzeiten.setEnabled(false);
			btnLeistungskurve.setEnabled(false);
			btnTrainingsbereich.setEnabled(false);										
		}
	}
	
// ---------------------------------- TABLE-METHODS ----------------------------
	
	protected int getSelectedRow(){
		return leistungenTabelle.getSelectedRow();
	}
	
	protected boolean isSelectedLeistung(){
		return leistungenTabelle.getSelectedRow() > -1;
	}
	
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
	
	protected Leistung getLeistungInZeile(int zeile) {
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

	private void initTab(String athletenName) {
		initLayout(athletenName);
		initJTable();
	}
	
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
		btnTabSchlieen.addActionListener(controller);
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
		
		JSeparator separator = new JSeparator();
		panel.add(separator, "cell 0 4 5 1,growx");
		
		btnBestzeiten = new JButton("M�gliche Bestzeiten");
		btnBestzeiten.setIcon(new ImageIcon(ProfilTab.class.getResource("/bilder/Pokal_24x24.png")));
		btnBestzeiten.addActionListener(controller);
		panel.add(btnBestzeiten, "cell 0 5,alignx right");
		
		btnTrainingsbereich = new JButton("Trainingsbereiche");
		btnTrainingsbereich.setIcon(new ImageIcon(ProfilTab.class.getResource("/bilder/Berechnen_24x24.png")));
		btnTrainingsbereich.addActionListener(controller);
		panel.add(btnTrainingsbereich, "cell 1 5");
		
		btnLeistungskurve = new JButton("Leistungskurve als Grafik");
		btnLeistungskurve.setIcon(new ImageIcon(ProfilTab.class.getResource("/bilder/Diagramm_24x24.png")));
		btnLeistungskurve.addActionListener(controller);
		panel.add(btnLeistungskurve, "cell 2 5,alignx left");

		scrollPane = new JScrollPane();
		splitPane.setRightComponent(scrollPane);
	}
	
	private void initJTable() {
		leistungenTabelle = initLeistungsTabelle(leistungenTabelle);	
		scrollPane.setViewportView(leistungenTabelle);
		
		JButton btnLeistungHinzuf�gen = new JButton("Leistung hinzuf\u00FCgen");
		btnLeistungHinzuf�gen.setIcon(new ImageIcon(ProfilTab.class.getResource("/bilder/NeueLeistung_24x24.png")));
		add(btnLeistungHinzuf�gen, "flowx,cell 0 2");
		btnLeistungHinzuf�gen.addActionListener(controller);
		
		btnLeistungBearbeiten = new JButton("Leistung bearbeiten");
		btnLeistungBearbeiten.setIcon(new ImageIcon(ProfilTab.class.getResource("/bilder/EditLeistung_24x24.png")));
		add(btnLeistungBearbeiten, "cell 0 2");
		btnLeistungBearbeiten.addActionListener(controller);

		btnLeistungL�schen = new JButton("Leistung l\u00F6schen");
		btnLeistungL�schen.setIcon(new ImageIcon(ProfilTab.class.getResource("/bilder/LeistungLoeschen_24x24.png")));
		add(btnLeistungL�schen, "cell 0 2");
		btnLeistungL�schen.addActionListener(controller);
	}
	
	// hier wird null �bergeben, den parameter k�nnte man folglich l�schen, er stellt jedoch die Aufruf-Reihenfolge der Methoden sicher...
	private JTable initLeistungsTabelle(JTable leistungenTabelle) {
		// die letzten beiden Spalten sind unsichtbar
		// BOOLEAN_SPALTE wird auf 7 gesetzt - m�sste angepasst werden, wenn sich solumnNames �ndert.
		final String[] columnNames = {"Datum",
				"Streckenl�nge",
                "Bezeichnung",
                "Zeit",
                "km/h",
                "m/s",
                "1.000m Zeit",
                "f�r Analysen ausgew�hlt",
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
			
			//nur mit der letzten Spalte (K�stchen) soll Interaktion stattfinden 
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
        leistungenTabelle.getSelectionModel().addListSelectionListener(controller);        
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