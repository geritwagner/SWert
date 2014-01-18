package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedList;
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
import controller.FunktionenController;
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
	private StreckenController streckenController = Main.streckenController;
	private LeistungController leistungController = Main.leistungController;
	private FunktionenController funktionenController = Main.funktionenController;
	
	private JLabel lblAthletName;
	private JButton btnBestzeiten;
	private JButton btnTrainingsbereich;
	private JButton btnLeistungskurve;
	private JScrollPane scrollPane;
	private JTextField textFieldSchwelle;
	private JCheckBox chckbxLeistungenAuswahl;
	private boolean leistungenAuswahlCheck = false;
	private Leistung[] leistungAuswahl = new Leistung[2];
	private double slopeFaktor;
	private double anaerobeSchwelle;
	
	private JTable leistungenTabelle;
	private boolean ver�ndert = false;
	private int booleanSpalte;
	public TableRowSorter<TableModel> sorter;
	private String speicherPfad;	
	private JButton btnLeistungBearbeiten;
	private JButton btnLeistungL�schen;
	
	private boolean gespeichert = false;
	private boolean automatischeVerarbeitung = false;
	private boolean ausDialog = false;
	private JButton btnTabSchlieen;
	
	//----------------------- KONSTRUKTOR -----------------------
	
	public ProfilTab(Athlet athlet) {
		this.athlet = athlet;
		this.speicherPfad = null;
		initLayout();
		initJTable();	
	}
	
	/**
	 * Layout des Athleten-Profils erstellen
	 */
	private void initLayout() {
		mainFrame.leistungBearbeitenAusgrauen();
		mainFrame.leistungL�schenAusgrauen();
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
		
		lblAthletName = new JLabel("<html><u>"+athlet.getName()+"</u></<html>");
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
            	checkboxLeistungenAuswahlpr�fen();            	
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
				new BestzeitenDialog(leistungAuswahl,slopeFaktor);
				funktionenController.bestzeitenListe(leistungAuswahl[0], slopeFaktor);
				
			}
		});
		btnBestzeiten.setEnabled(false);
		panel.add(btnBestzeiten, "cell 0 5,alignx right");
		
		btnTrainingsbereich = new JButton("Trainingsbereiche");
		btnTrainingsbereich.setIcon(new ImageIcon(ProfilTab.class.getResource("/bilder/Berechnen_24x24.png")));
		btnTrainingsbereich.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				new TrainingsbereichDialog(anaerobeSchwelle);
			}
		});
		btnTrainingsbereich.setEnabled(false);
		panel.add(btnTrainingsbereich, "cell 1 5");
		
		btnLeistungskurve = new JButton("Leistungskurve als Grafik");
		btnLeistungskurve.setIcon(new ImageIcon(ProfilTab.class.getResource("/bilder/Diagramm_24x24.png")));
		btnLeistungskurve.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {				
				LinkedList<Leistung> bestzeiten = funktionenController.bestzeitenListe(leistungAuswahl[0], slopeFaktor);
				mainFrame.diagrammController.addAthletLeistungsKurve(athlet);
				mainFrame.diagrammController.addBestzeitenKurve(bestzeiten);
				mainFrame.diagrammController.DiagrammOeffnen();
			}
		});
		btnLeistungskurve.setEnabled(false);
		panel.add(btnLeistungskurve, "cell 2 5,alignx left");

		scrollPane = new JScrollPane();
		splitPane.setRightComponent(scrollPane);
	}
	
	/**
	 * Initialisieren der f�r die Tabelle relevanten Elemente
	 */
	private void initJTable() {
		
		leistungenTabelle = initLeistungsTabelle(leistungenTabelle);	
		scrollPane.setViewportView(leistungenTabelle);
		
		JButton btnLeistungHinzuf�gen = new JButton("Leistung hinzuf\u00FCgen");
		btnLeistungHinzuf�gen.setIcon(new ImageIcon(ProfilTab.class.getResource("/bilder/NeueLeistung_24x24.png")));
		add(btnLeistungHinzuf�gen, "flowx,cell 0 2");
		btnLeistungHinzuf�gen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				leistungenTabelle.getSelectionModel().clearSelection();
				mainFrame.leistungBearbeitenAusgrauen();
				mainFrame.leistungL�schenAusgrauen();
				btnLeistungBearbeiten.setEnabled(false);
				btnLeistungL�schen.setEnabled(false);
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
		btnLeistungBearbeiten.setEnabled(false);
		
		btnLeistungL�schen = new JButton("Leistung l\u00F6schen");
		btnLeistungL�schen.setIcon(new ImageIcon(ProfilTab.class.getResource("/bilder/LeistungLoeschen_24x24.png")));
		add(btnLeistungL�schen, "cell 0 2");
		btnLeistungL�schen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteZeileAction();
			}
		});
		btnLeistungL�schen.setEnabled(false);
	}
	
	
	//----------------------- JTABLE METHODEN -----------------------
	
	/**
	 * eigentliche JTable erzeugen
	 * @param leistungenTabelle
	 * @return leistungenTabelle
	 */
	private JTable initLeistungsTabelle(JTable leistungenTabelle) {
		//Spalten definieren; die letzten beiden Spalten sind unsichtbar
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
		
		
		for (int i = 0; i < columnNames.length; i++) {
			if (columnNames[i].equalsIgnoreCase("f�r Slope-Faktor ausgew�hlt")) {
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
			
			//nur mit der letzten Spalte (K�stchen) soll Interaktion stattfinden 
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
	
	/**
	 * Methodenaufrufe, falls �nderungen in Tabelle vorgenommen werden
	 */
	@Override
	public void tableChanged(TableModelEvent e) {
        int zeile = e.getFirstRow();
        int spalte = e.getColumn();
        TableModel model = (TableModel)e.getSource();          
       
		if (automatischeVerarbeitung) {
        	return;
        }     
	       
		//Verhindern, dass mehr als 2 Leistungen ausgew�hlt werden
        if (spalte == booleanSpalte && !automatischeVerarbeitung && !tabelleAuswahlZul�ssig()) {
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
	 * Boolean umschalten, falls in der Tabelle etwas ge�ndert wurde
	 */
	private void aktualisiereSpeicherStatus(int spalte) {         
		if (spalte != booleanSpalte) {
        	setSpeicherStatus(false);
        }
	}
	
	/**
	 * Sobald 2 Checkboxen ausgew�hlt sind, wird das Ausw�hlen einer dritten wieder r�ckg�ngig gemacht
	 */
	private void uncheckAutoAuswahl(int spalte) {
		
        if (!ver�ndert && spalte == booleanSpalte && !leistungenAuswahlCheck) {
        	ver�ndert = true;
        	chckbxLeistungenAuswahl.setSelected(false);
        	ver�ndert = false;        	
        } 
 	}
	
	/**
	 * Erstellt Referenz-Leistungen mit den beiden manuell gew�hlten Leistungen und berechnet dann die Werte
	 */	
	private void berechneWerte() {
        if (tabelleAuswahlRichtig() && !chckbxLeistungenAuswahl.isSelected()) {  
        	setLeistungArray();
        	werteBerechnen();        	
        }
	}
	
	/**
	 * Startet einen Leistungsdialog mit den ausgef�llten Werten der ausgew�hlten Leistung
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
	 * Schlie�t das Profil-Tab
	 * @return art
	 */
	public int tabSchlie�en() {
		mainFrame.leistungBearbeitenAusgrauen();
    	mainFrame.leistungL�schenAusgrauen();
        int i = mainFrame.tabbedPane.getSelectedIndex();
        int art = -1;
        if (i != -1) {
        	if (gespeichert) {
        		mainFrame.tabbedPane.remove(i); 
        		mainFrame.tabList.remove(i);
        	} else {
        		art = JOptionPane.showConfirmDialog(this, "Wollen Sie die �nderungen am Profil '"+athlet.getName()+"' speichern?", "Achtung!", JOptionPane.YES_NO_CANCEL_OPTION);
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
        return art;
	}
	
	/**
	 * Visuelle Anzeige im Tab, falls etwas im Profil ge�ndert wurde
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
	 * Pr�ft, ob mehr als 2 Haken in die letzte Spalte gesetzt werden wollen
	 */	
	public boolean tabelleAuswahlZul�ssig() {
		
		int zeilenAnzahl = leistungenTabelle.getRowCount();
		int z�hler = 0;
		int spalte = booleanSpalte;		
		
		for (int i = 0; i < zeilenAnzahl; i++) {
			if ((boolean)(leistungenTabelle.getValueAt(i, spalte))==true){
				z�hler++;
			}
			if (z�hler >= 3) {
				return false;
			}			
		}
		return true;	
	}
	
	/**
	 * Pr�ft, ob genau 2 Haken in der letzte Spalte gesetzt sind
	 */	
	public boolean tabelleAuswahlRichtig() {
		try{
			int zeilenAnzahl = leistungenTabelle.getRowCount();
			int z�hler = 0;
			int spalte = booleanSpalte;		
			
			for (int i = 0; i < zeilenAnzahl; i++) {
				if ((boolean)(leistungenTabelle.getValueAt(i, spalte))==true){
					z�hler++;
				}
			}
			if (z�hler == 2) {
				return true;
			} else {			
				return false;	
			}
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Gibt ein Leistungsarray mit den beiden ausgew�hlten Leistungen zur�ck
	 */	
	public void setLeistungArray() {
		
		int zeilenAnzahl = leistungenTabelle.getRowCount();
		int z�hler = 0;
		int spalte = booleanSpalte;		
		
		for (int i = 0; i < zeilenAnzahl; i++) {
			if (z�hler > 1) {
				break;
			}
			if ((boolean)(leistungenTabelle.getValueAt(i, spalte))==true){
				leistungAuswahl[z�hler] = getLeistungInZeile(i);
				z�hler++;
			}				
		}
		
	}
	
	/**
	 * Gibt ein Leistungsarray mit den beiden besten Leistungen zur�ck
	 */	
	public void setLeistungAutomatisch() {		
		int kleinsteL�nge = 0;
		int gr��teL�nge = leistungenTabelle.getRowCount()-1;

		tabelleAuswahlAufheben();		
		automatischeVerarbeitung = true;
		leistungenTabelle.setValueAt(true, kleinsteL�nge, booleanSpalte);
		leistungenTabelle.setValueAt(true, gr��teL�nge, booleanSpalte);
		automatischeVerarbeitung = false;
		

		leistungAuswahl[0] = getLeistungInZeile(kleinsteL�nge);
		leistungAuswahl[1] = getLeistungInZeile(gr��teL�nge);
		
		
	}
	
	/**
	 * Hebt die Auswahl auf
	 */	
	public void tabelleAuswahlAufheben() {
		
		int zeilenAnzahl = leistungenTabelle.getRowCount();		
		int spalte = booleanSpalte;			
		for (int i = 0; i < zeilenAnzahl; i++) {
			if ((boolean)(leistungenTabelle.getValueAt(i, spalte))==true){
				leistungenTabelle.setValueAt(false, i, spalte);
			}
					
		}		
	}
	
	/**
	 * Pr�ft, ob die Auswahl richtig ist und gibt entsprechend eine Meldung zur�ck oder berechnet die Werte
	 */
	public void checkboxLeistungenAuswahlpr�fen() {		
		if (chckbxLeistungenAuswahl.isSelected()) {
        	if (!automatischeAuswahlZul�ssig()) {
				JOptionPane.showMessageDialog(this, "Zum Berechnen der Werte werden zwei Leistungen mit unterschiedlicher Laufstrecke ben�tigt!"
						, "Laufstrecken identisch",JOptionPane.ERROR_MESSAGE);
				chckbxLeistungenAuswahl.setSelected(false);
        	} else {
        		leistungenAuswahlCheck = true;
        		tabelleAuswahlAufheben(); 
        		leistungenAuswahlCheck = false;
        		setLeistungAutomatisch();
        		werteBerechnen();
        	}
    	}
	}
	
	/**
	 * Pr�ft, ob eine automatische Auswahl zul�ssig ist
	 */
	private boolean automatischeAuswahlZul�ssig() {
		int zeilenAnzahl = leistungenTabelle.getRowCount();
		int streckenIdSpalte = 8;
		
		
		if (!chckbxLeistungenAuswahl.isSelected()) {
			return false;
		}
		
		if (zeilenAnzahl>1) {
			
			for (int i = 0; i < zeilenAnzahl-1; i++) {
				String zahl1String = String.valueOf(leistungenTabelle.getValueAt(i, streckenIdSpalte));
				int zahl1 = Integer.parseInt(zahl1String);				
				for (int j = i; j < zeilenAnzahl; j++) {
					String zahl2String = String.valueOf(leistungenTabelle.getValueAt(j, streckenIdSpalte));
					int zahl2 = Integer.parseInt(zahl2String);	
					if (zahl1 != zahl2) {
						return true;
					}
					if (zahl1 == -1 && zahl2 == -1) {
						Leistung zahl1Leistung = getLeistungInZeile(i);
						Leistung zahl2Leistung = getLeistungInZeile(j);
						double zahl1Strecke = funktionenController.getStrecke(zahl1Leistung);
						double zahl2Strecke = funktionenController.getStrecke(zahl2Leistung);
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
	 * Methoden um eine neue Zeile in der Tabelle hinzuzuf�gen oder bestehende zu l�schen
	 */
	public void addZeile(Leistung leistung) {
		athlet.addLeistung(leistung);
		
		DecimalFormat f = new DecimalFormat("#0.00");
		double geschwindigkeit = leistung.getGeschwindigkeit();
		double kmH = Einheitenumrechner.toKmH(geschwindigkeit);
		double mS = Einheitenumrechner.toMS(geschwindigkeit);
		
		int streckenID = leistung.getId_strecke();
		String streckenString;
		if(streckenID == -1) {
			int strecke = leistungController.berechneStreckeAusGeschwindigkeit(geschwindigkeit);
			DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.GERMANY);
			streckenString = formatter.format(strecke);
			streckenString = streckenString+"m";
		} else {
			streckenString = streckenController.getStreckenlaengeStringById(streckenID);
		}
		
		Object[] daten = {leistung.getDatum(),
						  streckenString,
						  leistung.getBezeichnung(),
						  leistung.getZeit(),
						  f.format(kmH),
						  f.format(mS),
						  leistungController.parseSecInMinutenstring(geschwindigkeit),
						  new Boolean(false),
						  new Integer(leistung.getId_strecke()),
						  String.valueOf(leistung.getGeschwindigkeit())};	
		DefaultTableModel model = (DefaultTableModel) leistungenTabelle.getModel();
		model.addRow(daten);	
		leistungsAuswahlBeiZeileHinzuf�gen();

	}	
	/**
	 * Pr�ft, ob automatische Auswahl bei einer neuen Zeile durchgef�hrt werden soll
	 */
	private void leistungsAuswahlBeiZeileHinzuf�gen() {
		if (automatischeAuswahlZul�ssig()) {
			checkboxLeistungenAuswahlpr�fen();
		}	
	}
	
	/**
	 * L�scht eine ausgew�hlte Zeile nach Best�tigung
	 */
	public void deleteZeileAction() {
		if (JOptionPane.showConfirmDialog(this, "Wollen Sie die Leistung wirklich l�schen?", "Leistung l�schen", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION){
			deleteZeile();
		}
	}
	
	/**
	 * L�scht eine ausgew�hlte Zeile, wenn von Schwellen- oder Leistungsdialog gefordert,
	 * ohne eine automatische Auswahl triggern zu k�nnen
	 */
	public void deleteZeileAusDialog() {
		ausDialog = true;
		deleteZeile();
		ausDialog = false;
	}
	
	/**
	 * L�scht eine ausgew�hlte Zeile
	 */
	public void deleteZeile() {
		DefaultTableModel model = (DefaultTableModel) leistungenTabelle.getModel();
		try {		
		model.removeRow(leistungenTabelle.convertRowIndexToModel(leistungenTabelle.getSelectedRow()));	
		if (!ausDialog) {
			leistungsAuswahlBeiZeileL�schen();
		}
		mainFrame.leistungBearbeitenAusgrauen();
		mainFrame.leistungL�schenAusgrauen();
		btnLeistungBearbeiten.setEnabled(false);
		btnLeistungL�schen.setEnabled(false);
		athlet.resetLeistungen();
		if(getZeilenAnzahl() != 0) {
			for (int i = 0; i < leistungenTabelle.getRowCount(); i++) {
				Leistung leistung = getLeistungInZeile(i);
				athlet.addLeistung(leistung);
			}
		}
		}catch (Exception e) {
			//TODO Log-File schreiben			
		}
	}
	
	/**
	 * Schaltet eine automatische Auswahl ab, falls diese nicht m�glich ist und
	 * berechnet ansonsten die neuen Werte
	 */
	private void leistungsAuswahlBeiZeileL�schen() {
		if (!automatischeAuswahlZul�ssig()) {
			chckbxLeistungenAuswahl.setSelected(false);
		} else {
			checkboxLeistungenAuswahlpr�fen();
		}
	}

	
	//----------------------- METHODEN ZUM BERECHNEN -----------------------
	
	/**
	 * Berechnet die n�tigen Werte bzw. gibt entsprechende Fehlermeldungen zur�ck
	 */
	public void werteBerechnen() {								
				slopeFaktor = funktionenController.slopeFaktorBerechnen(leistungAuswahl[0], leistungAuswahl[1]);
				DecimalFormat f = new DecimalFormat("#0.00");				
				String slopeFaktorString = f.format(slopeFaktor);
				if (slopeFaktor==-1) {
					JOptionPane.showMessageDialog(this, "Zum Berechnen der Werte werden zwei Leistungen mit unterschiedlicher Laufstrecke ben�tigt!"
							, "Laufstrecken identisch",JOptionPane.ERROR_MESSAGE);
					tabelleAuswahlAufheben();
					chckbxLeistungenAuswahl.setSelected(false);
				} else {
					anaerobeSchwelle = funktionenController.anaerobeSchwelleBerechnen(leistungAuswahl[0], slopeFaktor);
					if (anaerobeSchwelle == -1) {
						JOptionPane.showMessageDialog(this, "Beim Sch�tzen der Aerob/Anaeroben Schwelle konnte der Wert nicht genau genug festgelegt werden. Der tat�chliche Wert kann von dem angezeigten Ergebnis abweichen!"
								, "Wert nicht genau genug",JOptionPane.INFORMATION_MESSAGE);
						
					} else {
						LeistungController l = new LeistungController();
						textFieldSchwelle.setText(l.parseSecInMinutenstring(anaerobeSchwelle));
						btnBestzeiten.setEnabled(true);
						btnLeistungskurve.setEnabled(true);
						btnTrainingsbereich.setEnabled(true);						
					}
				}		
	}
	//----------------------- GET/SET METHODEN  -----------------------
	
	/**
	 * Gibt ein Leistungsobjekt aus der jeweiligen Zeile zur�ck
	 * @param zeile
	 * @return leistung
	 */
	public Leistung getLeistungInZeile(int zeile) {	
		zeile = leistungenTabelle.convertRowIndexToModel(zeile);
		DefaultTableModel model = (DefaultTableModel) leistungenTabelle.getModel();

		String datum = (String) model.getValueAt(zeile, 0);
		String streckenlaenge = (String) model.getValueAt(zeile, 1);
		int streckenId = streckenController.getStreckenIdByString(streckenlaenge);
		String bezeichnung = (String) model.getValueAt(zeile, 2);
		double geschwindigkeit = Double.parseDouble((String)model.getValueAt(zeile, 9));
		Leistung leistung = leistungController.neueLeistung(streckenId, athlet.getId(), geschwindigkeit, bezeichnung, datum);
		return leistung;
	}
	
	public int getZeilenAnzahl() {
		return leistungenTabelle.getRowCount();
	}
	
	public int getSpaltenAnzahl() {
		return leistungenTabelle.getColumnCount();
	}

	public String getValueAt (int zeile, int spalte) {
		DefaultTableModel model = (DefaultTableModel) leistungenTabelle.getModel();
		return (String) model.getValueAt(zeile, spalte);		
	}
	
	public int getBooleanSpalte() {
		return booleanSpalte;
	}
	
	public boolean getSpeicherStatus() {
		return gespeichert;
	}
	
	public long getAthletenId() {
		return athlet.getId();
	}
	
	public void setAthletenId(long id) {
		athlet.setId(id);		
	}
	
	public String getAthletenName() {
		return athlet.getName();
	}
	
	public String getSpeicherPfad() {
		return speicherPfad;
	}
	
	public void setSpeicherPfad(String speicherPfad) {
		this.speicherPfad = speicherPfad;
	}	
	
	public double getSlopeFaktor() {
		return slopeFaktor;
	}
	
	public double getAnaerobeSchwelle() {
		return anaerobeSchwelle;
	}
	
	public Leistung[] getLeistungAuswahl() {
		return leistungAuswahl;
	}
	
	public void leistungsButtonsVerf�gbar() {
		btnLeistungBearbeiten.setEnabled(true);
		btnLeistungL�schen.setEnabled(true);
	}
}
