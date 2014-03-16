package main;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.awt.EventQueue;
import javax.swing.*;
import net.miginfocom.swing.MigLayout;
import datei_operationen.*;
import model.*;

public class Hauptfenster extends JFrame implements Observer {

	private static final long serialVersionUID = 1L;
	public JTabbedPane tabbedPane;
	public LinkedList<ProfilTab> tabList = new LinkedList<ProfilTab>();
	
	private JMenuItem mntmAthletenprofilSchliessen;
	private JMenu mnBearbeiten;
	private JMenuItem mntmLeistungenBearbeiten;
	private JMenuItem mntmLeistungenLoeschen;
	private JMenuItem mntmSpeichern;
	private JMenuItem mntmSpeicherUnter;
	
	protected int selectedIndex;
	public static Hauptfenster aktuellesHauptfenster;	
	public HauptfensterController controller;
	public static AthletenListe athletenListe;
	
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
		athletenListe = new AthletenListe();
		athletenListe.addObserver(this);
		controller = new HauptfensterController(athletenListe, this);
		initializeFrame();
		initDummyPane();
		athletenMenüVerfügbar(false);
		addWindowListener(controller);
		setVisible(true);
	}
	
	private void initializeFrame() {		
		setIconImage(Toolkit.getDefaultToolkit().getImage(Hauptfenster.class.getResource("/bilder/Logo_32x32.png")));
		addWindowListener(controller);

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setTitle("S-Wert 3.0");
		setBounds(100, 100, 950, 500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension d = getToolkit().getScreenSize();
		setLocation((int) ((d.getWidth() - getWidth()) / 2), (int) ((d.getHeight() - getHeight()) / 2));
		setExtendedState(Frame.MAXIMIZED_BOTH);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnDatei = new JMenu("Datei");
		menuBar.add(mnDatei);
		
		JMenuItem mntmNeuesProfilAnlegen = new JMenuItem("Neues Athletenprofil anlegen");
		mntmNeuesProfilAnlegen.setIcon(new ImageIcon(Hauptfenster.class.getResource("/bilder/NeuerAthlet_16x16.png")));
		mntmNeuesProfilAnlegen.addActionListener(controller);
		mnDatei.add(mntmNeuesProfilAnlegen);
		
		JMenuItem mntmProfilffnen = new JMenuItem("Athletenprofil \u00F6ffnen");		
		mntmProfilffnen.setIcon(new ImageIcon(Hauptfenster.class.getResource("/bilder/EditAthlet_16x16.png")));
		mnDatei.add(mntmProfilffnen);
		mntmProfilffnen.addActionListener(controller);
		
		mntmAthletenprofilSchliessen = new JMenuItem("Athletenprofil schlie\u00DFen");
		mntmAthletenprofilSchliessen.addActionListener(controller);
		mntmAthletenprofilSchliessen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.ALT_MASK));
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
		mnDatei.add(mntmSpeicherUnter);
		mntmSpeicherUnter.addActionListener(controller);
		
		JSeparator separator_1 = new JSeparator();
		mnDatei.add(separator_1);
		
		JMenuItem mntmSwertSchlieen = new JMenuItem("S-Wert schlie\u00DFen");
		mntmSwertSchlieen.addActionListener(controller);
		mnDatei.add(mntmSwertSchlieen);
		
		mnBearbeiten = new JMenu("Bearbeiten");
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

		getContentPane().setLayout(new GridLayout(1, 0, 0, 0));
		
		tabbedPane = new JTabbedPane(SwingConstants.TOP);
		tabbedPane.addChangeListener(controller);

		getContentPane().add(tabbedPane);
	}
	
	protected void initDummyPane(){
		JPanel dummyTab = new JPanel();
		tabbedPane.addTab("Start", new ImageIcon(Hauptfenster.class.getResource("/bilder/Logo_16x16.png")), dummyTab, null);
		dummyTab.setLayout(new MigLayout("", "[grow][205px][grow]", "[5:50:200][14px][2px][23px][20px][][][][2px][23px]"));

		JLabel lblLegenSieHier = new JLabel("Legen Sie hier ein neues Athletenprofil an:", SwingConstants.CENTER);
		lblLegenSieHier.setFont(new Font("Tahoma", Font.BOLD, 11));
		dummyTab.add(lblLegenSieHier, "cell 1 1,growx,aligny top");
		
		JSeparator separator_2 = new JSeparator();
		dummyTab.add(separator_2, "cell 0 2 3 1,growx,aligny top");
		
		JButton btnNeuesAthletenprofilAnlegen = new JButton("Neues Athletenprofil anlegen");
		btnNeuesAthletenprofilAnlegen.setToolTipText("Anlegen eines neuen Athletenprofils");
		btnNeuesAthletenprofilAnlegen.setIcon(new ImageIcon(Hauptfenster.class.getResource("/bilder/NeuerAthlet_24x24.png")));
		btnNeuesAthletenprofilAnlegen.addActionListener(controller);
				
		dummyTab.add(btnNeuesAthletenprofilAnlegen, "cell 1 3,growx,aligny top");
		
		JLabel lblffnenSieEin = new JLabel("\u00D6ffnen Sie ein bestehendes Athletenprofil:");
		lblffnenSieEin.setFont(new Font("Tahoma", Font.BOLD, 11));
		dummyTab.add(lblffnenSieEin, "cell 1 5,alignx left,aligny top");
		
		JSeparator separator_3 = new JSeparator();
		dummyTab.add(separator_3, "cell 0 6 3 1,growx,aligny top");
		
		JButton btnAthletenprofilffnen = new JButton("Athletenprofil \u00F6ffnen");
		btnAthletenprofilffnen.setToolTipText("\u00D6ffnen und Bearbeiten eines bestehende Athletenprofils");
		btnAthletenprofilffnen.setIcon(new ImageIcon(Hauptfenster.class.getResource("/bilder/EditAthlet_24x24.png")));
		btnAthletenprofilffnen.addActionListener(controller);
		
		dummyTab.add(btnAthletenprofilffnen, "cell 1 7,growx,aligny top");
	}
	
	protected void release() {
		Iterator<ProfilTab> iterator = tabList.iterator();
		boolean gespeichert = true;
		while (iterator.hasNext()) {
			ProfilTab tab = iterator.next();
			if (!tab.getSpeicherStatus()) {
				gespeichert = false;
			}
		}
		if(!gespeichert) {
			// TODO: ggf. YES_NO_CANCEL_OPTION
			int art = JOptionPane.showConfirmDialog(getContentPane(),
				"S-Wert wird geschlossen.\nWollen Sie Ihre Änderungen speichern?", 
				"Achtung!", JOptionPane.YES_NO_OPTION);
    		if (art == 1) {
    			setEnabled(false);
    			dispose();
    		} else if (art== 0) {
    			iterator = tabList.iterator();
    			while(iterator.hasNext()) {
    				ProfilTab tab = iterator.next();
    				if (tab.getSpeicherStatus()) {
    					iterator.remove();
    					tabbedPane.setSelectedComponent(tab);
    					tabbedPane.remove(tabbedPane.getSelectedIndex());
    				} else {
    					tab.speichernClicked(false);
    				}
    				iterator = tabList.iterator();
    			}
    			System.exit(0);
    		}
		} else {
			System.exit(0);
		}
	}
	
	protected void dateiOeffnenClicked(){
		try {
			new DateiOeffnen(athletenListe);
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
		} catch (AlreadyOpenException  e){
		JOptionPane.showMessageDialog(this,
				"Die Datei ist schon geöffnet.",
				"Datei schon geöffnet", JOptionPane.ERROR_MESSAGE);			
		}
	}
		
	public ProfilTab createTab (Athlet athlet) {
		ProfilTab newTab = new ProfilTab(athletenListe, athlet);			
		tabList.add(0, newTab);
		tabbedPane.insertTab(athlet.getName() + " *", null, newTab, null, 0);
		tabbedPane.setSelectedIndex(0);
		return newTab;
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
	
	public ProfilTab getAktivesTab() {		
		for (int i = 0; i < tabList.size(); i++) {
			if (tabList.get(i).isShowing() == true) {
				return  tabList.get(i);
			}
		}
		// TODO: null ok??
		return null;		
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
			Athlet letzterGeoeffneterAthlet = athletenListe.getLetzterGeoeffneterAthlet();
			createTab(letzterGeoeffneterAthlet);
			if (letzterGeoeffneterAthlet.isSetSpeicherpfad()){
				getAktivesTab().setSpeicherStatus(true);				
			} else {
				getAktivesTab().setSpeicherStatus(false);								
			}
		}
		if (anzahlGeoffneteAthleten > anzahlAngelegteAthleten){
			getAktivesTab().release();			
	        int i = tabbedPane.getSelectedIndex();
			tabbedPane.remove(i); 
			tabList.remove(i);
		}

	}
}