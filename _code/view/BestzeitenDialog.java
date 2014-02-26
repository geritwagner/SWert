package view;

import helper.IntegerDocument;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import main.Main;
import model.Athlet;
import model.Leistung;
import controller.FunktionenController;
import controller.LeistungController;
import controller.StreckenController;

/**
 * Dialog zum Anzeigen der m�glichen Bestzeiten des Athleten �ber eine w�hlbare Strecke
 * @author Honors-WInfo-Projekt (Fabian B�hm, Alexander Puchta)
 */
public class BestzeitenDialog extends JDialog {

//----------------------- VARIABLEN -----------------------	
	private static final long serialVersionUID = 1L;
	private JPanel contentPanel = new JPanel();
	private Dimension d = this.getToolkit().getScreenSize();
	private LeistungController lController = Main.leistungController;
	private StreckenController sController = Main.streckenController;
	private FunktionenController fController = Main.funktionenController;
	
	private JTable trainingsTabelle;
	private JTextField txtFieldZeit;
	private JTextField txtFieldStrecke;
	
	private Athlet athlet;
	private Leistung leistungAuswahl;
	private double slopeFaktor;

//----------------------- KONSTRUKTOREN -----------------------
	/**
	 * Konstruktor zum Erzeugen des Dialogs
	 * @param leistungAuswahl: Array mit 2 Leistungsobjekten
	 * @param slopeFaktor
	 */
	public BestzeitenDialog(Athlet inputAthlet) {	
		this.athlet = inputAthlet;
		if (athlet.isSetAnaerobeSchwelle()){
			initProperties();
			initComponents();
			setModal(true);
			setVisible(true);		
		}
	}	

//----------------------- PRIVATE METHODEN -----------------------
	/**
	 * Initialisieren der Eigenschaften des Dialog-Fensters
	 */
	private void initProperties() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(BestzeitenDialog.class.getResource("/bilder/Pokal_24x24.png")));
		setResizable(false);
		setTitle("Bestzeiten berechnen");
		setBounds(100, 100, 262, 408);
		setLocation((int) ((d.getWidth() - this.getWidth()) / 2), (int) ((d.getHeight() - this.getHeight()) / 2));
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}
	
	/**
	 * Initialisieren der Komponenten
	 */
	private void initComponents() {
		initComponentsGeneral();		
		initJTable();
		initButtonPane();	
	}
	
	/**
	 * Initialisieren der Komponenten im Fenster
	 */
	private void initComponentsGeneral() {
		JLabel lblBestzeiten = new JLabel("Bestzeiten");
		lblBestzeiten.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblBestzeiten.setBounds(10, 11, 258, 14);
		contentPanel.add(lblBestzeiten);
		
		JSeparator separator1 = new JSeparator();
		separator1.setBounds(10, 27, 234, 2);
		contentPanel.add(separator1);		
		
		JSeparator separator2 = new JSeparator();
		separator2.setBounds(10, 267, 234, 2);
		contentPanel.add(separator2);
		
		JSeparator separator3 = new JSeparator();
		separator3.setBounds(10, 330, 234, 2);
		contentPanel.add(separator3);
		
		
		JLabel lblStreckenlnge = new JLabel("Streckenl\u00E4nge");
		lblStreckenlnge.setBounds(10, 280, 93, 14);
		contentPanel.add(lblStreckenlnge);
		
		JLabel lblBestzeit = new JLabel("Bestzeit");
		lblBestzeit.setBounds(10, 305, 93, 14);
		contentPanel.add(lblBestzeit);
		
		
		txtFieldZeit = new JTextField();
		txtFieldZeit.setEditable(false);
		txtFieldZeit.setBounds(111, 302, 68, 20);
		contentPanel.add(txtFieldZeit);
		txtFieldZeit.setColumns(10);
	
		txtFieldStrecke = new JFormattedTextField();
		txtFieldStrecke.setToolTipText("Geben Sie hier die Strecke ein, f\u00FCr die Sie die m\u00F6gliche Bestzeit erfahren m\u00F6chten");
		txtFieldStrecke.setBounds(111, 277, 68, 20);
		txtFieldStrecke.setDocument(new IntegerDocument());
		txtFieldStrecke.getDocument().addDocumentListener(new IntegerDocumentListener());
		contentPanel.add(txtFieldStrecke);
		
		JLabel lblM = new JLabel("m");
		lblM.setBounds(182, 280, 46, 14);
		contentPanel.add(lblM);
	}
	
	/**
	 * Initialisieren des Buttons "Schlie�en"
	 */
	private void initButtonPane() {
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		
		JButton cancelButton = new JButton("Schlie\u00DFen");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				BestzeitenDialog.this.setVisible(false);
				BestzeitenDialog.this.dispose();
			}
		});
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);

	}
	
	/**
	 * Initialisieren der Tabelle zum Anzeigen der m�glichen Bestzeiten
	 * zu den einzelnen fixen Strecken
	 */
	private void initJTable() {	
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportBorder(null);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(10, 36, 234, 226);
		contentPanel.add(scrollPane);
		
		trainingsTabelle = new JTable();
		trainingsTabelle.setModel(new DefaultTableModel(
				berechneBestzeit(),
				new String[] {
					"Streckenl�nge", "Bestzeit"
				}
				));
		trainingsTabelle.getColumnModel().getColumn(0).setPreferredWidth(85);
		trainingsTabelle.getColumnModel().getColumn(0).setMinWidth(95);
		trainingsTabelle.getColumnModel().getColumn(1).setPreferredWidth(60);
		trainingsTabelle.getTableHeader().setReorderingAllowed(false);
		trainingsTabelle.setEnabled(false);
		scrollPane.setViewportView(trainingsTabelle);
	}
	
	/**
	 * Auslesen des Wertes im Textfeld f�r die Strecke und 
	 * Berechnen der entsprechenden m�glichen Bestzeit
	 */
	private void berechneZeit() {
		String streckenString = txtFieldStrecke.getText();
    	if (streckenString!= null) {
    		int strecke = -1;
    		try{
    			strecke = Integer.parseInt(streckenString);
    		} catch (Exception h) {
    			strecke = -1;
    		}  
    		if (strecke == -1) {
    			return;
    		}
    		
    		double bestzeit = athlet.calculateSpeed(strecke);
			String bestzeitString = lController.parseSecInMinutenstring(bestzeit);
			txtFieldZeit.setText(bestzeitString);
    	}
	}
	
	/**
	 * Berechnen der m�glichen Bestzeiten zu den fixen Streckenl�ngen,
	 * die direkt an die JTable �bergeben werden
	 * @return: Array mit [][0] als Streckenl�nge (String) und [][1] als Bestzeit (String: 00:00,00)
	 */
	private Object[][] berechneBestzeit() {
		int streckenAnzahl = sController.getStreckenLength();
		Object[][] data = new Object [streckenAnzahl][2];
		for (int i = 0; i < streckenAnzahl; i++) {
			double streckenL�nge = sController.getStreckenlaengeById(i);
			data[i][0] = sController.getStreckenlaengeStringById(i);
			double bestzeit = athlet.calculateSpeed(streckenL�nge);
			String bestzeitString = lController.parseSecInMinutenstring(bestzeit);
			data[i][1] = bestzeitString;			
		}
		return data;
	}

//----------------------- PRIVATE KLASSEN -----------------------
	/**
	 * Erweiterung zum DocumentListener
	 * Echtzeitverarbeitung von Integerwerten 
	 * @author Honors-WInfo-Projekt (Fabian B�hm, Alexander Puchta)
	 */
	private class IntegerDocumentListener implements DocumentListener {    
		@Override
		public void insertUpdate(DocumentEvent e) {
			berechneZeit();
		}
		
		@Override
		public void removeUpdate(DocumentEvent e) {
			berechneZeit();
		}
		
		@Override
		public void changedUpdate(DocumentEvent e) {
			berechneZeit();
		}
	}
}
