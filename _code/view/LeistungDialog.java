package view;

import helper.DezimalDocument;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
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
import javax.swing.text.MaskFormatter;

import main.Main;
import model.Leistung;

import com.toedter.calendar.JDateChooser;

import controller.Einheitenumrechner;
import controller.LeistungController;
import controller.StreckenController;
import net.miginfocom.swing.MigLayout;

/**
 * Dialog zum Anlegen einer neuen Leistung oder
 * Bearbeiten einer vorhandenen Leistung
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta)
 */
public class LeistungDialog extends JDialog {

//----------------------- VARIABLEN -----------------------
	private final JDialog self = this;
	private static final long serialVersionUID = 1L;
	
	private final JPanel contentPanel = new JPanel();
	private final Dimension d = this.getToolkit().getScreenSize();
	private MainFrame mainFrame = Main.mainFrame;
	private final LeistungController lController = Main.leistungController;
	private final StreckenController sController = Main.streckenController;
	
	private JTextField textFieldBezeichnung;
	private JLabel lblBezeichnungError;
	private JComboBox<String> comboBoxStrecke;
	private JDateChooser calendar;
	private JLabel lblCalendarError;
	
	private JRadioButton rdbtnZeit;
	private JFormattedTextField textFieldZeit;
	private JLabel lblZeitError;
	private JRadioButton rdbtnkmH;
	private JTextField textFieldkmH;
	private JRadioButton rdbtnms;
	private JTextField textFieldMs;
	private JRadioButton rdbtnminkm;
	private JFormattedTextField textFieldminKm;

	String[] strecken = new String[] {"400m",
									  "800m",
									  "1.000m",
									  "1.500m",
									  "2.000m",
									  "3.000m",
									  "5.000m",
									  "10.000m",
									  "15km",
									  "Halbmarathon",
									  "25km",
									  "Marathon"};
	
	private double geschwindigkeit; //Enthält immer die aktuelle Geschwindigkeit in s/km (ungerundet)
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JLabel lblKmhError;
	private JLabel lblMsError;
	private JLabel lblMinKmError;
	private JButton btnAnaerobeSchwelleDirekt;
	private JPanel buttonPanel;

//----------------------- KONSTRUKTOREN -----------------------
	/**
	 * Konstruktor zum Erzeugen des Dialogs
	 */
	public LeistungDialog() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent arg0) {
				textFieldBezeichnung.requestFocus();
			}
		});
		initProperties();
		initComponents();
		clearWarnings();
	}
	
