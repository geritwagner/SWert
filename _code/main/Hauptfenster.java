package main;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import net.miginfocom.swing.MigLayout;
import datei_operationen.*;
import datenbank_kommunikation.DBVerbindung;
import model.*;

import javax.swing.table.DefaultTableModel;

/**
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta), Gerit Wagner
 */

public class Hauptfenster extends JFrame implements Observer {

	private static final long serialVersionUID = 1L;
	private JTabbedPane tabbedPane;
	private LinkedList<ProfilTab> tabList = new LinkedList<ProfilTab>();
	
	private JMenuItem mntmAthletenprofilSchliessen;
	private JMenu mnBearbeiten;
	private JMenuItem mntmLeistungenBearbeiten;
	private JMenuItem mntmLeistungenLoeschen;
	private JMenuItem mntmSpeichern;
	private JMenuItem mntmSpeicherUnter;
	
	public static Hauptfenster aktuellesHauptfenster;	
	private HauptfensterController controller;
	public static AthletenListe athletenListe;
	public DBVerbindung db;
	
	public static final String SWert_Version = "4.0";
	public static final int Db_Version = 1;
	public static final String Autoren = "Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta), Gerit Wagner";
	private JTable athlet_table;
	
	public static void main (String args[]) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					aktuellesHauptfenster = new Hauptfenster();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});		
	}
	
	public Hauptfenster() {
		db = new DBVerbindung();
		athletenListe = new AthletenListe();
		athletenListe.addObserver(this);
		controller = new HauptfensterController(athletenListe, this);
		initHauptfenster();
	}
	
	private void release(){
		athletenListe.deleteObserver(this);
		athletenListe = null;
		controller.release();
		controller = null;
		db.verbindungBeenden();
		setEnabled(false);
		dispose();
	}
	
	protected void fensterSchließen() {
		while (getAnzahlTabs() > 1) {
			getAktivesTab().tabSchließenClicked();
		}
		release();
	}
	
	protected boolean isAktuellerAthletGespeichert(){
		return controller.isAktuellerAthletGespeichert();
	}
	
	protected void setSpeicherStatus () {
		if (isAktuellerAthletGespeichert()){
			tabbedPane.setTitleAt(getIndexSelectedTab(), getAktivesTab().getAthlet().getName());			
		} else {
			tabbedPane.setTitleAt(getIndexSelectedTab(), "* "+getAktivesTab().getAthlet().getName());
		}
	}
	
	protected void dateiOeffnenClicked(){
		try {
			DateiOeffnen dateiOeffnen = new DateiOeffnen();
			dateiOeffnen.getCSVPfadFromUserDialog();
			Athlet gelesenerAthlet = dateiOeffnen.getAthlet();
			if (checkAthletGeöffnet(gelesenerAthlet.getName(), gelesenerAthlet.getId())){
				JOptionPane.showMessageDialog(this,
		    			"Das ausgewählte Athletenprofil ist bereit geöffnet!",
		    			"Athletenprofil bereits geöffnet", JOptionPane.WARNING_MESSAGE);	
			} else {
				athletenListe.addAthlet(gelesenerAthlet);	
			}
		}catch(java.io.FileNotFoundException e) {
			JOptionPane.showMessageDialog(this,
				"Die Datei wurde nicht gefunden, bitte probieren Sie es noch einmal.",
				"Fehler beim Öffnen der Datei", JOptionPane.ERROR_MESSAGE);
		}catch(java.io.IOException e) {
			JOptionPane.showMessageDialog(this,
				"Die Datei konnte nicht gelesen werden, bitte probieren Sie es noch einmal.",
				"Fehler beim Öffnen der Datei", JOptionPane.ERROR_MESSAGE);
		} catch (SyntaxException  e){
			JOptionPane.showMessageDialog(this,
				"Es ist ein Fehler beim Öffnen der Datei aufgetreten (Format/Syntax), bitte überprüfen sie die Datei.",
				"Fehler beim Öffnen der Datei", JOptionPane.ERROR_MESSAGE);			
		}
	}
		
	public void createTab (Athlet athlet) {
		ProfilTab newTab = new ProfilTab(athlet);			
		tabList.add(0, newTab);
		tabbedPane.insertTab(athlet.getName() + " *", null, newTab, null, 0);
		tabbedPane.setSelectedIndex(0);
		newTab.tryAutomatischeAuswahl();
	}
	
	public boolean checkAthletGeöffnet (String name, long id) {		
		for (int i = 0; i < tabList.size(); i++) {			
			ProfilTab tab = tabList.get(i);
			String tabName = tab.getAthlet().getName();
			long tabId = tab.getAthlet().getId();			
			if (tabName.equals(name) && id == tabId) {
				return true;
			}			
		}		
		return false;
	}
	
	protected ProfilTab getAktivesTab() {
		for (int i = 0; i < tabList.size(); i++) {
			if (tabList.get(i).isShowing() == true) {
				return  tabList.get(i);
			}
		}
		return null;		
	}
	
	protected int getAnzahlTabs(){
		return tabbedPane.getTabCount();
	}
	
	protected int getIndexSelectedTab(){
		return tabbedPane.getSelectedIndex();	
	}
	
	protected void athletenMenüVerfügbar(boolean setTo){
		mntmSpeichern.setEnabled(setTo);
		mntmSpeicherUnter.setEnabled(setTo);
		mntmAthletenprofilSchliessen.setEnabled(setTo);
		mnBearbeiten.setEnabled(setTo);
	}
	
	protected void setLeistungenMenüVerfügbar(boolean setTo){
		mntmLeistungenBearbeiten.setEnabled(setTo);
		mntmLeistungenLoeschen.setEnabled(setTo);
	}
	
	public void update(Observable arg0, Object arg1) {
		int anzahlGeoffneteAthleten = tabList.size();
		int anzahlAngelegteAthleten = athletenListe.getAlleAthleten().size();
		
		if (anzahlGeoffneteAthleten < anzahlAngelegteAthleten){
			athletenOeffnen();
		}
		if (anzahlGeoffneteAthleten > anzahlAngelegteAthleten){
			athletenSchliessen();
		}
	}
	
	private void athletenOeffnen(){
		Athlet letzterGeoeffneterAthlet = athletenListe.getLetzterGeoeffneterAthlet();
		createTab(letzterGeoeffneterAthlet);
		setSpeicherStatus();
	}
	
	private void athletenSchliessen(){
		getAktivesTab().release();			
        int i = getIndexSelectedTab();
		tabbedPane.remove(i); 
		tabList.remove(i);
	}
	
