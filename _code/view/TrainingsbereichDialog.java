package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;

import controller.Einheitenumrechner;
import controller.LeistungController;

/**
 * Dialog zum Anzeigen einer Tabelle der Trainingsbereiche des Athleten
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta)
 */
public class TrainingsbereichDialog extends JDialog {

//----------------------- VARIABLEN -----------------------
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private final Dimension d = this.getToolkit().getScreenSize();
	private LeistungController l = new LeistungController();
		
	private double anaerobeSchwelle;
	private JTextField txtAnaerobeSchwelle;
	private JTable trainingsTabelle;
	private JSlider slider;
	private JLabel lblRundenanzahl;
	private JScrollPane scrollPane;
	
	//Entspricht 1 Runde in Sekunden
	private double winzererRundengeschindigkeit;
	
	private final ButtonGroup buttonGroup = new ButtonGroup();

//----------------------- KONSTRUKTOREN -----------------------
	/**
	 * Konstruktor zum Erzeugen des Dialogs
	 */
	public TrainingsbereichDialog(double anaerobeSchwelle) {
		this.anaerobeSchwelle = anaerobeSchwelle;
		initProperties();
		initComponents();
		setModal(true);
		setVisible(true);
	}
	
//----------------------- PRIVATE METHODEN -----------------------
	/**
	 * Initialisieren der Eigenschaften des Dialog-Fensters
	 */
	private void initProperties() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(TrainingsbereichDialog.class.getResource("/bilder/Berechnen_24x24.png")));
		setResizable(false);
		setTitle("Trainingsbereiche berechnen");
		setBounds(100, 100, 350, 439);
		setLocation((int) ((d.getWidth() - this.getWidth()) / 2), (int) ((d.getHeight() - this.getHeight()) / 2));
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		//contentPanel.setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{textFieldBezeichnung, comboBoxStrecke, textFieldZeit, textFieldGeschwindigkeit, comboBoxGeschwindigkeit}));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}
	
	/**
	 * Initialisieren der Komponenten
	 */
	private void initComponents() {
		initComponentsGeneral();
		initComponentsSpecific();
		initJTable();
		initButtonPane();
		
	}
	
	/**
	 * Initialisieren der "oberen" Komponenenten (Abschnitt: "Allgemeine Daten")
	 */
	private void initComponentsGeneral() {
		JLabel lblAnaerobeSchwelle = new JLabel("Aerob/Anaerobe Schwelle");
		lblAnaerobeSchwelle.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblAnaerobeSchwelle.setBounds(10, 11, 258, 14);
		contentPanel.add(lblAnaerobeSchwelle);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(10, 27, 324, 2);
		contentPanel.add(separator);		
	}
	
	/**
	 * Initialisieren der "unteren Komponenten (Abschnitt: "Spezifische Daten")
	 */
	private void initComponentsSpecific() {
		JLabel lblTrainingsbereiche = new JLabel("Trainingsbereiche");
		lblTrainingsbereiche.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblTrainingsbereiche.setBounds(10, 131, 258, 14);
		contentPanel.add(lblTrainingsbereiche);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(10, 146, 324, 2);
		contentPanel.add(separator_1);		
			
		txtAnaerobeSchwelle = new JTextField();
		txtAnaerobeSchwelle.setText(l.parseSecInMinutenstring(anaerobeSchwelle));
		txtAnaerobeSchwelle.setEditable(false);
		txtAnaerobeSchwelle.setColumns(10);
		txtAnaerobeSchwelle.setBounds(20, 36, 86, 20);
		contentPanel.add(txtAnaerobeSchwelle);
		
		
		slider = new JSlider();
		slider.setPaintLabels(true);
		slider.setPaintTicks(true);
		slider.setSnapToTicks(true);
		slider.setMinimum(1);
		slider.setValue(1);
		slider.setMaximum(6);
		slider.setBounds(112, 81, 115, 39);
	    slider.setLabelTable(slider.createStandardLabels(1));	
	    slider.setVisible(false);	    
	    
	    slider.addChangeListener(new ChangeListener() {
	        @Override
			public void stateChanged(ChangeEvent evt) {
	          JSlider slider = (JSlider) evt.getSource();
	          if (!slider.getValueIsAdjusting()) {
	            updateRundenzeit(slider.getValue());	            
	          }
	        }
	      });
	      
		contentPanel.add(slider);
		
		scrollPane = new JScrollPane();
		scrollPane.setViewportBorder(null);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(10, 159, 324, 206);
		contentPanel.add(scrollPane);
			
		lblRundenanzahl = new JLabel("Rundenanzahl");
		lblRundenanzahl.setBounds(20, 86, 86, 14);
		contentPanel.add(lblRundenanzahl);
		lblRundenanzahl.setVisible(false);
		
		JRadioButton rdbtnNewRadioButton = new JRadioButton("flache Strecke");
		buttonGroup.add(rdbtnNewRadioButton);
		rdbtnNewRadioButton.setSelected(true);
		rdbtnNewRadioButton.setBounds(112, 32, 156, 23);
		contentPanel.add(rdbtnNewRadioButton);
		rdbtnNewRadioButton.addActionListener(new ActionListener(){
		    @Override
			public void actionPerformed(ActionEvent e) {
		      initJTable();		      
		      slider.setVisible(false);
		      lblRundenanzahl.setVisible(false);
		    }
		});
		
		JRadioButton rdbtnProfilierteStrecke = new JRadioButton("RWH");
		buttonGroup.add(rdbtnProfilierteStrecke);
		rdbtnProfilierteStrecke.setBounds(112, 51, 156, 23);
		contentPanel.add(rdbtnProfilierteStrecke);
		rdbtnProfilierteStrecke.addActionListener(new ActionListener(){
		    @Override
			public void actionPerformed(ActionEvent e) {
		      initJTableProfiliert();
		      slider.setVisible(true);
		      lblRundenanzahl.setVisible(true);
		    }
		});
	}
	
	/**
	 * Initialisieren des Buttons "Schließen"
	 */
	private void initButtonPane() {
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		
		JButton cancelButton = new JButton("Schlie\u00DFen");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				TrainingsbereichDialog.this.setVisible(false);
				TrainingsbereichDialog.this.dispose();
			}
		});
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);
	}
	
	/**
	 * Initialisieren der Tabelle zur Anzeige der verschiedenen einfachen Trainingsbereiche
	 */
	private void initJTable() {	
		trainingsTabelle = new JTable();
		trainingsTabelle.setModel(new DefaultTableModel(
			berechneTrainingsBereiche(),
			new String[] {
				"Bereich", 
				"1.000m Zeit",
				"km/h",
				"m/s"
			}
		));
		trainingsTabelle.getColumnModel().getColumn(0).setPreferredWidth(60);
		trainingsTabelle.getColumnModel().getColumn(0).setMinWidth(60);
		trainingsTabelle.getColumnModel().getColumn(1).setPreferredWidth(65);
		trainingsTabelle.getColumnModel().getColumn(2).setPreferredWidth(55);
		trainingsTabelle.getColumnModel().getColumn(3).setPreferredWidth(55);
		trainingsTabelle.getTableHeader().setReorderingAllowed(false);
		trainingsTabelle.setEnabled(false);
		scrollPane.setViewportView(trainingsTabelle);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(10, 376, 324, 2);
		contentPanel.add(separator);		
	}

	/**
	 * Initialisieren der Tabelle zum Anzeigen der profilierten Trainingsbereiche
	 * mit Rundengeschwindigkeiten
	 */
	private void initJTableProfiliert() {	
		trainingsTabelle = new JTable();
		trainingsTabelle.setModel(new DefaultTableModel(
			berechneTrainingsBereicheProfiliert(),
			new String[] {
				"Bereich", 
				"1.000m Zeit",
				"km/h",
				"m/s",
				"Gesamtrunde"
			}
		));
		trainingsTabelle.getColumnModel().getColumn(0).setPreferredWidth(60);
		trainingsTabelle.getColumnModel().getColumn(0).setMinWidth(60);
		trainingsTabelle.getColumnModel().getColumn(1).setPreferredWidth(65);
		trainingsTabelle.getColumnModel().getColumn(2).setPreferredWidth(55);
		trainingsTabelle.getColumnModel().getColumn(3).setPreferredWidth(55);
		trainingsTabelle.getTableHeader().setReorderingAllowed(false);
		trainingsTabelle.setEnabled(false);
		scrollPane.setViewportView(trainingsTabelle);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(10, 326, 258, 2);
		contentPanel.add(separator);	
	}

	/**
	 * Berechnen der Trainingsbereiche, die direkt an die JTable übergeben werden
	 * @return: [x][y] --> x enthält die verschiedenen Strecke,
	 * y die entsprechenden Geschwindigkeiten in kmh, ms, minkm
	 */
	private Object[][] berechneTrainingsBereiche() {	
		DecimalFormat f = new DecimalFormat("#0.00");
		Object[][] data = new Object [11][4];
		int untereSchranke = 60;
		int obereSchranke = 110;
		int schritt = 5;	
		int zähler = 0;
		
		for (int i = untereSchranke; i <= obereSchranke; i = i + schritt) {
			double gewichtung = (200-i)/100D;
			double trainingsGeschwindigkeitSKm = gewichtung * anaerobeSchwelle;			
			data[zähler][0] = String.valueOf(i) + "%";
			data[zähler][1] = l.parseSecInMinutenstring(trainingsGeschwindigkeitSKm);	
			data[zähler][2] = f.format(Einheitenumrechner.toKmH(trainingsGeschwindigkeitSKm));
			data[zähler][3] = f.format(Einheitenumrechner.toMS(trainingsGeschwindigkeitSKm));
			zähler++;
		}
		
		return data;	
	}
	
	/**
	 * Berechnen der Trainingsbereiche, die direkt an die JTable übergeben werden
	 * @return: [x][y] --> x enthält die verschiedenen Strecke,
	 * y die entsprechenden Geschwindigkeiten in kmh, ms, minkm, rundengeschwindigkeit
	 */
	private Object[][] berechneTrainingsBereicheProfiliert() {	
		DecimalFormat f = new DecimalFormat("#0.00");
		Object[][] data = new Object [11][5];
		int untereSchranke = 60;
		int obereSchranke = 110;
		int schritt = 5;	
		int zähler = 0;
		double winzererAufschlag = 1.03;
		double winzererRundenlänge = 3.409;
		
		for (int i = untereSchranke; i <= obereSchranke; i = i + schritt) {
			double gewichtung = (200-i)/100D;
			double trainingsGeschwindigkeitSKm = gewichtung * anaerobeSchwelle* winzererAufschlag;	
			winzererRundengeschindigkeit = trainingsGeschwindigkeitSKm*winzererRundenlänge;
			int rundenAnzahl = slider.getValue();
			data[zähler][0] = String.valueOf(i) + "%";
			data[zähler][1] = l.parseSecInMinutenstring(trainingsGeschwindigkeitSKm);	
			data[zähler][2] = f.format(Einheitenumrechner.toKmH(trainingsGeschwindigkeitSKm));
			data[zähler][3] = f.format(Einheitenumrechner.toMS(trainingsGeschwindigkeitSKm));
			data[zähler][4] = l.parseSecInMinutenstring(winzererRundengeschindigkeit*rundenAnzahl);
			zähler++;
		}		
		return data;	
	}
	
	/**
	 * Updaten der einzelenen Rundengeschwindigkeit in Abhängigkeit der Rundenanzahl
	 * @param rundenAnzahl: aktuell im Slider eingestellte Rundenzahl
	 */
	private void updateRundenzeit (int rundenAnzahl) {	
		int untereSchranke = 60;
		int obereSchranke = 110;
		int schritt = 5;	
		int zähler = 0;
		double winzererAufschlag = 1.03;
		double winzererRundenlänge = 3.409;
		
		for (int i = untereSchranke; i <= obereSchranke; i = i + schritt) {
			double gewichtung = (200-i)/100D;
			double trainingsGeschwindigkeitSKm = gewichtung * anaerobeSchwelle* winzererAufschlag;
			winzererRundengeschindigkeit = trainingsGeschwindigkeitSKm*winzererRundenlänge;
			String MinutenString = l.parseSecInMinutenstring(winzererRundengeschindigkeit*rundenAnzahl);
			trainingsTabelle.setValueAt(MinutenString, zähler, 4);
			zähler++;
		}		
	}
}