//----------------------- ÖFFENTLICHE METHODEN -----------------------
	/**
	 * Wird aufgerufen, wenn eine Leistung bearbeitet wird. Schreibt die
	 * Werte der Leistung in die entsprechenden Felder des Dialogs
	 * @param leistung: Leistung-Objekt, welches bearbeitet werden soll
	 */
	public void initWerte(Leistung leistung) {
		textFieldBezeichnung.setText(leistung.getBezeichnung());
		setIconImage(Toolkit.getDefaultToolkit().getImage(LeistungDialog.class.getResource("/bilder/EditLeistung_24x24.png")));
		comboBoxStrecke.setSelectedIndex(leistung.getId_strecke());
		String datum = leistung.getDatum();
		DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
		try {
			calendar.setDate(formatter.parse(datum));
		} catch (ParseException e) {
			//TODO
		}
		textFieldZeit.setText(leistung.getZeit());
		this.geschwindigkeit = leistung.getGeschwindigkeit();
		setzeZeit(geschwindigkeit);
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
		setIconImage(Toolkit.getDefaultToolkit().getImage(LeistungDialog.class.getResource("/bilder/NeueLeistung_24x24.png")));
		setResizable(false);
		setTitle("Neue Leistung anlegen");
		setBounds(100, 100, 589, 330);
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
	private void initComponents() {
		initComponentsGeneral();
		initComponentsSpecific();
		initButtonPanel();
	}
	
	/**
	 * Initialisieren der "oberen" Komponenenten (Abschnitt: "Allgemeine Daten")
	 */
	private void initComponentsGeneral() {
		JLabel lblAllgemeineDaten = new JLabel("Allgemeine Daten");
		lblAllgemeineDaten.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblAllgemeineDaten.setBounds(10, 11, 424, 14);
		contentPanel.add(lblAllgemeineDaten);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(10, 27, 563, 2);
		contentPanel.add(separator);
		
		JLabel lblBezeichnung = new JLabel("Bezeichnung:");
		lblBezeichnung.setBounds(10, 36, 78, 14);
		contentPanel.add(lblBezeichnung);
		
		initTextFieldBezeichnung();
		
		lblBezeichnungError = new JLabel("");
		lblBezeichnungError.setBounds(236, 42, 337, 14);
		contentPanel.add(lblBezeichnungError);
		
		JLabel lblStreckenlnge = new JLabel("Streckenl\u00E4nge:");
		lblStreckenlnge.setBounds(10, 70, 78, 14);
		contentPanel.add(lblStreckenlnge);
		
		initComboBoxStrecke();
		
		JLabel lblDatum = new JLabel("Datum:");
		lblDatum.setBounds(10, 101, 78, 14);
		contentPanel.add(lblDatum);
		
		initCalendar();
		
		lblCalendarError = new JLabel("");
		lblCalendarError.setBounds(236, 101, 337, 14);
		contentPanel.add(lblCalendarError);
	}
	
	/**
	 * Initialisieren der "unteren Komponenten (Abschnitt: "Spezifische Daten")
	 */
	private void initComponentsSpecific() {
		JLabel lblSpezifischeDaten = new JLabel("Spezifische Daten");
		lblSpezifischeDaten.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblSpezifischeDaten.setBounds(10, 126, 424, 14);
		contentPanel.add(lblSpezifischeDaten);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(10, 141, 563, 2);
		contentPanel.add(separator_1);
		
		initRadioButtonZeit();
			
		initTextFieldZeit();
		
		lblZeitError = new JLabel("");
		lblZeitError.setBounds(236, 151, 337, 14);
		contentPanel.add(lblZeitError);
		
		initRadioButtonKmH();
		
		initTextFieldKmH();
		
		lblKmhError = new JLabel("");
		lblKmhError.setBounds(236, 177, 337, 14);
		contentPanel.add(lblKmhError);
		buttonGroup.add(rdbtnms);
		
		initRadioButtonMs();
		rdbtnms.setBounds(10, 199, 109, 23);
		contentPanel.add(rdbtnms);
		
		initTextFieldMs();
		
		lblMsError = new JLabel("");
		lblMsError.setBounds(236, 203, 337, 14);
		contentPanel.add(lblMsError);
		buttonGroup.add(rdbtnminkm);

		initRadioButtonMinKm();
		rdbtnminkm.setBounds(10, 225, 109, 23);
		contentPanel.add(rdbtnminkm);
		
		initTextFieldMinKm();
		
		lblMinKmError = new JLabel("");
		lblMinKmError.setBounds(236, 229, 337, 14);
		contentPanel.add(lblMinKmError);
		
		JSeparator separator_2 = new JSeparator();
		separator_2.setBounds(10, 255, 563, 2);
		contentPanel.add(separator_2);
	}
	
	/**
	 * Initialisieren der beiden Buttons "Bestätigen" und "Abbrechen"
	 */
	private void initButtonPanel() {
		
		buttonPanel = new JPanel();
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		buttonPanel.setLayout(new MigLayout("", "[194px][194px][194px][194px]", "[25px]"));
		btnAnaerobeSchwelleDirekt = new JButton("Aerob/Anaerobe Schwelle direkt eingeben");
		buttonPanel.add(btnAnaerobeSchwelleDirekt, "cell 0 0,grow");
		btnAnaerobeSchwelleDirekt.setToolTipText("Direktes Eingeben einer Strecke oder Geschwindigkeit als Aerob/Anaerobe Schwelle");
		
		JButton okButton = new JButton("Best\u00E4tigen");
		buttonPanel.add(okButton, "cell 2 0,grow");
		okButton.setToolTipText("Pr\u00FCfen der Eingabe und Anlegen der Leistung");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(actionBestaetigen()) {
					// mainFrame.tabList.get(mainFrame.getAktivesTab()).deleteZeileAusDialog();
					LeistungDialog.this.setVisible(false);
					LeistungDialog.this.dispose();					
				} else {
					JOptionPane.showMessageDialog(contentPanel,
							"Leistung wurde nicht erstellt. Bitte überprüfen Sie ihre Eingaben.",
						    "Fehler",
						    JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		//getRootPane().setDefaultButton(okButton);
		
		JButton cancelButton = new JButton("Abbrechen");
		buttonPanel.add(cancelButton, "cell 3 0,grow");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				LeistungDialog.this.setVisible(false);
				LeistungDialog.this.dispose();
			}
		});
		cancelButton.setActionCommand("Cancel");
		btnAnaerobeSchwelleDirekt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				self.setVisible(false);
				self.dispose();
				SchwellenDialog dialog = new SchwellenDialog();
				dialog.setVisible(true);
			}
		});
	}
	
	/**
	 * Initialisieren des Textfelds zur Eingabe der Bezeichnung
	 */
	private void initTextFieldBezeichnung() {
		textFieldBezeichnung = new JTextField();
		textFieldBezeichnung.setToolTipText("Bezeichnung der Leistung");
		textFieldBezeichnung.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				checkBezeichnung(textFieldBezeichnung.getText());
			}
			
			@Override
			public void focusGained(FocusEvent e) {
				lblBezeichnungError.setText("");
			}
		});
		textFieldBezeichnung.setBounds(98, 36, 128, 20);
		contentPanel.add(textFieldBezeichnung);
		textFieldBezeichnung.setColumns(10);
	}
	
	/**
	 * Initialisieren der ComboBox zur Auswahl einer Streckenlänge
	 */
	private void initComboBoxStrecke() {
		comboBoxStrecke = new JComboBox<String>();
		comboBoxStrecke.setToolTipText("Absolvierte Strecke");
		comboBoxStrecke.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if (rdbtnZeit.isSelected()) {
					if(checkZeit(textFieldZeit.getText())) {
						setzeGeschwindigkeiten();
					}					
				} else if (rdbtnkmH.isSelected()) {
					if (checkKmh(textFieldkmH.getText())) {
						setzeZeit(geschwindigkeit);
					}
				} else if (rdbtnms.isSelected()) {
					if (checkMs(textFieldMs.getText())) {
						setzeZeit(geschwindigkeit);
					}
				} else if (rdbtnminkm.isSelected()) {
					if(checkMinKm(textFieldminKm.getText())) {
						setzeZeit(geschwindigkeit);
					}
				}
					
			}
		});
		comboBoxStrecke.setModel(new DefaultComboBoxModel<String>(strecken));
		comboBoxStrecke.setBounds(98, 67, 128, 20);
		contentPanel.add(comboBoxStrecke);
	}
	
	/**
	 * Initialisieren des Kalender zur Datumsauswahl
	 */
	private void initCalendar() {
		calendar = new JDateChooser();
		calendar.setToolTipText("Datum der Leistung");
		calendar.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent arg0) {
				Date datum = calendar.getDate();
				checkDatum(datum);
			}
		});
		calendar.setBounds(98, 98, 128, 17);
		calendar.setDateFormatString("dd.MM.yyyy");
		calendar.setDate(new Date());
		calendar.getDateEditor().getUiComponent().addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				Date datum = calendar.getDate();
				checkDatum(datum);
			}
			@Override
			public void focusGained(FocusEvent e) {
				lblCalendarError.setText("");
			}
		});
		contentPanel.add(calendar);
	}
	
	/**
	 * Initialisieren der Funktionalität des "Zeit"-RadioButtons
	 */
	private void initRadioButtonZeit() {
		rdbtnZeit = new JRadioButton("Zeit:");
		rdbtnZeit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (rdbtnZeit.isSelected()) {
					textFieldZeit.setText("");
					textFieldZeit.setEnabled(true);;
					textFieldkmH.setText("");
					textFieldkmH.setEnabled(false);
					textFieldMs.setText("");
					textFieldMs.setEnabled(false);
					textFieldminKm.setText("");
					textFieldminKm.setEnabled(false);
					lblZeitError.setText("");
					lblKmhError.setText("");
					lblMsError.setText("");
					lblMinKmError.setText("");
				}
			}
		});
		buttonGroup.add(rdbtnZeit);
		rdbtnZeit.setBounds(10, 147, 109, 23);
		rdbtnZeit.setSelected(true);
		contentPanel.add(rdbtnZeit);
	}
	
	/**
	 * Initialisieren des Textfelds zur Eingabe der Zeit
	 */
	private void initTextFieldZeit() {
		try {
			MaskFormatter zeitMaske = new MaskFormatter("##:##:##,##");
			zeitMaske.setPlaceholderCharacter('0');
			zeitMaske.setValidCharacters("1234567890");
			textFieldZeit = new JFormattedTextField(zeitMaske);
			textFieldZeit.addFocusListener(new FocusAdapter() {
				@Override
				public void focusLost(FocusEvent e) {
					String zeit = textFieldZeit.getText();
					if(checkZeit(zeit)) {
						setzeGeschwindigkeiten();
					}
				}
				
				@Override
				public void focusGained(FocusEvent e) {
					lblZeitError.setText("");
				}
			});
		} catch (ParseException e) {
			//TODO
		}
		textFieldZeit.setBounds(125, 148, 101, 20);
		textFieldZeit.setToolTipText("Ben\u00F6tigte Zeit f\u00FCr die Strecke (hh:minmin:secsec,msecmsec)");
		contentPanel.add(textFieldZeit);
		textFieldZeit.setColumns(10);
	}
	
	/**
	 * Initialisieren der Funktionalität des "km/h"-RadioButtons
	 */
	private void initRadioButtonKmH() {
		rdbtnkmH = new JRadioButton("[km/h]:");
		rdbtnkmH.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (rdbtnkmH.isSelected()) {
					textFieldZeit.setText("");
					textFieldZeit.setEnabled(false);;
					textFieldkmH.setText("");
					textFieldkmH.setEnabled(true);
					textFieldMs.setText("");
					textFieldMs.setEnabled(false);
					textFieldminKm.setText("");
					textFieldminKm.setEnabled(false);
					lblZeitError.setText("");
					lblKmhError.setText("");
					lblMsError.setText("");
					lblMinKmError.setText("");
				}
			}
		});
		buttonGroup.add(rdbtnkmH);
		rdbtnkmH.setBounds(10, 173, 109, 23);
		contentPanel.add(rdbtnkmH);
	}
	
	/**
	 * Initialisieren des Textfelds zur Eingabe der Geschwindigkeit in kmh
	 */
	private void initTextFieldKmH() {
		textFieldkmH = new JTextField();
		textFieldkmH.setToolTipText("Geschwindigkeit in [km/h] mit zwei Nachkommastellen");
		textFieldkmH.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				String geschwindigkeitString = textFieldkmH.getText();
				if (checkKmh(geschwindigkeitString)) {
					setzeZeit(geschwindigkeit);
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
		textFieldkmH.setBounds(125, 174, 101, 20);
		textFieldkmH.setDocument(new DezimalDocument());
		contentPanel.add(textFieldkmH);
		textFieldkmH.setColumns(10);
	}
	
	/**
	 * Initialisieren der Funktionalität des "MS"-RadioButtons
	 */
	private void initRadioButtonMs() {
		rdbtnms = new JRadioButton("[m/s]:");
		rdbtnms.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (rdbtnms.isSelected()) {
					textFieldZeit.setText("");
					textFieldZeit.setEnabled(false);;
					textFieldkmH.setText("");
					textFieldkmH.setEnabled(false);
					textFieldMs.setText("");
					textFieldMs.setEnabled(true);
					textFieldminKm.setText("");
					textFieldminKm.setEnabled(false);
					lblZeitError.setText("");
					lblKmhError.setText("");
					lblMsError.setText("");
					lblMinKmError.setText("");
				}
			}
		});
	}
	
	/**
	 * Initialisieren des Textfelds zur Eingabe der Geschwindigkeit in MS
	 */
	private void initTextFieldMs() {
		textFieldMs = new JTextField();
		textFieldMs.setToolTipText("Geschwindigkeit in [m/s] mit zwei Nachkommastellen");
		textFieldMs.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				String geschwindigkeitString = textFieldMs.getText();
				if (checkMs(geschwindigkeitString)) {
					setzeZeit(geschwindigkeit);
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
		textFieldMs.setBounds(125, 200, 101, 20);
		textFieldMs.setDocument(new DezimalDocument());
		contentPanel.add(textFieldMs);
		textFieldMs.setColumns(10);
	}
	
	/**
	 * Initialisieren der Funktionalität des "MinKm"-RadioButtons
	 */
	private void initRadioButtonMinKm() {
		rdbtnminkm = new JRadioButton("[1.000m Zeit]:");
		rdbtnminkm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (rdbtnminkm.isSelected()) {
					textFieldZeit.setText("");
					textFieldZeit.setEnabled(false);;
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
	 * Initialisieren des Textfelds zur Eingabe der Geschwindigkeit in MinKm
	 */
	private void initTextFieldMinKm() {
		try {
			MaskFormatter minKmMaske = new MaskFormatter("##:##,##");
			minKmMaske.setPlaceholderCharacter('0');
			minKmMaske.setValidCharacters("1234567890");
			textFieldminKm = new JFormattedTextField(minKmMaske);
			textFieldminKm.setToolTipText("Geschwindigkeit in [min/km] in der Form \"minmin:secsec,msecmsec\"");
			textFieldminKm.addFocusListener(new FocusAdapter() {
				@Override
				public void focusLost(FocusEvent e) {
					String geschwindigkeitString = textFieldminKm.getText();
					if (checkMinKm(geschwindigkeitString)) {
						setzeZeit(geschwindigkeit);
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
			textFieldminKm.setBounds(125, 226, 101, 20);
		} catch (ParseException e) {
			//TODO
		}
		contentPanel.add(textFieldminKm);
	}
	
	/**
	 * Überprüfen, ob eine Bezeichnung eingegeben wurde
	 * @param bezeichnung: String aus dem Textfeld für die "Bezeichnung" --> textFieldBezeichnung
	 * @return FALSE falls keine Bezeichnung eingegenen wurde | TRUE falls eine Bezeichnung eingegeben wurde
	 */
	private boolean checkBezeichnung(String bezeichnung) {
		if (bezeichnung.equals("")) {
			lblBezeichnungError.setText("Bitte geben Sie eine Bezeichnung ein.");
			return false;
		} else {
			lblBezeichnungError.setText("");
			return true;
		}
	}
	
	/**
	 * Überprüfen, ob ein gültiges Datum eingegeben wurde (Gültigkeit des Datums + Datum darf nicht in Zukunft liegen
	 * @param datum: java.util.Date-Objekt, welches aus dem Kalender (--> calendar) ausgelesen wird
	 * @return FALSE für ein ungültiges Datum | TRUE für ein gültiges Datum
	 */
	private boolean checkDatum (Date datum) {
		if (datum != null) {
			 Date heute = new Date();
			 long diff = (heute.getTime()-datum.getTime())/1000/60/60;
			 if (!(diff < 0)) {
				 return true;				
			 } else {
				 lblCalendarError.setText("Datum darf nicht in der Zukunft liegen.");
				 return false;
			 }
		} else {
			lblCalendarError.setText("Bitte geben Sie ein gültiges Datum ein.");
			return false;
		}
	}
	
	/**
	 * Überprüfen, ob eine gültige Zeit für die Strecke eingegeben wurde
	 * @param zeit: String-Darstellung der Zeit für eine Leistung (--> textFieldZeit)
	 * @return TRUE für gültige Zeit | FALSE für ungültige Zeit
	 */
	private boolean checkZeit (String zeit) {
		if (zeit.equals("00:00:00,00")) {
			lblZeitError.setText("Bitte geben Sie eine Zeit ein.");
			lblZeitError.setForeground(Color.RED);
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
	 * Einsetzen der Geschwindigkeit (aus Zeit berechnet) in GeschwindigkeitsTextfeld --> textFieldGeschwindigkeit
	 */
	private void setzeGeschwindigkeiten() {
		textFieldkmH.setText("");
		textFieldMs.setText("");
		textFieldminKm.setText("");
		textFieldkmH.setText(berechneGeschwindigkeitenAusZeit(1));
		textFieldMs.setText(berechneGeschwindigkeitenAusZeit(2));
		textFieldminKm.setText(berechneGeschwindigkeitenAusZeit(3));
		checkMinMaxGeschwindigkeit(lblKmhError, this.geschwindigkeit);
	}
	
	/**
	 * Berechnen der Geschwindigkeit aus der Zeit aus Zeitfeld --> textFieldZeit
	 * @return Formatierter Geschwindigkeitsstring mit 2 Nachkommastellen in [km/h]
	 */
	private String berechneGeschwindigkeitenAusZeit(int geschwindigkeitArt) {
		DecimalFormat f = new DecimalFormat("#0.00");
		double geschwindigkeitFormat = 0D;
		String zeit = textFieldZeit.getText();		
		int strecke = sController.getStreckenlaengeById(comboBoxStrecke.getSelectedIndex());
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
	 * Berechnen der benötigten Zeit aus gegebener Geschwindigkeit und Strecke
	 * @param geschwindigkeit: [s/km]
	 */
	private void setzeZeit(double geschwindigkeit) {
		int strecke = sController.getStreckenlaengeById(comboBoxStrecke.getSelectedIndex());
		double sec = lController.berechneZeit(strecke, geschwindigkeit);
		textFieldZeit.setValue(lController.parseSecInZeitstring(sec));
	}
	
	/**
	 * Überprüfen, ob alle Werte gesetzt wurden, instanziieren eines Leistungs-Objekt
	 * und hinzufügen dieses Objekts in die Leistungstabelle
	 * @return TRUE, falls alle Eingaben i.o.
	 */
	private boolean actionBestaetigen () {
		boolean ok = true;
		String bezeichnungString = textFieldBezeichnung.getText();
		if(!checkBezeichnung(bezeichnungString)) {
			ok = false;
		}
		Date datum = calendar.getDate();
		DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
		String datumString = df.format(datum);
		if(!checkDatum(datum)) {
			ok = false;
		}

		String zeitString = textFieldZeit.getText();
		String kmhString = textFieldkmH.getText();
		String msString = textFieldMs.getText();
		String minkmString = textFieldminKm.getText();
		if (rdbtnZeit.isSelected()) {
			if(!checkZeit(zeitString)) {
				ok = false;
			}
			setzeKmH(this.geschwindigkeit);
			setzeMs(this.geschwindigkeit);
			setzeMinKm(this.geschwindigkeit);
		} else if (rdbtnkmH.isSelected()) {
			if(!checkKmh(kmhString)) {
				ok = false;
			}
			setzeZeit(this.geschwindigkeit);
			setzeMs(this.geschwindigkeit);
			setzeMinKm(this.geschwindigkeit);
		} else if (rdbtnms.isSelected()) {
			if(!checkMs(msString)) {
				ok = false;
			}
			setzeZeit(this.geschwindigkeit);
			setzeKmH(this.geschwindigkeit);
			setzeMinKm(this.geschwindigkeit);
		} else if (rdbtnminkm.isSelected()) {
			if(!checkMinKm(minkmString)) {
				ok = false;
			}
			setzeZeit(this.geschwindigkeit);
			setzeKmH(this.geschwindigkeit);
			setzeMs(this.geschwindigkeit);
		}
		
		if(ok) {
			long id_athlet = mainFrame.tabList.get(mainFrame.getAktivesTab()).getAthlet().getId();
			int id_strecke = comboBoxStrecke.getSelectedIndex();
			Leistung leistung = lController.neueLeistung(id_strecke,id_athlet,geschwindigkeit,bezeichnungString,datumString);
			mainFrame.tabList.get(mainFrame.getAktivesTab()).addZeile(leistung);			
		}
		return ok;
	}
	
	/**
	 * Leeren aller Warnungs-Labels
	 */
	private void clearWarnings() {
		lblBezeichnungError.setText("");
		lblCalendarError.setText("");
		lblZeitError.setText("");
		lblKmhError.setText("");
		lblMsError.setText("");
		lblMinKmError.setText("");
	}
}
