package view;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import main.Main;
import model.Athlet;
import net.miginfocom.swing.MigLayout;
import controller.AthletController;
import controller.CSVController;
import controller.DiagrammController;


public class MainFrame {

	private JFrame mainFrame = new JFrame();
	public JTabbedPane tabbedPane;
	public LinkedList<ProfilTab> tabList = new LinkedList<ProfilTab>();
	public DiagrammController diagrammController = Main.diagrammController;
	private CSVController csvController = Main.csvController;
	
	private Dimension d = mainFrame.getToolkit().getScreenSize();
	private AthletController athletcontroller = Main.athletController;
	private JMenuItem mntmAthletenprofilSchlieen;
	private JMenu mnBearbeiten;
	private JMenuItem mntmLeistungenBearbeiten;
	private JMenuItem mntmLeistungenLoeschen;
	
	private int selectedIndex;

	/**
	 * Create the application.
	 */
	public MainFrame() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {		
		mainFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(MainFrame.class.getResource("/bilder/Logo_32x32.png")));
		mainFrame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent arg0) {
				programmSchliessen();
			}
		});
		mainFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		mainFrame.setTitle("S-Wert 3.0");
		mainFrame.setBounds(100, 100, 950, 500);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setLocation((int) ((d.getWidth() - mainFrame.getWidth()) / 2), (int) ((d.getHeight() - mainFrame.getHeight()) / 2));
		mainFrame.setExtendedState(Frame.MAXIMIZED_BOTH);
		
		JMenuBar menuBar = new JMenuBar();
		mainFrame.setJMenuBar(menuBar);
		
		JMenu mnDatei = new JMenu("Datei");
		menuBar.add(mnDatei);
		
		JMenuItem mntmNeuesProfilAnlegen = new JMenuItem("Neues Athletenprofil anlegen");
		mntmNeuesProfilAnlegen.setIcon(new ImageIcon(MainFrame.class.getResource("/bilder/NeuerAthlet_16x16.png")));
		mntmNeuesProfilAnlegen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				AthletDialog dialog = new AthletDialog();
				dialog.setVisible(true);
			}
		});
		mnDatei.add(mntmNeuesProfilAnlegen);
		
		JMenuItem mntmProfilffnen = new JMenuItem("Athletenprofil \u00F6ffnen");		
		mntmProfilffnen.setIcon(new ImageIcon(MainFrame.class.getResource("/bilder/EditAthlet_16x16.png")));
		mnDatei.add(mntmProfilffnen);
		mntmProfilffnen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
					try {						
						DateiPfadÖffnen dö = new DateiPfadÖffnen();
						String pfad = dö.open();						
						if (pfad != null) {							
							if (!csvController.lesen(pfad)){
								fehlermeldungBeiLesen();
							}
						}	
					}catch(Exception e) {
						
					}
			}
		});
		
		mntmAthletenprofilSchlieen = new JMenuItem("Athletenprofil schlie\u00DFen");
		mntmAthletenprofilSchlieen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (selectedIndex != -1	) {
					ProfilTab tab = (ProfilTab) tabbedPane.getComponentAt(selectedIndex);
					tab.tabSchließen();					
				} else {
					return;
				}
			}
		});
		mntmAthletenprofilSchlieen.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.ALT_MASK));
		mnDatei.add(mntmAthletenprofilSchlieen);
		
		JSeparator separator = new JSeparator();
		mnDatei.add(separator);
		
		JMenuItem mntmSpeichern = new JMenuItem("Speichern");
		mntmSpeichern.setIcon(new ImageIcon(MainFrame.class.getResource("/bilder/Speichern_16x16.png")));
		mntmSpeichern.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		mnDatei.add(mntmSpeichern);
		mntmSpeichern.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				speichern();
			}
		});
		
		JMenuItem mntmSpeicherUnter = new JMenuItem("Speichern unter...");
		mntmSpeicherUnter.setIcon(new ImageIcon(MainFrame.class.getResource("/bilder/SpeichernUnter_16x16.png")));
		mnDatei.add(mntmSpeicherUnter);
		mntmSpeicherUnter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				speichernUnter();
			}
		});
		
		JSeparator separator_1 = new JSeparator();
		mnDatei.add(separator_1);
		
		JMenuItem mntmSwertSchlieen = new JMenuItem("S-Wert 3.0 schlie\u00DFen");
		mntmSwertSchlieen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				programmSchliessen();
			}
		});
		mnDatei.add(mntmSwertSchlieen);
		
		mnBearbeiten = new JMenu("Bearbeiten");
		mnBearbeiten.setEnabled(false);
		menuBar.add(mnBearbeiten);
		
		JMenu mnLeistungen = new JMenu("Leistung");
		mnBearbeiten.add(mnLeistungen);
		
		JMenuItem mntmLeistungenHinzufgen = new JMenuItem("Leistung hinzuf\u00FCgen");
		mntmLeistungenHinzufgen.setIcon(new ImageIcon(MainFrame.class.getResource("/bilder/NeueLeistung_16x16.png")));
		mnLeistungen.add(mntmLeistungenHinzufgen);
		mntmLeistungenHinzufgen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				LeistungDialog dialog = new LeistungDialog();
				dialog.setVisible(true);
			}
		});
		
		mntmLeistungenBearbeiten = new JMenuItem("Leistung bearbeiten");
		mntmLeistungenBearbeiten.setIcon(new ImageIcon(MainFrame.class.getResource("/bilder/EditLeistung_16x16.png")));
		mnLeistungen.add(mntmLeistungenBearbeiten);
		mntmLeistungenBearbeiten.setEnabled(false);
		mntmLeistungenBearbeiten.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ProfilTab tab = tabList.get(getAktivesTab());
				tab.leistungBearbeiten();
			}
		});
		
		mntmLeistungenLoeschen = new JMenuItem("Leistung l\u00F6schen");
		mntmLeistungenLoeschen.setIcon(new ImageIcon(MainFrame.class.getResource("/bilder/LeistungLoeschen_16x16.png")));
		mntmLeistungenLoeschen.setEnabled(false);
		mnLeistungen.add(mntmLeistungenLoeschen);
		mntmLeistungenLoeschen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					tabList.get(getAktivesTab()).deleteZeileAction();
				} catch (Exception e) {
					System.out.println("Bitte ein Tab wählen");
				}
				
			}
		});
		mainFrame.getContentPane().setLayout(new GridLayout(1, 0, 0, 0));
		
		tabbedPane = new JTabbedPane(SwingConstants.TOP);
		tabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				
				int alleTabs = tabbedPane.getTabCount();
				int selectedTab = tabbedPane.getSelectedIndex();
				if(selectedTab != alleTabs-1) {
					selectedIndex = selectedTab;
					menueVerfuegbar();
				} else {
					menueAusgrauen();
				}
			}
		});
		mainFrame.getContentPane().add(tabbedPane);
		
		JPanel dummyTab = new JPanel();
		tabbedPane.addTab("Start", new ImageIcon(MainFrame.class.getResource("/bilder/Logo_16x16.png")), dummyTab, null);
		dummyTab.setLayout(new MigLayout("", "[grow][205px][grow]", "[5:50:200][14px][2px][23px][20px][][][][2px][23px]"));
		
		JLabel lblLegenSieHier = new JLabel("Legen Sie hier ein neues Athletenprofil an:", SwingConstants.CENTER);
		lblLegenSieHier.setFont(new Font("Tahoma", Font.BOLD, 11));
		dummyTab.add(lblLegenSieHier, "cell 1 1,growx,aligny top");
		
		JSeparator separator_2 = new JSeparator();
		dummyTab.add(separator_2, "cell 0 2 3 1,growx,aligny top");
		
		JButton btnNeuesAthletenprofilAnlegen = new JButton("Neues Athletenprofil anlegen");
		btnNeuesAthletenprofilAnlegen.setToolTipText("Anlegen eines neuen Athletenprofils");
		btnNeuesAthletenprofilAnlegen.setIcon(new ImageIcon(MainFrame.class.getResource("/bilder/NeuerAthlet_24x24.png")));
		btnNeuesAthletenprofilAnlegen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				AthletDialog dialog = new AthletDialog();
				dialog.setVisible(true);
			}
		});
		dummyTab.add(btnNeuesAthletenprofilAnlegen, "cell 1 3,growx,aligny top");
		
		JLabel lblffnenSieEin = new JLabel("\u00D6ffnen Sie ein bestehendes Athletenprofil:");
		lblffnenSieEin.setFont(new Font("Tahoma", Font.BOLD, 11));
		dummyTab.add(lblffnenSieEin, "cell 1 5,alignx left,aligny top");
		
		JSeparator separator_3 = new JSeparator();
		dummyTab.add(separator_3, "cell 0 6 3 1,growx,aligny top");
		
		JButton btnAthletenprofilffnen = new JButton("Athletenprofil \u00F6ffnen");
		btnAthletenprofilffnen.setToolTipText("\u00D6ffnen und Bearbeiten eines bestehende Athletenprofils");
		btnAthletenprofilffnen.setIcon(new ImageIcon(MainFrame.class.getResource("/bilder/EditAthlet_24x24.png")));
		btnAthletenprofilffnen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {						
					DateiPfadÖffnen dö = new DateiPfadÖffnen();
					String pfad = dö.open();						
					if (pfad != null) {							
						if (!csvController.lesen(pfad)){
							fehlermeldungBeiLesen();
						}
					}	
				}catch(Exception d) {
					
				}
			}
		});
		dummyTab.add(btnAthletenprofilffnen, "cell 1 7,growx,aligny top");
	}
	
	public JFrame getContext() {
		return mainFrame;
	}
	
	private void programmSchliessen() {
		Iterator<ProfilTab> iterator = tabList.iterator();
		boolean gespeichert = true;
		while (iterator.hasNext()) {
			ProfilTab tab = iterator.next();
			if (!tab.getSpeicherStatus()) {
				gespeichert = false;
			}
		}
		if(!gespeichert) {
			int art = JOptionPane.showConfirmDialog(mainFrame.getContentPane(),
													"S-Wert 3.0 wird geschlossen.\nWollen Sie Ihre Änderungen speichern?", 
													"Achtung!",
													JOptionPane.YES_NO_OPTION);
    		if (art == 1) {
    			mainFrame.setEnabled(false);
    			mainFrame.dispose();
    		} else if (art== 0) {
    			iterator = tabList.iterator();
    			while(iterator.hasNext()) {
    				ProfilTab tab = iterator.next();
    				if (tab.getSpeicherStatus()) {
    					iterator.remove();
    					tabbedPane.setSelectedComponent(tab);
    					tabbedPane.remove(tabbedPane.getSelectedIndex());
    				} else {
    					speichern();
    				}
    				iterator = tabList.iterator();
    			}
    			System.exit(0);
    		}
		} else {
			System.exit(0);
		}
	}
	
	private void menueVerfuegbar() {
		mntmAthletenprofilSchlieen.setEnabled(true);
		mnBearbeiten.setEnabled(true);
	}
	
	private void menueAusgrauen() {
		mntmAthletenprofilSchlieen.setEnabled(false);
		mnBearbeiten.setEnabled(false);
	}
	
	protected void speichern () {
		int aktivesTab = tabbedPane.getSelectedIndex();
		if (aktivesTab == -1) {
			//TODO Fehlermeldung falls Start ausgewählt
			return;
		}
		ProfilTab tab = (ProfilTab) tabbedPane.getComponentAt(tabbedPane.getSelectedIndex());
		if (tab.getSpeicherPfad()==null){
			try{
				DateiPfadSpeichern ds = new DateiPfadSpeichern();
				String pfad = ds.save(tab.getAthletenName());
				if (pfad == null) {
					return;
				}
				tab.setSpeicherPfad(pfad);
				if (!csvController.schreiben(tab.getSpeicherPfad(),tab)) {
					fehlermeldungBeiSpeichern();
				} else {
					tab.setSpeicherStatus(true);
				}
			}catch (Exception e) {
				//TODO
				e.printStackTrace();
			}
		} else {
			try{											
				if (!csvController.schreiben(tab.getSpeicherPfad(),tab)) {
					fehlermeldungBeiSpeichern();
				} else {
					tab.setSpeicherStatus(true);
				}
			}catch (Exception e) {
				//TODO
				e.printStackTrace();
			}
		}
	}
	
	protected void speichernUnter() {
		int aktivesTab = tabbedPane.getSelectedIndex();
		if (aktivesTab == -1) {
			return;
		}
		ProfilTab tab = (ProfilTab) tabbedPane.getComponentAt(tabbedPane.getSelectedIndex());			
		try{
			DateiPfadSpeichern ds = new DateiPfadSpeichern();
			String pfad = ds.save(tab.getAthletenName());
			if (pfad == null) {
				return;
			}
			tab.setSpeicherPfad(pfad);
			if (!csvController.schreiben(tab.getSpeicherPfad(),tab)) {
				fehlermeldungBeiSpeichern();
			} else {
				tab.setSpeicherStatus(true);				
			}
		}catch (Exception e) {
			//TODO
			e.printStackTrace();
		}
	}
	
	public void createTab (String name) {
			Athlet athlet = athletcontroller.neuerAthlet(name);
			ProfilTab newTab = new ProfilTab(athlet);			
			tabList.add(0, newTab);
			tabbedPane.insertTab("* "+name, null, newTab, null, 0);
			tabbedPane.setSelectedIndex(0);	
	}
	
	public ProfilTab createTab (String name,long id) {
		Athlet athlet = athletcontroller.neuerAthlet(name,id);
		ProfilTab newTab = new ProfilTab(athlet);			
		tabList.add(0, newTab);
		tabbedPane.insertTab(name+" *", null, newTab, null, 0);
		tabbedPane.setSelectedIndex(0);
		return newTab;
	}
	
	public boolean checkAthletGeöffnet (String name, long id) {		
		for (int i = 0; i < tabList.size(); i++) {			
			ProfilTab tab = tabList.get(i);
			String tabName = tab.getAthletenName();
			long tabId = tab.getAthletenId();			
			if (tabName.equals(name) && id == tabId) {
				return true;
			}			
		}		
		return false;
	}
	
	public void fehlermeldungBeiSpeichern() {	
		JOptionPane.showMessageDialog(mainFrame, 
				"Es ist ein Fehler beim Speichern der Datei aufgetreten, bitte probieren Sie es noch einmal.", 
				"Fehler beim Speichern",
				JOptionPane.ERROR_MESSAGE);
	}
	
	public void fehlermeldungBeiLesen() {
		JOptionPane.showMessageDialog(mainFrame,
				"Es ist ein Fehler beim Öffnen der Datei aufgetreten, bitte probieren Sie es noch einmal.",
				"Fehler beim Öffnen der Datei",
				JOptionPane.ERROR_MESSAGE);
	}
	
	public int getAktivesTab() {		
		for (int i = 0; i < tabList.size(); i++) {
			if (tabList.get(i).isShowing() == true) {
				return i;
			}
		}
		return -1;		
	}
	
	public void leistungBearbeitenVerfügbar() {
		mntmLeistungenBearbeiten.setEnabled(true);
	}
	
	public void leistungBearbeitenAusgrauen() {
		mntmLeistungenBearbeiten.setEnabled(false);
	}
	
	public void leistungLöschenVerfügbar() {
		mntmLeistungenLoeschen.setEnabled(true);
	}
	
	public void leistungLöschenAusgrauen() {
		mntmLeistungenLoeschen.setEnabled(false);
	}
}
