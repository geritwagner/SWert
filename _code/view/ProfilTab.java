package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowSorter;
import javax.swing.RowSorter.SortKey;
import javax.swing.SortOrder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import net.miginfocom.swing.MigLayout;
import controller.Einheitenumrechner;
import helper.IntegerComparator;
import controller.LeistungController;
import controller.StreckenController;
import helper.TabelleLeistungsListener;
import main.Main;
import model.Athlet;
import model.Leistung;

import javax.swing.ImageIcon;
import java.awt.Font;

public class ProfilTab extends JPanel implements TableModelListener {
	private static final long serialVersionUID = 1L;
	private Athlet athlet;

	private MainFrame mainFrame = Main.mainFrame;
	private StreckenController streckenController = Main.mainFrame.streckenController;
	private LeistungController leistungController = Main.mainFrame.leistungController;
	
	private JLabel lblAthletName;
	private JButton btnBestzeiten;
	private JButton btnTrainingsbereich;
	private JButton btnLeistungskurve;
	private JScrollPane scrollPane;
	private JTextField textFieldSchwelle;
	private JCheckBox chckbxLeistungenAuswahl;
	private boolean leistungenAuswahlCheck = false;
	
	private JTable leistungenTabelle;
	private boolean verändert = false;
	private int booleanSpalte;
	public TableRowSorter<TableModel> sorter;
	private String speicherPfad;	
	private JButton btnLeistungBearbeiten;
	private JButton btnLeistungLöschen;
	
	private boolean gespeichert = false;
	private boolean automatischeVerarbeitung = false;
	private boolean ausDialog = false;
	private JButton btnTabSchlieen;
	
	//----------------------- für presenter/controller -----------------------

	
	//----------------------- view logik -----------------------

	public ProfilTab(Athlet athlet) {
		// TODO: Athlet nicht speichern - nur darstellen!
		this.athlet = athlet;
		this.speicherPfad = null;
		initLayout(athlet.getName());
		setAnalysenVerfügbar(false);
		initJTable();
		setBearbeitenStatus(false);
	}
	
	public void bestzeitenButtonPressed(){
		new BestzeitenDialog(athlet);
		// TODO: comment?
		// funktionenController.bestzeitenListe(leistungAuswahl[0], slopeFaktor);
	}
	
