package view;

import helper.DezimalDocument;
import helper.IntegerDocument;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.MaskFormatter;

import main.Main;
import model.Leistung;
import controller.Einheitenumrechner;
import controller.LeistungController;

/**
 * Dialog zum Anlegen einer neuen Leistung durch
 * direkte Eingabe einer Schwelle
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta)
 */
public class SchwellenDialog extends JDialog {

//----------------------- VARIABLEN -----------------------
	private static final long serialVersionUID = 1L;
	
	private final JPanel contentPanel = new JPanel();
	private final Dimension d = this.getToolkit().getScreenSize(); 
	
	private final LeistungController lController = Main.leistungController;
	private MainFrame mainFrame = Main.mainFrame;
	
	private JRadioButton rdbtnStrecke;
	private JFormattedTextField textFieldStrecke;
	private JLabel lblStreckeError;
	private JRadioButton rdbtnkmH;
	private JTextField textFieldkmH;
	private JRadioButton rdbtnms;
	private JTextField textFieldMs;
	private JRadioButton rdbtnminkm;
	private JFormattedTextField textFieldminKm;
	
	private double geschwindigkeit; //Enthält immer die aktuelle, berechnete Geschwindigkeits
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JLabel lblKmhError;
	private JLabel lblMsError;
	private JLabel lblMinKmError;

//----------------------- KONSTRUKTOREN -----------------------
	/**
	 * Konstruktor zum Erzeugen des Dialogs
	 */
	public SchwellenDialog() {
		initProperties();
		initAllComponents();
		clearWarnings();
	}
	
//----------------------- ÖFFENTLICHE METHODEN -----------------------
	/**
	 * Füllen des Dialogs mit Werten aus einer bestehenden Leistung
	 * @param leistung
	 */
	public void initWerte(Leistung leistung) {
		setIconImage(Toolkit.getDefaultToolkit().getImage(LeistungDialog.class.getResource("/bilder/EditLeistung_24x24.png")));
		this.geschwindigkeit = leistung.getGeschwindigkeit();
		setzeStrecke(geschwindigkeit);
		setzeKmH(geschwindigkeit);
		setzeMs(geschwindigkeit);
		setzeMinKm(geschwindigkeit);
		clearWarnings();
	}

//----------------------- PRIVATE METHODEN -----------------------
	/**
	 * Initialisieren der Eigenschaften des Dialog-Fensters
	 */
	private void initProperties() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(SchwellenDialog.class.getResource("/bilder/NeueLeistung_16x16.png")));
		setResizable(false);
		setTitle("Anaerobe Schwelle eingeben");
		setBounds(100, 100, 454, 219);
		setLocation((int) ((d.getWidth() - this.getWidth()) / 2), (int) ((d.getHeight() - this.getHeight()) / 2));
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setModal(true);
	}
	
	/**
	 * Initialisieren der Komponenten
	 */
	private void initAllComponents() {
		initComponents();
		initButtonPane();
	}
	
	/**
	 * Initialisieren der "unteren Komponenten (Abschnitt: "Spezifische Daten")
	 */
	private void initComponents() {
		
		JLabel lblDaten = new JLabel("Bitte geben Sie Ihre Werte ein:");
		lblDaten.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblDaten.setBounds(10, 11, 424, 14);
		contentPanel.add(lblDaten);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(10, 36, 428, 2);
		contentPanel.add(separator_1);
		
		initRadioButtonStrecke();
			
        initTextFieldStrecke();
		
		lblStreckeError = new JLabel("");
		lblStreckeError.setBounds(236, 53, 202, 14);
		contentPanel.add(lblStreckeError);
		
		initRadioButtonKmH();
		
		initTextFieldKmH();

		lblKmhError = new JLabel("");
		lblKmhError.setBounds(236, 79, 202, 14);
		contentPanel.add(lblKmhError);
		buttonGroup.add(rdbtnms);
		
		initRadioButtonMs();		
		rdbtnms.setBounds(10, 101, 95, 23);
		contentPanel.add(rdbtnms);
		
		initTextFieldMs();
		
		lblMsError = new JLabel("");
		lblMsError.setBounds(236, 105, 202, 14);
		contentPanel.add(lblMsError);
		buttonGroup.add(rdbtnminkm);
		
		initRadioButtonMinKm();
		rdbtnminkm.setBounds(10, 127, 95, 23);
		contentPanel.add(rdbtnminkm);
		
		initTextFieldMinKm();
		
		lblMinKmError = new JLabel("");
		lblMinKmError.setBounds(236, 131, 202, 14);
		contentPanel.add(lblMinKmError);
		
		JSeparator separator_2 = new JSeparator();
		separator_2.setBounds(10, 157, 428, 2);
		contentPanel.add(separator_2);
	}
	
	/**
	 * Initialisieren der beiden Buttons "Bestätigen" und "Abbrechen"
	 */
	private void initButtonPane() {
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		
		JButton okButton = new JButton("Best\u00E4tigen");
		okButton.setToolTipText("Anlegen der Schwelle");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(actionBestaetigen()) {
					mainFrame.tabList.get(mainFrame.getAktivesTab()).deleteZeileAusDialog();
					SchwellenDialog.this.setVisible(false);
					SchwellenDialog.this.dispose();					
				} else {
					JOptionPane.showMessageDialog(contentPanel,
							"Schwelle wurde nicht erstellt. Bitte überprüfen Sie ihre Eingaben.",
						    "Fehler",
						    JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
		
		JButton cancelButton = new JButton("Abbrechen");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				SchwellenDialog.this.setVisible(false);
				SchwellenDialog.this.dispose();
			}
		});
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);
	}
	
	/**
	 * Initialisieren der Funktionalität des strecke-RadioButtons
	 */
	private void initRadioButtonStrecke() {
		rdbtnStrecke = new JRadioButton("Strecke in [m]:");
		rdbtnStrecke.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (rdbtnStrecke.isSelected()) {
					textFieldStrecke.setText(null);
					textFieldStrecke.setEnabled(true);;
					textFieldkmH.setText("");
					textFieldkmH.setEnabled(false);
					textFieldMs.setText("");
					textFieldMs.setEnabled(false);
					textFieldminKm.setText("");
					textFieldminKm.setEnabled(false);
					lblStreckeError.setText("");
					lblKmhError.setText("");
					lblMsError.setText("");
					lblMinKmError.setText("");
				}
			}
		});
		buttonGroup.add(rdbtnStrecke);
		rdbtnStrecke.setBounds(10, 49, 95, 23);
		rdbtnStrecke.setSelected(true);
		contentPanel.add(rdbtnStrecke);
	}
	
	/**
	 * Initialisieren des "Strecke"-Textfelds
	 */
	private void initTextFieldStrecke() {
		textFieldStrecke = new JFormattedTextField();
		textFieldStrecke.setToolTipText("L\u00E4nge der Strecke in Metern");
        textFieldStrecke.setDocument(new IntegerDocument());
		textFieldStrecke.setColumns(10);
		textFieldStrecke.getDocument().addDocumentListener(new IntegerDocumentListener());
		textFieldStrecke.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				String strecke = textFieldStrecke.getText();
				if(checkStrecke(strecke)) {
					setzeGeschwindigkeiten();
				}
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				lblStreckeError.setText("");
			}
		});
		textFieldStrecke.setBounds(111, 52, 101, 20);
		contentPanel.add(textFieldStrecke);
		textFieldStrecke.setColumns(10);
	}
	
	/**
	 * Initialisieren der Funtkionalität des kmh-RadioButtons
	 */
	private void initRadioButtonKmH() {
		rdbtnkmH = new JRadioButton("[km/h]:");
		rdbtnkmH.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (rdbtnkmH.isSelected()) {
					textFieldStrecke.setText(null);
					textFieldStrecke.setEnabled(false);;
					textFieldkmH.setText("");
					textFieldkmH.setEnabled(true);
					textFieldMs.setText("");
					textFieldMs.setEnabled(false);
					textFieldminKm.setText("");
					textFieldminKm.setEnabled(false);
					lblStreckeError.setText("");
					lblKmhError.setText("");
					lblMsError.setText("");
					lblMinKmError.setText("");
				}
			}
		});
		buttonGroup.add(rdbtnkmH);
		rdbtnkmH.setBounds(10, 75, 95, 23);
		contentPanel.add(rdbtnkmH);
	}
	
	/**
	 * Initialisieren des Textfelds zur Eingabe der Geschwindigkeit in Kmh
	 */
	private void initTextFieldKmH() {
		textFieldkmH = new JTextField();
		textFieldkmH.setToolTipText("Geschwindigkeit in [km/h] mit zwei Nachkommastellen");
		textFieldkmH.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				String geschwindigkeitString = textFieldkmH.getText();
				if (checkKmh(geschwindigkeitString)) {
					setzeStrecke(geschwindigkeit);
					setzeMs(geschwindigkeit);
					setzeMinKm(geschwindigkeit);
					checkMinMaxGeschwindigkeit(lblKmhError, geschwindigkeit);
				}
			}
			@Override
			public void focusGained(FocusEvent e) {
				lblKmhError.setText("");
			}
		});
		textFieldkmH.setEnabled(false);
		textFieldkmH.setBounds(111, 78, 101, 20);
		textFieldkmH.setDocument(new DezimalDocument());
		contentPanel.add(textFieldkmH);
		textFieldkmH.setColumns(10);
	}
	
	/**
	 * Initialisieren der Funktionalität des Ms-RadioButtons
	 */
	private void initRadioButtonMs() {
		rdbtnms = new JRadioButton("[m/s]:");
		rdbtnms.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (rdbtnms.isSelected()) {
					textFieldStrecke.setText(null);
					textFieldStrecke.setEnabled(false);;
					textFieldkmH.setText("");
					textFieldkmH.setEnabled(false);
					textFieldMs.setText("");
					textFieldMs.setEnabled(true);
					textFieldminKm.setText("");
					textFieldminKm.setEnabled(false);
					lblStreckeError.setText("");
					lblKmhError.setText("");
					lblMsError.setText("");
					lblMinKmError.setText("");
				}
			}
		});
	}
	
	/**
	 * Initialisieren des Textfelds zur Eingabe der Geschwindigkeit in ms
	 */
	private void initTextFieldMs() {
		textFieldMs = new JTextField();
		textFieldMs.setToolTipText("Geschwindigkeit in [m/s] mit zwei Nachkommastellen");
		textFieldMs.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				String geschwindigkeitString = textFieldMs.getText();
				if (checkMs(geschwindigkeitString)) {
					setzeStrecke(geschwindigkeit);
					setzeKmH(geschwindigkeit);
					setzeMinKm(geschwindigkeit);
					checkMinMaxGeschwindigkeit(lblMsError, geschwindigkeit);
				}
			}
			@Override
			public void focusGained(FocusEvent e) {
				lblMsError.setText("");
			}
		});
		textFieldMs.setEnabled(false);
		textFieldMs.setBounds(111, 104, 101, 20);
		textFieldMs.setDocument(new DezimalDocument());
		contentPanel.add(textFieldMs);
		textFieldMs.setColumns(10);
	}
	
	/**
	 * Initialisieren der Funktionalität des minkm-RadioButtons
	 */
	private void initRadioButtonMinKm() {
		rdbtnminkm = new JRadioButton("[min/km]:");
		rdbtnminkm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (rdbtnminkm.isSelected()) {
					textFieldStrecke.setText(null);
					textFieldStrecke.setEnabled(false);;
					textFieldkmH.setText("");
					textFieldkmH.setEnabled(false);
					textFieldMs.setText("");
					textFieldMs.setEnabled(false);
					textFieldminKm.setText("");
					textFieldminKm.setEnabled(true);
					lblKmhError.setText("");
					lblMsError.setText("");
					lblMinKmError.setText("");
				}
			}
		});
	}
	
	
	/**
	 * Initialisieren des Textfelds zur Eingabe der Geschwindigkeit in minkm
	 */
	private void initTextFieldMinKm() {
		MaskFormatter minKmMaske;
		try {
			minKmMaske = new MaskFormatter("##:##,##");
			minKmMaske.setPlaceholderCharacter('0');
			minKmMaske.setValidCharacters("1234567890");
			textFieldminKm = new JFormattedTextField(minKmMaske);
			textFieldminKm.setToolTipText("Geschwindigkeit in [min/km] im Format \"minmin:secsec,msecmsec\"");
			textFieldminKm.addFocusListener(new FocusAdapter() {
				@Override
				public void focusLost(FocusEvent e) {
					String geschwindigkeitString = textFieldminKm.getText();
					if (checkMinKm(geschwindigkeitString)) {
						setzeStrecke(geschwindigkeit);
						setzeKmH(geschwindigkeit);
						setzeMs(geschwindigkeit);
						checkMinMaxGeschwindigkeit(lblMinKmError, geschwindigkeit);
					}
				}
				@Override
				public void focusGained(FocusEvent e) {
					lblMinKmError.setText("");
				}
			});
			textFieldminKm.setEnabled(false);
			textFieldminKm.setBounds(111, 130, 101, 20);
		} catch (ParseException e) {
			//TODO
		}
		contentPanel.add(textFieldminKm);
	}
	
	/**
	 * Überprüfen, ob eine Strecke eingegeben wurde, falls RadioButton für Strecke ausgewählt
	 * @param strecke: String aus dem Textfeld für die "Bezeichnung" --> textFieldBezeichnung
	 * @return FALSE falls keine Bezeichnung eingegenen wurde | TRUE falls eine Bezeichnung eingegeben wurde
	 */
	private boolean checkStrecke(String strecke) {
		if (strecke.equals("")) {
			lblStreckeError.setText("Bitte geben Sie eine Strecke ein!");
			return false;
		}
		return true;
	}
	
	/**
	 * Überprüfen, ob eine gültige Geschwindigkeit eingegeben wurde
	 * (kmh-RadioButton augewählt)
	 * @param geschwindigkeitString: Der aus dem Textfeld ausgelesen String
	 * @return TRUE für gültige Geschwindigkeit | FALSE für ungültige Geschwindigkeit
	 */
	private boolean checkKmh(String geschwindigkeitString) {
		if (geschwindigkeitString.equals("")) {
			lblKmhError.setText("Bitte geben Sie eine Geschwindigkeit ein!");
			return false;
		} else {
			double geschwindigkeit;
			Number geschwindigkeitNumber;
			NumberFormat format = NumberFormat.getInstance(Locale.GERMANY);
			
			try {
				geschwindigkeitNumber = format.parse(geschwindigkeitString);
				geschwindigkeit = geschwindigkeitNumber.doubleValue();
			} catch (ParseException e) {
				lblKmhError.setText("Bitte geben Sie eine gültige Zahl ein!");
				//TODO
				return false;
			}
			geschwindigkeit = Einheitenumrechner.kmHToSKm(geschwindigkeit);
			this.geschwindigkeit = geschwindigkeit;
			checkMinMaxGeschwindigkeit(lblKmhError, this.geschwindigkeit);
			return true;
		}
	}
	
	/**
	 * Überprüfen, ob eine gültige Geschwindigkeit eingegeben wurde
	 * (ms-RadioButton augewählt)
	 * @param geschwindigkeitString: Der aus dem Textfeld ausgelesen String
	 * @return TRUE für gültige Geschwindigkeit | FALSE für ungültige Geschwindigkeit
	 */
	private boolean checkMs(String geschwindigkeitString) {
		if (geschwindigkeitString.equals("")) {
			lblMsError.setText("Bitte geben Sie eine Geschwindigkeit ein!");
			return false;
		} else {
			double geschwindigkeit;
			Number geschwindigkeitNumber;
			NumberFormat format = NumberFormat.getInstance(Locale.GERMANY);
			
			try {
				geschwindigkeitNumber = format.parse(geschwindigkeitString);
				geschwindigkeit = geschwindigkeitNumber.doubleValue();
			} catch (ParseException e) {
				lblMsError.setText("Bitte geben Sie eine gültige Zahl ein!");
				//TODO
				return false;
			}
			geschwindigkeit = Einheitenumrechner.MSToSKm(geschwindigkeit);
			this.geschwindigkeit = geschwindigkeit;
			checkMinMaxGeschwindigkeit(lblMsError, this.geschwindigkeit);
			return true;
		}
	}
	
	/**
	 * Überprüfen, ob eine gültige Geschwindigkeit eingegeben wurde
	 * (minKm-RadioButton augewählt)
	 * @param geschwindigkeitString: Der aus dem Textfeld ausgelesen String
	 * @return TRUE für gültige Geschwindigkeit | FALSE für ungültige Geschwindigkeit
	 */
	private boolean checkMinKm(String geschwindigkeitString) {
		if (geschwindigkeitString.equals("00:00,00")) {
			lblMinKmError.setText("Bitte geben Sie eine Geschwindigkeit ein!");
			return false;
		} else {
			double geschwindigkeit = lController.parseMinStringToSec(geschwindigkeitString);
			this.geschwindigkeit = geschwindigkeit;
			return true;			
		}
	}
	
	/**
	 * Überprüfen, ob eine Geschwindigkeit zwischen 10 und 30 km/h liegt
	 * @param geschwindigkeit: [s/kmH]
	 */
	private void checkMinMaxGeschwindigkeit (JLabel errorLabel, double geschwindigkeit) {
		if(geschwindigkeit == 0 ) {
			return;
		}
		double kmH = Einheitenumrechner.toKmH(geschwindigkeit);
		if (!(kmH >= 10D)) {
			errorLabel.setText("Geschwindigkeit liegt unter 10km/h");		
		} else if(!(kmH <= 30D) && !(kmH >= 100D)) {
			errorLabel.setText("Geschwindigkeit liegt über 30km/h");
		} else if(kmH >= 100D) {
			errorLabel.setText("Geschwindigkeit liegt über 100 km/h");
		} else {
			errorLabel.setText("");			
		}
	}
	
	/**
	 * Berechnen der Geschwindigkeit aus der Zeit aus Zeitfeld --> textFieldZeit
	 * @return Formatierter Geschwindigkeitsstring mit 2 Nachkommastellen in [km/h]
	 */
	private String berechneGeschwindigkeitenAusStrecke(int geschwindigkeitArt) {
		DecimalFormat f = new DecimalFormat("#0.00");
		double geschwindigkeitFormat = 0D;
		String zeit = "01:00:00,00";
		int strecke = -1;
		String streckenString = textFieldStrecke.getText();
    	if (streckenString!= null) {
    		strecke = -1;
    		try{
    			strecke = Integer.parseInt(streckenString);
    		} catch (Exception h) {
    			strecke = -1;
    		}  
    		if (strecke == -1) {
    			return "";
    		}
    	}
		double geschwindigkeit = lController.berechneGeschwindigkeit(strecke, zeit);
		this.geschwindigkeit = geschwindigkeit;
		switch (geschwindigkeitArt) {
			case 1: 
				geschwindigkeit = Einheitenumrechner.toKmH(geschwindigkeit);
				geschwindigkeitFormat = (Math.round(geschwindigkeit*100D))/100D;
				return f.format(geschwindigkeitFormat);
			case 2:
				geschwindigkeit = Einheitenumrechner.toMS(geschwindigkeit);
				geschwindigkeitFormat = (Math.round(geschwindigkeit*100D))/100D;
				return f.format(geschwindigkeitFormat);
			case 3:
				String minString = lController.parseSecInMinutenstring(this.geschwindigkeit);
				return minString;
		}
		return f.format(geschwindigkeitFormat);
	}
	
	/**
	 * Einsetzen der Geschwindigkeit (aus Zeit berechnet) in GeschwindigkeitsTextfeld --> textFieldGeschwindigkeit
	 */
	private void setzeGeschwindigkeiten() {
		textFieldkmH.setText("");
		textFieldMs.setText("");
		textFieldminKm.setText("");
		textFieldkmH.setText(berechneGeschwindigkeitenAusStrecke(1));
		textFieldMs.setText(berechneGeschwindigkeitenAusStrecke(2));
		textFieldminKm.setText(berechneGeschwindigkeitenAusStrecke(3));
		checkMinMaxGeschwindigkeit(lblKmhError, this.geschwindigkeit);
	}
	
	/**
	 * Füllen des Textfelds für die Strecke mit der, aus der Geschwindigkeit berechneten
	 * Strecke
	 * @param geschwindigkeit: Zugrundeliegende Geschwindigkeit
	 */
	private void setzeStrecke(double geschwindigkeit) {
		int strecke = lController.berechneStreckeAusGeschwindigkeit(geschwindigkeit);
		textFieldStrecke.setText(String.valueOf(strecke));
	}
	
	/**
	 * Berechnen der Geschwindigkeit in km/h und Einsetzen in das 
	 * entsprechende Textfeld
	 * @param geschwindigkeit: [s/km]
	 */
	private void setzeKmH(double geschwindigkeit) {
		DecimalFormat f = new DecimalFormat("#0.00");
		double geschwindigkeitFormat = 0D;
		geschwindigkeit = Einheitenumrechner.toKmH(geschwindigkeit);
		geschwindigkeitFormat = (Math.round(geschwindigkeit*100D))/100D;
		textFieldkmH.setText(f.format(geschwindigkeitFormat));
	}
	
	/**
	 * Berechnen der Geschwindigkeit in m/s und Einsetzen in das 
	 * entsprechende Textfeld
	 * @param geschwindigkeit: [s/km]
	 */
	private void setzeMs(double geschwindigkeit) {
		DecimalFormat f = new DecimalFormat("#0.00");
		double geschwindigkeitFormat = 0D;
		geschwindigkeit = Einheitenumrechner.toMS(geschwindigkeit);
		geschwindigkeitFormat = (Math.round(geschwindigkeit*100D))/100D;
		textFieldMs.setText(f.format(geschwindigkeitFormat));
	}
	
	/**
	 * Berechnen der Geschwindigkeit in min/km,
	 * Formatierung des Ergebnis und Einsetzen in das 
	 * entsprechende Textfeld
	 * @param geschwindigkeit: [s/km]
	 */
	private void setzeMinKm(double geschwindigkeit) {
		String minString = lController.parseSecInMinutenstring(geschwindigkeit);
		textFieldminKm.setText(minString);
	}
	
	/**
	 * Überprüfen, ob alle Werte gesetzt wurden, instanziieren eines Leistungs-Objekt
	 * und hinzufügen dieses Objekts in die Leistungstabelle
	 * @return TRUE, falls alle Eingaben i.o.
	 */
	private boolean actionBestaetigen () {
		boolean ok = true;
		String bezeichnungString = "Direkt eingegebene Schwelle";
	
		Date datum = new Date();
		DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
		String datumString = df.format(datum);
		
		String streckenString = textFieldStrecke.getText();
		String kmhString = textFieldkmH.getText();
		String msString = textFieldMs.getText();
		String minkmString = textFieldminKm.getText();
		if (rdbtnStrecke.isSelected()) {
			if(!checkStrecke(streckenString)) {
				ok = false;
			}					
		} else if (rdbtnkmH.isSelected()) {
			if(!checkKmh(kmhString)) {
				ok = false;
			}
		} else if (rdbtnms.isSelected()) {
			if(!checkMs(msString)) {
				ok = false;
			}
		} else if (rdbtnminkm.isSelected()) {
			if(!checkMinKm(minkmString)) {
				ok = false;
			}
		}
		
		if(ok) {
			long id_athlet = mainFrame.tabList.get(mainFrame.getAktivesTab()).getAthletenId();
			int id_strecke = -1;
			Leistung leistung = lController.neueLeistung(id_strecke,id_athlet,geschwindigkeit,bezeichnungString,datumString);
			mainFrame.tabList.get(mainFrame.getAktivesTab()).addZeile(leistung);			
		}
		return ok;
	}
	
	/**
	 * Löschen aller vorhandenen Warnungen
	 */
	private void clearWarnings() {
		lblStreckeError.setText("");
		lblMsError.setText("");
		lblMinKmError.setText("");
	}
	
//----------------------- PRIVATE METHODEN -----------------------
	/**
	 * Erweiterung zum DocumentListener
	 * Echtzeitverarbeitung von Integerwerten 
	 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta)
	 */
	private class IntegerDocumentListener implements DocumentListener {
	    
	    @Override
	    public void insertUpdate(DocumentEvent e) {
	    	if (textFieldStrecke.getText().length() >= 4) {
	    		setzeGeschwindigkeiten();	    		
	    	}
	    }
	    
	    @Override
	    public void removeUpdate(DocumentEvent e) {
	    	if (textFieldStrecke.getText().length() >= 4) {
	    		setzeGeschwindigkeiten();	    		
	    	}
	    }
	    
	    @Override
	    public void changedUpdate(DocumentEvent e) {
	    	if (textFieldStrecke.getText().length() >= 4) {
	    		setzeGeschwindigkeiten();	    		
	    	}
	    }
	}
}