// ------------------------------- view datstellung -----------------------------------------------
	
	private void initHauptfenster(){
		initializeFrame();
		initDummyPane();
		athletenMenüVerfügbar(false);
		addWindowListener(controller);
		setVisible(true);		
	}
	
	private void initializeFrame() {		
		setIconImage(Toolkit.getDefaultToolkit().getImage(Hauptfenster.class.getResource("/bilder/Logo_32x32.png")));

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setTitle("S-Wert 3.0");
		setBounds(100, 100, 950, 500);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		Dimension d = getToolkit().getScreenSize();
		setLocation((int) ((d.getWidth() - getWidth()) / 2), (int) ((d.getHeight() - getHeight()) / 2));
		setExtendedState(Frame.MAXIMIZED_BOTH);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnDatei = new JMenu("Datei");
		mnDatei.setMnemonic('D');
		menuBar.add(mnDatei);
		
			JMenuItem mntmNeuesProfilAnlegen = new JMenuItem("Neues Athletenprofil anlegen");
			mntmNeuesProfilAnlegen.setIcon(new ImageIcon(Hauptfenster.class.getResource("/bilder/NeuerAthlet_16x16.png")));
			mntmNeuesProfilAnlegen.addActionListener(controller);
			mntmNeuesProfilAnlegen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
			mnDatei.add(mntmNeuesProfilAnlegen);
			
			JMenuItem mntmProfilffnen = new JMenuItem("Athletenprofil \u00F6ffnen");		
			mntmProfilffnen.setIcon(new ImageIcon(Hauptfenster.class.getResource("/bilder/EditAthlet_16x16.png")));
			mntmProfilffnen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
			mnDatei.add(mntmProfilffnen);
			mntmProfilffnen.addActionListener(controller);
			
			mntmAthletenprofilSchliessen = new JMenuItem("Athletenprofil schlie\u00DFen");
			mntmAthletenprofilSchliessen.addActionListener(controller);
			mntmAthletenprofilSchliessen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
			mnDatei.add(mntmAthletenprofilSchliessen);
			
			JSeparator separator = new JSeparator();
			mnDatei.add(separator);
			
			mntmSpeichern = new JMenuItem("Speichern");
			mntmSpeichern.setIcon(new ImageIcon(Hauptfenster.class.getResource("/bilder/Speichern_16x16.png")));
			mntmSpeichern.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
			mnDatei.add(mntmSpeichern);
			mntmSpeichern.addActionListener(controller);
			
			mntmSpeicherUnter = new JMenuItem("Speichern unter...");
			mntmSpeicherUnter.setIcon(new ImageIcon(Hauptfenster.class.getResource("/bilder/SpeichernUnter_16x16.png")));
			mntmSpeicherUnter.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
			mnDatei.add(mntmSpeicherUnter);
			mntmSpeicherUnter.addActionListener(controller);
			
			JSeparator separator_1 = new JSeparator();
			mnDatei.add(separator_1);
			
			JMenuItem mntmSwertSchlieen = new JMenuItem("S-Wert schlie\u00DFen");
			mntmSwertSchlieen.addActionListener(controller);
			mntmSwertSchlieen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
			mnDatei.add(mntmSwertSchlieen);
			
			mnBearbeiten = new JMenu("Bearbeiten");
			mnBearbeiten.setMnemonic('B');
			mnBearbeiten.setEnabled(false);
			menuBar.add(mnBearbeiten);
		
		JMenu mnLeistungen = new JMenu("Leistung");
		mnBearbeiten.add(mnLeistungen);
		
			JMenuItem mntmLeistungenHinzufgen = new JMenuItem("Leistung hinzuf\u00FCgen");
			mntmLeistungenHinzufgen.setIcon(new ImageIcon(Hauptfenster.class.getResource("/bilder/NeueLeistung_16x16.png")));
			mnLeistungen.add(mntmLeistungenHinzufgen);
			mntmLeistungenHinzufgen.addActionListener(controller);
			
			mntmLeistungenBearbeiten = new JMenuItem("Leistung bearbeiten");
			mntmLeistungenBearbeiten.setIcon(new ImageIcon(Hauptfenster.class.getResource("/bilder/EditLeistung_16x16.png")));
			mnLeistungen.add(mntmLeistungenBearbeiten);
			mntmLeistungenBearbeiten.setEnabled(false);
			mntmLeistungenBearbeiten.addActionListener(controller);
			
			mntmLeistungenLoeschen = new JMenuItem("Leistung l\u00F6schen");
			mntmLeistungenLoeschen.setIcon(new ImageIcon(Hauptfenster.class.getResource("/bilder/LeistungLoeschen_16x16.png")));
			mntmLeistungenLoeschen.setEnabled(false);
			mnLeistungen.add(mntmLeistungenLoeschen);
			mntmLeistungenLoeschen.addActionListener(controller);
		
		JMenu mnHilfe = new JMenu("Hilfe");
			mnHilfe.setMnemonic('H');
			mnHilfe.setEnabled(true);
			menuBar.add(mnHilfe);
			
			JMenuItem mntmAbout = new JMenuItem("About");
			// mntmAbout.setIcon(new ImageIcon(Hauptfenster.class.getResource("/bilder/NeuerAthlet_16x16.png")));
			mntmAbout.addActionListener(controller);
			mnHilfe.add(mntmAbout);
			// Einstellungen, Dokumentation/Tutorial, Links

		getContentPane().setLayout(new GridLayout(1, 0, 0, 0));
		tabbedPane = new JTabbedPane(SwingConstants.TOP);
		tabbedPane.addChangeListener(controller);
		getContentPane().add(tabbedPane);
	}
	
	private void initDummyPane(){
		JPanel dummyTab = new JPanel();
		tabbedPane.addTab("Start", new ImageIcon(Hauptfenster.class.getResource("/bilder/Logo_16x16.png")), dummyTab, null);
		dummyTab.setLayout(new MigLayout("", "[grow]", "[grow][23px]"));
		
		JButton btnNeuesAthletenprofilAnlegen = new JButton("Neues Athletenprofil anlegen");
		btnNeuesAthletenprofilAnlegen.setToolTipText("Anlegen eines neuen Athletenprofils");
		btnNeuesAthletenprofilAnlegen.setIcon(new ImageIcon(Hauptfenster.class.getResource("/bilder/NeuerAthlet_24x24.png")));
		btnNeuesAthletenprofilAnlegen.addActionListener(controller);
		
		athlet_table = new JTable();
		athlet_table.setFillsViewportHeight(true);
		athlet_table.getTableHeader().setReorderingAllowed(false);
		athlet_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		athlet_table.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"ID", "Name", "geoeffnet"
			}
		) {
			Class[] columnTypes = new Class[] {
				Long.class, String.class, Boolean.class
			};
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
			boolean[] columnEditables = new boolean[] {
				false, false, false
			};
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		athlet_table.getColumnModel().getColumn(0).setResizable(false);
		athlet_table.getColumnModel().getColumn(1).setResizable(false);
		athlet_table.getColumnModel().getColumn(2).setResizable(false);
		
		JScrollPane scroll_pane = new JScrollPane();
		//scroll_pane.add(athlet_table);
		scroll_pane.setViewportView(athlet_table);
		dummyTab.add(scroll_pane, "cell 0 0,grow");
		dummyTab.add(btnNeuesAthletenprofilAnlegen, "flowx,cell 0 1");
		setAlleAthleten();
		
		JButton btnAthletenprofilffnen = new JButton("Athletenprofil \u00F6ffnen");
		btnAthletenprofilffnen.setToolTipText("\u00D6ffnen und Bearbeiten eines bestehende Athletenprofils");
		btnAthletenprofilffnen.setIcon(new ImageIcon(Hauptfenster.class.getResource("/bilder/EditAthlet_24x24.png")));
		btnAthletenprofilffnen.addActionListener(controller);
		
		dummyTab.add(btnAthletenprofilffnen, "cell 0 1");
	}
	
	private void setAlleAthleten(){
		DefaultTableModel model = (DefaultTableModel) athlet_table.getModel();
		if (model.getRowCount() > 0) {
		    for (int i = model.getRowCount() - 1; i > -1; i--) {
		    	model.removeRow(i);
		    }
		}
		for (Athlet aktuellerAthlet: athletenListe.getAlleAthleten()){
			model.addRow(aktuellerAthlet.getObjectDataForTable());
		}			
	}
}