	public void trainingsBereichButtonPressed(){
		Double schwelle;
		try {
			schwelle = athlet.getAnaerobeSchwelle();
			new TrainingsbereichDialog(schwelle);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Für die Berechnung der Trainingsbereiche müssen zwei Leistungen für die Berechnung" +
					"des Slope-Faktors ausgewählt werden."
					, "Leistungen auswählen",JOptionPane.ERROR_MESSAGE);
		}
	}

	//----------------------- view darstellung -----------------------

	/**
	 * Layout des Athleten-Profils erstellen
	 */
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
		chckbxLeistungenAuswahl.setSelected(true);
		chckbxLeistungenAuswahl.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { 
            	checkboxLeistungenAuswahlprüfen();            	
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
				mainFrame.diagrammController.DiagrammOeffnen();
			}
		});
		panel.add(btnLeistungskurve, "cell 2 5,alignx left");

		scrollPane = new JScrollPane();
		splitPane.setRightComponent(scrollPane);
	}
	
	/**
	 * Initialisieren der für die Tabelle relevanten Elemente
	 */
	private void initJTable() {
		
		leistungenTabelle = initLeistungsTabelle(leistungenTabelle);	
		scrollPane.setViewportView(leistungenTabelle);
		
		JButton btnLeistungHinzufügen = new JButton("Leistung hinzuf\u00FCgen");
		btnLeistungHinzufügen.setIcon(new ImageIcon(ProfilTab.class.getResource("/bilder/NeueLeistung_24x24.png")));
		add(btnLeistungHinzufügen, "flowx,cell 0 2");
		btnLeistungHinzufügen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				leistungenTabelle.getSelectionModel().clearSelection();
				setBearbeitenStatus(false);
				LeistungDialog dialog = new LeistungDialog();		
				dialog.setVisible(true);
			}
		});
		
		btnLeistungBearbeiten = new JButton("Leistung bearbeiten");
		btnLeistungBearbeiten.setIcon(new ImageIcon(ProfilTab.class.getResource("/bilder/EditLeistung_24x24.png")));
		add(btnLeistungBearbeiten, "cell 0 2");
		btnLeistungBearbeiten.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				leistungBearbeiten();
			}
		});
		
		btnLeistungLöschen = new JButton("Leistung l\u00F6schen");
		btnLeistungLöschen.setIcon(new ImageIcon(ProfilTab.class.getResource("/bilder/LeistungLoeschen_24x24.png")));
		add(btnLeistungLöschen, "cell 0 2");
		btnLeistungLöschen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteZeileAction();
			}
		});
	}
		
	/**
	 * eigentliche JTable erzeugen
	 * @param leistungenTabelle
	 * @return leistungenTabelle
	 */
	private JTable initLeistungsTabelle(JTable leistungenTabelle) {
		//Spalten definieren; die letzten beiden Spalten sind unsichtbar
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
		
		
		for (int i = 0; i < columnNames.length; i++) {
			if (columnNames[i].equalsIgnoreCase("für Slope-Faktor ausgewählt")) {
				booleanSpalte = i;				
			}
		}
		//Anfangswerte definieren
		Object[][] data = null;
		
		leistungenTabelle = new JTable(new DefaultTableModel(data,columnNames)) {
			private static final long serialVersionUID = -3228449157468360581L;
						
			@Override
			public Class<?> getColumnClass(int columnIndex) { 
				if (columnIndex == booleanSpalte) { 
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
              	if (spalte == booleanSpalte) {
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
	
	public void setBearbeitenStatus(boolean editable){
		if (editable){
			btnLeistungBearbeiten.setEnabled(true);
			btnLeistungLöschen.setEnabled(true);
	        mainFrame.leistungBearbeitenMenüVerfügbar();
	        mainFrame.leistungLöschenMenüVerfügbar();
		} else {
			mainFrame.leistungBearbeitenMenüAusgrauen();
			mainFrame.leistungLöschenMenüAusgrauen();
			btnLeistungBearbeiten.setEnabled(false);
			btnLeistungLöschen.setEnabled(false);
		}
	}
	
	public void setAnalysenVerfügbar(boolean AnalyseVerfügbar){
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
	
	/**
	 * Schließt das Profil-Tab
	 * @return art
	 */
	public void tabSchließen() {
		setBearbeitenStatus(false);
        int i = mainFrame.tabbedPane.getSelectedIndex();
        int art = -1;
        if (i != -1) {
        	if (gespeichert) {
        		mainFrame.tabbedPane.remove(i); 
        		mainFrame.tabList.remove(i);
        	} else {
        		art = JOptionPane.showConfirmDialog(this, "Wollen Sie die Änderungen am Profil '"+athlet.getName()+"' speichern?", "Achtung!", JOptionPane.YES_NO_CANCEL_OPTION);
        		if (art == 0) {
        			mainFrame.speichern();
        			mainFrame.tabbedPane.remove(i); 
            		mainFrame.tabList.remove(i);
        		} else if (art == 1) {
        			mainFrame.tabbedPane.remove(i); 
            		mainFrame.tabList.remove(i);
        		}
        	}
        }
	}
	
	/**
	 * Visuelle Anzeige im Tab, falls etwas im Profil geändert wurde
	 */
	public void setSpeicherStatus (boolean gespeichert) {
		this.gespeichert = gespeichert;
		int tabStelle = mainFrame.tabbedPane.getSelectedIndex();
		if (gespeichert) {
			mainFrame.tabbedPane.setTitleAt(tabStelle, athlet.getName());
		} else {
			mainFrame.tabbedPane.setTitleAt(tabStelle, "* "+athlet.getName());
		}
	}
	
	/**
	 *  Gibt die Auswahl der Leistungen für die Berechnung des Slope-Faktors gem. der Checkboxen zurück.
	 */	
	public Leistung[] getAusgewählteLeistungenForSlopeFaktorFromCheckbox() {
		int zeilenAnzahl = leistungenTabelle.getRowCount();
		int spalte = booleanSpalte;
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

	
	
	
	
	
	// view bis hier hin überprüft	
	// TODO: ab hier sortieren:	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Methodenaufrufe, falls Änderungen in Tabelle vorgenommen werden
	 */
	@Override
	public void tableChanged(TableModelEvent e) {
        int zeile = e.getFirstRow();
        int spalte = e.getColumn();
        TableModel model = (TableModel)e.getSource();          
       
		if (automatischeVerarbeitung) {
        	return;
        }     
	       
		//Verhindern, dass mehr als 2 Leistungen ausgewählt werden
        if (spalte == booleanSpalte && !automatischeVerarbeitung && !tabelleAuswahlZulässig()) {
//        if (spalte == booleanSpalte && !tabelleAuswahlZulässig()) {
        	automatischeVerarbeitung = true;        	
        	model.setValueAt(false, zeile, spalte);
        	automatischeVerarbeitung = false;
        	return;
        }
        
        aktualisiereSpeicherStatus(spalte);
	    uncheckAutoAuswahl(spalte);
        berechneWerte(); 
    }
	
	/**
	 * Boolean umschalten, falls Checkboxen der Tabelle geändert wurden
	 */
	private void aktualisiereSpeicherStatus(int spalte) {         
		if (spalte != booleanSpalte) {
        	setSpeicherStatus(false);
        }
	}
	
	/**
	 * Sobald 2 Checkboxen ausgewählt sind, wird das Auswählen einer dritten wieder rückgängig gemacht
	 */
	private void uncheckAutoAuswahl(int spalte) {
        if (!verändert && spalte == booleanSpalte && !leistungenAuswahlCheck) {
        	verändert = true;
        	chckbxLeistungenAuswahl.setSelected(false);
        	verändert = false;        	
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
	 * Startet einen Leistungsdialog mit den ausgefüllten Werten der ausgewählten Leistung
	 */
	public void leistungBearbeiten() {
		Leistung leistung = getLeistungInZeile(leistungenTabelle.getSelectedRow());
		if (leistung.getId_strecke() == -1) {
			SchwellenDialog dialog = new SchwellenDialog();
			dialog.setTitle("Schwelle bearbeiten");
			dialog.initWerte(leistung);
			dialog.setVisible(true);
		} else {
			LeistungDialog dialog = new LeistungDialog();
			dialog.setTitle("Leistung bearbeiten");
			dialog.initWerte(leistung);
			dialog.setVisible(true);					
		}
	}
	
	/**
	 * Prüft, ob mehr als 2 Haken in die letzte Spalte gesetzt werden wollen
	 */	
	public boolean tabelleAuswahlZulässig() {
		
		int zeilenAnzahl = leistungenTabelle.getRowCount();
		int zähler = 0;
		
		for (int i = 0; i < zeilenAnzahl; i++) {
			if (true == getBooleanAt(i, booleanSpalte)){
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
	public boolean tabelleAuswahlRichtig() {
		try{
			int zeilenAnzahl = leistungenTabelle.getRowCount();
			int zähler = 0;
			
			for (int i = 0; i < zeilenAnzahl; i++) {
				if (true == getBooleanAt(i, booleanSpalte)){
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
	 * Wählt die Leistungen über die kürzeste und längste Distanz für die Berechnung des Slope-Faktors aus.
	 */	
	public void setLeistungAutomatisch() {		
		int kürzereStrecke = 0;
		int längereStrecke = leistungenTabelle.getRowCount()-1;

		tabelleCheckboxenFürSlopeFaktorAuswahlAufheben();		
		automatischeVerarbeitung = true;
		leistungenTabelle.setValueAt(true, kürzereStrecke, booleanSpalte);
		leistungenTabelle.setValueAt(true, längereStrecke, booleanSpalte);
		automatischeVerarbeitung = false;
		
		Leistung leistungKurzeStrecke = getLeistungInZeile(kürzereStrecke);
		Leistung leistungLangeStrecke = getLeistungInZeile(längereStrecke);

		athlet.resetLeistungAuswahlForSlopeFaktor();
		try {
			athlet.setLeistungToAuswahlForSlopeFaktor(leistungLangeStrecke);		
			athlet.setLeistungToAuswahlForSlopeFaktor(leistungKurzeStrecke);
		} catch (Exception e) {
			// TODO gleiche Leistung ausgewählt oder schon alle Leistungen für Berechnung des Slope-Faktors gesetzt
			e.printStackTrace();
		}
	}
	
	/**
	 * Hebt die Auswahl auf
	 */	
	public void tabelleCheckboxenFürSlopeFaktorAuswahlAufheben() {
		int zeilenAnzahl = leistungenTabelle.getRowCount();
		for (int i = 0; i < zeilenAnzahl; i++) {
			if (true == getBooleanAt(i, booleanSpalte)){
				leistungenTabelle.setValueAt(false, i, booleanSpalte);
			}
		}		
	}
	
	/**
	 * Prüft, ob die Auswahl richtig ist und gibt entsprechend eine Meldung zurück oder berechnet die Werte
	 */
	public void checkboxLeistungenAuswahlprüfen() {		
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
	 * Prüft, ob eine automatische Auswahl zulässig ist
	 */
	private boolean automatischeAuswahlZulässig() {
		int zeilenAnzahl = leistungenTabelle.getRowCount();
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
	
	/**
	 * Methoden um eine neue Zeile in der Tabelle hinzuzufügen oder bestehende zu löschen
	 */
	public void addZeile(Leistung leistung) {
		
		//TODO: Änderung des Models nicht im View!
		athlet.addLeistung(leistung);
		
		DecimalFormat f = new DecimalFormat("#0.00");
		double geschwindigkeit = leistung.getGeschwindigkeit();
		double kmH = Einheitenumrechner.toKmH(geschwindigkeit);
		double mS = Einheitenumrechner.toMS(geschwindigkeit);
		
		int streckenID = leistung.getId_strecke();
		String streckenString;
		if(streckenID == -1) {
			int strecke = leistungController.berechneSchwellenStreckeAusGeschwindigkeit(geschwindigkeit);
			DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.GERMANY);
			streckenString = formatter.format(strecke);
			streckenString = streckenString+"m";
		} else {
			streckenString = streckenController.getStreckenlaengeStringById(streckenID);
		}
		
		Object[] daten = {leistung.getDatum(),
						  streckenString,
						  leistung.getBezeichnung(),
						  leistung.getZeitString(),
						  f.format(kmH),
						  f.format(mS),
						  leistungController.parseSecInMinutenstring(geschwindigkeit),
						  new Boolean(false),
						  new Integer(leistung.getId_strecke()),
						  String.valueOf(leistung.getGeschwindigkeit())};	
		DefaultTableModel model = (DefaultTableModel) leistungenTabelle.getModel();
		model.addRow(daten);	
		leistungsAuswahlBeiZeileHinzufügen();
	}
	
	/**
	 * Prüft, ob automatische Auswahl bei einer neuen Zeile durchgeführt werden soll
	 */
	private void leistungsAuswahlBeiZeileHinzufügen() {
		if (automatischeAuswahlZulässig()) {
			checkboxLeistungenAuswahlprüfen();
		}	
	}
	
	/**
	 * Löscht eine ausgewählte Zeile nach Bestätigung
	 */
	public void deleteZeileAction() {
		if (JOptionPane.showConfirmDialog(this, "Wollen Sie die Leistung wirklich löschen?", "Leistung löschen", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION){
			deleteLeistung();
		}
	}
	
	/**
	 * Löscht eine ausgewählte Zeile, wenn von Schwellen- oder Leistungsdialog gefordert,
	 * ohne eine automatische Auswahl triggern zu können
	 */
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
	 * Schaltet eine automatische Auswahl ab, falls diese nicht möglich ist und
	 * berechnet ansonsten die neuen Werte
	 */
	private void leistungsAuswahlBeiZeileLöschenBeiAutomatischerAuswahlNeuBerechnen() {
		if (!automatischeAuswahlZulässig()) {
			chckbxLeistungenAuswahl.setSelected(false);
		} else {
			checkboxLeistungenAuswahlprüfen();
		}
	}

	
	//----------------------- METHODEN ZUM BERECHNEN -----------------------
	
	/**
	 * Berechnet die nötigen Werte bzw. gibt entsprechende Fehlermeldungen zurück
	 */
	public void werteBerechnen() {
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
			LeistungController l = new LeistungController();
			textFieldSchwelle.setText(l.parseSecInMinutenstring(anaerobeSchwelle));
			
			setAnalysenVerfügbar(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//----------------------- GET/SET METHODEN  -----------------------
	
	/**
	 * Gibt ein Leistungsobjekt aus der jeweiligen Zeile zurück
	 * @param zeile
	 * @return leistung
	 */
	public Leistung getLeistungInZeile(int zeile) {	
		zeile = leistungenTabelle.convertRowIndexToModel(zeile);
		String datum = getStringAt(zeile, 0);
		String streckenlaenge = getStringAt(zeile, 1);
		int streckenId = streckenController.getStreckenIdByString(streckenlaenge);
		String bezeichnung = getStringAt(zeile, 2);
		double geschwindigkeit = getDoubleAt(zeile, 9);
		Leistung leistung = new Leistung(streckenId, athlet.getId(), geschwindigkeit, bezeichnung, datum);
		return leistung;
	}
	
	public int getZeilenAnzahl() {
		return leistungenTabelle.getRowCount();
	}
	
	public String getStringAt (int zeile, int spalte) {
		DefaultTableModel model = (DefaultTableModel) leistungenTabelle.getModel();
		return (String) model.getValueAt(zeile, spalte);		
	}
	
	public boolean getBooleanAt (int zeile, int spalte) {
		DefaultTableModel model = (DefaultTableModel) leistungenTabelle.getModel();
		return (boolean) model.getValueAt(zeile, spalte);		
	}
	
	public int getIntAt (int zeile, int spalte) {
		DefaultTableModel model = (DefaultTableModel) leistungenTabelle.getModel();
		return (int) model.getValueAt(zeile, spalte);		
	}
	
	public double getDoubleAt(int zeile, int spalte){
		DefaultTableModel model = (DefaultTableModel) leistungenTabelle.getModel();
		return Double.parseDouble((String) model.getValueAt(zeile, spalte));				
	}
	
	public int getBooleanSpalte() {
		return booleanSpalte;
	}
	
	public boolean getSpeicherStatus() {
		return gespeichert;
	}
	
	public Athlet getAthlet(){
		return athlet;
	}
		
	public String getSpeicherPfad() {
		return speicherPfad;
	}
	
	public void setSpeicherPfad(String speicherPfad) {
		this.speicherPfad = speicherPfad;
	}	
}
