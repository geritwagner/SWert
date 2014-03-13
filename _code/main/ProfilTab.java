package main;

import java.awt.event.*;
import java.awt.Font;
import java.util.*;
import javax.swing.*;
import javax.swing.RowSorter.*;
import javax.swing.event.*;
import javax.swing.table.*;
import net.miginfocom.swing.MigLayout;

import analyse_bestzeiten.BestzeitenDialog;
import analyse_diagramm.DiagrammController;
import analyse_trainingsbereich.TrainingsbereichDialog;
import leistung_bearbeiten.LeistungDialog;
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
	private String speicherPfad;
	private JButton btnLeistungBearbeiten;
	private JButton btnLeistungL�schen;
	private JButton btnTabSchlieen;
	
	private boolean gespeichert = false;
	private boolean automatischeVerarbeitung = false;
	private boolean ausDialog = false;
	private MainFrame mainFrame = Main.mainFrame;
	private Athlet athlet;
	
	public ProfilTab(Athlet athlet) {
		this.athlet = athlet;
		this.speicherPfad = null;
		initLayout(athlet.getName());
		setAnalysenVerf�gbar(false);
		initJTable();
		setAlleLeistungen();
		athlet.addObserver(this);
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
		// TODO: call DiagrammFrame!!!
		new DiagrammController();
	}
	
	public void trainingsBereichButtonPressed(){
		new TrainingsbereichDialog(athlet);
	}
	
	// ----------------- Actions ------------------------------
	
	public void triggerTableChanged(int zeileView, int spalte, Object data){
		// automatischeVerarbeitung: bricht den Methodenaufruf hier ab.
		if (automatischeVerarbeitung)
        	return;

        if (spalte == BOOLEAN_SPALTE )
        	auswahlF�rSlopeFaktor�ndern(zeileView, spalte, (boolean) data);
	}
	
	private void auswahlF�rSlopeFaktor�ndern (int zeileView, int spalte, boolean setTo) {
    	automatischeVerarbeitung = true;
    	Leistung leistung = getLeistungInZeile(zeileView);
    	try{
	    	if (setTo == true){
	    		athlet.setLeistungToAuswahlForSlopeFaktor(leistung);
	    	} else {
	    		athlet.removeLeistungFromAuswahlForSlopeFaktor(leistung);
	    	}
	    	// TODO: eleganter?
	    	chckbxLeistungenAuswahl.setSelected(false);
    	} catch (ThreeLeistungenForSlopeFaktorException e){
        	DefaultTableModel tableModel = (DefaultTableModel) leistungenTabelle.getModel();
        	tableModel.setValueAt(false, sorter.convertRowIndexToModel(zeileView), spalte);
			JOptionPane.showMessageDialog(this, "Zum Berechnen der Werte d�rfen nur zwei Leistungen ausgew�hlt werden!"
					, "Zwei Leistungen ausw�hlen",JOptionPane.ERROR_MESSAGE);
    	} catch (GleicheStreckeException e){
        	DefaultTableModel tableModel = (DefaultTableModel) leistungenTabelle.getModel();
        	tableModel.setValueAt(false, sorter.convertRowIndexToModel(zeileView), spalte);
			JOptionPane.showMessageDialog(this, "Zum Berechnen der Werte m�ssen zwei Leistungen �ber unterschiedliche Distanzen ausgew�hlt werden!"
					, "Unterschiedliche Strecken w�hlen",JOptionPane.ERROR_MESSAGE);
    	}
    	automatischeVerarbeitung = false;
	}
	
	public void checkboxLeistungenAutomatischW�hlenClicked() {		
		try{
			if (chckbxLeistungenAuswahl.isSelected()){
				athlet.setLeistungenAuswahlForSlopeFaktorAutomatisch();				
			} else {
			}
    	} catch (ThreeLeistungenForSlopeFaktorException e){
			JOptionPane.showMessageDialog(this, "Zum Berechnen der Werte d�rfen nur zwei Leistungen ausgew�hlt werden!"
					, "Zwei Leistungen ausw�hlen",JOptionPane.ERROR_MESSAGE);
    	} catch (GleicheStreckeException e){
			JOptionPane.showMessageDialog(this, "Zum Berechnen der Werte m�ssen zwei Leistungen �ber unterschiedliche Distanzen ausgew�hlt werden!"
					, "Unterschiedliche Strecken w�hlen",JOptionPane.ERROR_MESSAGE);
    	}
	}

	public void leistungBearbeitenPressed() {
		Leistung leistung = getLeistungInZeile(leistungenTabelle.getSelectedRow());
		new LeistungDialog(athlet, leistung);
	}

	public void deleteZeileButtonPressed() {
		if (JOptionPane.showConfirmDialog(this, "Wollen Sie die Leistung wirklich l�schen?", "Leistung l�schen", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION){
			deleteLeistung();
		}
	}
	
	public void neueLeistungButtonPressed(){
		setBearbeitenStatus(false);
		new LeistungDialog(athlet, null);		
	}

	public void tabSchlie�en() {
		setBearbeitenStatus(false);
        int i = mainFrame.tabbedPane.getSelectedIndex();
        // TODO: welcher Fall ist i=-1 bzw !=-1 ??? ggf. schon gespeichert???
        if (i != -1) {
        	if (gespeichert) {
        		mainFrame.tabbedPane.remove(i); 
        		mainFrame.tabList.remove(i);
        	} else {
        		int nutzerauswahlSpeichern = JOptionPane.showConfirmDialog(this, "Wollen Sie die �nderungen am Profil '"+athlet.getName()+"' speichern?", "Achtung!", JOptionPane.YES_NO_CANCEL_OPTION);
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
			btnLeistungL�schen.setEnabled(true);
	        mainFrame.leistungBearbeitenMen�Verf�gbar();
	        mainFrame.leistungL�schenMen�Verf�gbar();
		} else {
			leistungenTabelle.getSelectionModel().clearSelection();
			mainFrame.leistungBearbeitenMen�Ausgrauen();
			mainFrame.leistungL�schenMen�Ausgrauen();
			btnLeistungBearbeiten.setEnabled(false);
			btnLeistungL�schen.setEnabled(false);
		}
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
	
	// ----------------------------- Getter and Setter -----------------------
	
	public void setAlleLeistungen(){
		// TODO: ggf. nicht alles l�schen & neu eintragen... - �ber observing Leisung arbeiten?!!?
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
	
	/**
	 * L�scht eine ausgew�hlte Zeile, wenn von Schwellen- oder Leistungsdialog gefordert,
	 * ohne eine automatische Auswahl triggern zu k�nnen
	 */
	// is called from SchwellenDialog
	// TODO: �ber observer �ndern, nicht l�schen & neu einf�gen der Zeile!!
	public void deleteZeileAusDialog() {
		ausDialog = true;
		deleteLeistung();
		ausDialog = false;
	}
	
	public void deleteLeistung() {
		DefaultTableModel model = (DefaultTableModel) leistungenTabelle.getModel();
		int rowToDelete = leistungenTabelle.getSelectedRow();
		
		// deckt den Fall ab, wenn neue Leistungen hinzugef�gt werden (es wird immer die markierte gel�scht und durch die neue
		// ersetzt - bei neuen Leistungen ist aber standardm��ig keine bzw. Zeile -1 markiert, die aber nicht gel�scht werden kann)
		// TODO: eleganter umsetzen??
		if (-1 == rowToDelete) {
			return;
		}
		Leistung leistungToRemove = getLeistungInZeile(rowToDelete);
		model.removeRow(leistungenTabelle.convertRowIndexToModel(rowToDelete));	
		
		// TODO: umstrukturieren!!
		// ausDialog: wird dann auf true gesetzt, wenn deleteLeistungDialog() vom LeistungDialog aufgerufen wird
		if (!ausDialog) {
			athlet.removeLeistung(leistungToRemove);
		}
		setBearbeitenStatus(false);		
	}
	
	
	// ---------------------------------- TABLE-METHODS: GET/SET ----------------------------
	
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
				int i = mainFrame.tabbedPane.getSelectedIndex();
				ProfilTab tab = (ProfilTab) mainFrame.tabbedPane.getComponentAt(i);
				tab.tabSchlie�en();
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
		// TODO: entsprechend der Logik ggf. wieder standardm��ig auf true setzen...
		chckbxLeistungenAuswahl.setSelected(false);
		chckbxLeistungenAuswahl.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { 
            	checkboxLeistungenAutomatischW�hlenClicked();            	
            }
        });
		panel.add(chckbxLeistungenAuswahl, "cell 2 2");
		
		JSeparator separator = new JSeparator();
		panel.add(separator, "cell 0 4 5 1,growx");
		
		btnBestzeiten = new JButton("M�gliche Bestzeiten");
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
		
		JButton btnLeistungHinzuf�gen = new JButton("Leistung hinzuf\u00FCgen");
		btnLeistungHinzuf�gen.setIcon(new ImageIcon(ProfilTab.class.getResource("/bilder/NeueLeistung_24x24.png")));
		add(btnLeistungHinzuf�gen, "flowx,cell 0 2");
		btnLeistungHinzuf�gen.addActionListener(new ActionListener() {
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
		
		btnLeistungL�schen = new JButton("Leistung l\u00F6schen");
		btnLeistungL�schen.setIcon(new ImageIcon(ProfilTab.class.getResource("/bilder/LeistungLoeschen_24x24.png")));
		add(btnLeistungL�schen, "cell 0 2");
		btnLeistungL�schen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteZeileButtonPressed();
			}
		});
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
                "f�r Slope-Faktor ausgew�hlt",
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

	public void update(Observable arg0, Object arg1) {
		if (athlet.getSlopeFaktorStatus() == "set"){
			LeistungHelper l = new LeistungHelper();
			try {
				textFieldSchwelle.setText(l.parseSecInMinutenstring(athlet.getAnaerobeSchwelle()));
			} catch (Exception e) {
			}			
			setAnalysenVerf�gbar(true);
		} else {
			textFieldSchwelle.setText("-");
			setAnalysenVerf�gbar(false);
		}
		setAlleLeistungen();
		}
}