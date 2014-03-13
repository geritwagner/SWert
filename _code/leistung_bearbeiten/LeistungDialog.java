package leistung_bearbeiten;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.text.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;
import com.toedter.calendar.JDateChooser;
import net.miginfocom.swing.MigLayout;

import globale_helper.*;
import model.*;

/**
 * Dialog zum Anlegen einer neuen Leistung oder
 * Bearbeiten einer vorhandenen Leistung
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta)
 */
public class LeistungDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	private final JPanel contentPanel = new JPanel();
	private final Dimension d = this.getToolkit().getScreenSize();
	
	protected JTextField textFieldBezeichnung;
	private JLabel lblBezeichnungError;
	protected JComboBox<String> comboBoxStrecke;
	protected JDateChooser calendar;
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
	
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JLabel lblKmhError;
	private JLabel lblMsError;
	private JLabel lblMinKmError;
	private JButton btnAnaerobeSchwelleDirekt;
	private JPanel buttonPanel;

	private double geschwindigkeit; //Enthält immer die aktuelle Geschwindigkeit in s/km (ungerundet)

	private Leistung leistung;
	private Athlet athlet;
	
	LeistungDialogController controller;
	
	// todo: funtionalität in den Controller ausgliedern

	public LeistungDialog(Athlet athlet, Leistung leistung) {
		if (leistung != null && leistung.getId_strecke() == -1) {
			new SchwellenDialog(athlet, leistung);
		} else {
		this.athlet = athlet;
		this.leistung = leistung;
		controller = new LeistungDialogController(athlet, leistung, this);
		boolean leistungbearbeiten = (leistung != null);
		initProperties(leistungbearbeiten);
		initComponents();
		if (leistungbearbeiten)
			initWerte(leistung);
		setFocus();
		clearWarnings();
		setVisible(true);
		}
	}
	
	private void bestaetigenClicked(){
		if(controller.leistungÄndern()) {
			release();				
		} else {
			JOptionPane.showMessageDialog(contentPanel,
					"Leistung wurde nicht erstellt. Bitte überprüfen Sie ihre Eingaben.",
				    "Fehler",
				    JOptionPane.ERROR_MESSAGE);
		}
	}
	
	

	protected boolean validateInput(){
		boolean validInput = true;
		String bezeichnungString = textFieldBezeichnung.getText();
		if(!isValidBezeichnung(bezeichnungString)) {
			validInput = false;
		}
		Date datum = calendar.getDate();
		if(!isValidDatum(datum)) {
			validInput = false;
		}

		String zeitString = textFieldZeit.getText();
		String kmhString = textFieldkmH.getText();
		String msString = textFieldMs.getText();
		String minkmString = textFieldminKm.getText();
		if (rdbtnZeit.isSelected()) {
			if(!isValidZeit(zeitString)) {
				validInput = false;
			}
			setzeKmH(this.geschwindigkeit);
			setzeMs(this.geschwindigkeit);
			setzeMinKm(this.geschwindigkeit);
		} else if (rdbtnkmH.isSelected()) {
			if(!isValidKmh(kmhString)) {
				validInput = false;
			}
			setzeZeit(this.geschwindigkeit);
			setzeMs(this.geschwindigkeit);
			setzeMinKm(this.geschwindigkeit);
		} else if (rdbtnms.isSelected()) {
			if(!isValidMs(msString)) {
				validInput = false;
			}
			setzeZeit(this.geschwindigkeit);
			setzeKmH(this.geschwindigkeit);
			setzeMinKm(this.geschwindigkeit);
		} else if (rdbtnminkm.isSelected()) {
			if(!isValidMinKm(minkmString)) {
				validInput = false;
			}
			setzeZeit(this.geschwindigkeit);
			setzeKmH(this.geschwindigkeit);
			setzeMs(this.geschwindigkeit);
		}
		return validInput;
	}
	
	protected double getGeschwindigkeit() {
		return geschwindigkeit;
	}
	
	private boolean isValidBezeichnung(String bezeichnung) {
		if (bezeichnung.equals("")) {
			lblBezeichnungError.setText("Bitte geben Sie eine Bezeichnung ein.");
			return false;
		} else {
			lblBezeichnungError.setText("");
			return true;
		}
	}
	
	private boolean isValidDatum (Date datum) {
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
	
	private boolean isValidZeit (String zeit) {
		if (zeit.equals("00:00:00,00")) {
			lblZeitError.setText("Bitte geben Sie eine Zeit ein.");
			lblZeitError.setForeground(Color.RED);
			return false;
		}
		return true;
	}
	
	private boolean isValidKmh(String geschwindigkeitString) {
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
				return false;
			}
			geschwindigkeit = UnitsHelper.kmHToSKm(geschwindigkeit);
			this.geschwindigkeit = geschwindigkeit;
			isValidMinMaxGeschwindigkeit(lblKmhError, this.geschwindigkeit);
			return true;
		}
	}

	private boolean isValidMs(String geschwindigkeitString) {
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
				return false;
			}
			geschwindigkeit = UnitsHelper.MSToSKm(geschwindigkeit);
			this.geschwindigkeit = geschwindigkeit;
			isValidMinMaxGeschwindigkeit(lblMsError, this.geschwindigkeit);
			return true;
		}
	}

	private boolean isValidMinKm(String geschwindigkeitString) {
		if (geschwindigkeitString.equals("00:00,00")) {
			lblMinKmError.setText("Bitte geben Sie eine Geschwindigkeit ein!");
			return false;
		} else {
			LeistungHelper lHelper = new LeistungHelper();
			double geschwindigkeit = lHelper.parseMinStringToSec(geschwindigkeitString);
			this.geschwindigkeit = geschwindigkeit;
			return true;			
		}
	}
	
	private void isValidMinMaxGeschwindigkeit (JLabel errorLabel, double geschwindigkeit) {
		if(geschwindigkeit == 0 ) {
			return;
		}
		double kmH = UnitsHelper.toKmH(geschwindigkeit);
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
	
	private void setzeGeschwindigkeiten() {
		textFieldkmH.setText("");
		textFieldMs.setText("");
		textFieldminKm.setText("");
		textFieldkmH.setText(berechneGeschwindigkeitenAusZeit(1));
		textFieldMs.setText(berechneGeschwindigkeitenAusZeit(2));
		textFieldminKm.setText(berechneGeschwindigkeitenAusZeit(3));
		isValidMinMaxGeschwindigkeit(lblKmhError, this.geschwindigkeit);
	}
	
	/**
	 * Berechnen der Geschwindigkeit aus der Zeit aus Zeitfeld --> textFieldZeit
	 * @return Formatierter Geschwindigkeitsstring mit 2 Nachkommastellen in [km/h]
	 */
	private String berechneGeschwindigkeitenAusZeit(int geschwindigkeitArt) {
		LeistungHelper lHelper = new LeistungHelper();
		DecimalFormat f = new DecimalFormat("#0.00");
		double geschwindigkeitFormat = 0D;
		String zeit = textFieldZeit.getText();		
		int strecke = Strecken.getStreckenlaengeById(comboBoxStrecke.getSelectedIndex());
		double geschwindigkeit = lHelper.berechneGeschwindigkeit(strecke, zeit);
		this.geschwindigkeit = geschwindigkeit;
		switch (geschwindigkeitArt) {
			case 1: 
				geschwindigkeit = UnitsHelper.toKmH(geschwindigkeit);
				geschwindigkeitFormat = (Math.round(geschwindigkeit*100D))/100D;
				return f.format(geschwindigkeitFormat);
			case 2:
				geschwindigkeit = UnitsHelper.toMS(geschwindigkeit);
				geschwindigkeitFormat = (Math.round(geschwindigkeit*100D))/100D;
				return f.format(geschwindigkeitFormat);
			case 3:
				String minString = lHelper.parseSecInMinutenstring(this.geschwindigkeit);
				return minString;
		}
		return f.format(geschwindigkeitFormat);
	}
	
	/**
	 * @param geschwindigkeit: [s/km]
	 */
	private void setzeKmH(double geschwindigkeit) {
		DecimalFormat f = new DecimalFormat("#0.00");
		double geschwindigkeitFormat = 0D;
		geschwindigkeit = UnitsHelper.toKmH(geschwindigkeit);
		geschwindigkeitFormat = (Math.round(geschwindigkeit*100D))/100D;
		textFieldkmH.setText(f.format(geschwindigkeitFormat));
	}
	
	/**
	 * @param geschwindigkeit: [s/km]
	 */
	private void setzeMs(double geschwindigkeit) {
		DecimalFormat f = new DecimalFormat("#0.00");
		double geschwindigkeitFormat = 0D;
		geschwindigkeit = UnitsHelper.toMS(geschwindigkeit);
		geschwindigkeitFormat = (Math.round(geschwindigkeit*100D))/100D;
		textFieldMs.setText(f.format(geschwindigkeitFormat));
	}
	
	/**
	 * @param geschwindigkeit: [s/km]
	 */
	private void setzeMinKm(double geschwindigkeit) {
		LeistungHelper lHelper = new LeistungHelper();
		String minString = lHelper.parseSecInMinutenstring(geschwindigkeit);
		textFieldminKm.setText(minString);
	}
	
	/**
	 * @param geschwindigkeit: [s/km]
	 */
	private void setzeZeit(double geschwindigkeit) {
		LeistungHelper lHelper = new LeistungHelper();
		int strecke = Strecken.getStreckenlaengeById(comboBoxStrecke.getSelectedIndex());
		double sec = lHelper.berechneZeit(strecke, geschwindigkeit);
		textFieldZeit.setValue(lHelper.parseSecInZeitstring(sec));
	}
	

	
	// ---------------------------------- initialize view -------------------------------------

	private void initWerte(Leistung leistung) {
		textFieldBezeichnung.setText(leistung.getBezeichnung());
		setIconImage(Toolkit.getDefaultToolkit().getImage(LeistungDialog.class.getResource("/bilder/EditLeistung_24x24.png")));
		comboBoxStrecke.setSelectedIndex(leistung.getId_strecke());
		String datum = leistung.getDatum();
		DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
		try {
			calendar.setDate(formatter.parse(datum));
		} catch (ParseException e) {
		}
		textFieldZeit.setText(leistung.getZeitString());
		this.geschwindigkeit = leistung.getGeschwindigkeit();
		setzeZeit(geschwindigkeit);
		setzeKmH(geschwindigkeit);
		setzeMs(geschwindigkeit);
		setzeMinKm(geschwindigkeit);
	}

	private void initProperties(boolean leistungBearbeiten) {
		setIconImage(Toolkit.getDefaultToolkit().getImage(LeistungDialog.class.getResource("/bilder/NeueLeistung_24x24.png")));
		setResizable(false);
		if (leistungBearbeiten){
			setTitle("Leistung bearbeiten");
		} else {
			setTitle("Neue Leistung anlegen");			
		}
		setBounds(100, 100, 589, 330);
		setLocation((int) ((d.getWidth() - this.getWidth()) / 2), (int) ((d.getHeight() - this.getHeight()) / 2));
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setModal(true);
	}
	
	private void initComponents() {
		initComponentsGeneral();
		initComponentsSpecific();
		initButtonPanel();
	}
	
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
	
	private void initButtonPanel() {		
		buttonPanel = new JPanel();
		getContentPane().add(buttonPanel, BorderLayout.SOUTH);
		buttonPanel.setLayout(new MigLayout("", "[194px][194px][194px][194px]", "[25px]"));
		btnAnaerobeSchwelleDirekt = new JButton("Aerob/Anaerobe Schwelle direkt eingeben");
		buttonPanel.add(btnAnaerobeSchwelleDirekt, "cell 0 0,grow");
		btnAnaerobeSchwelleDirekt.setToolTipText("Direktes Eingeben einer Strecke oder Geschwindigkeit als Aerob/Anaerobe Schwelle");
		btnAnaerobeSchwelleDirekt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				release();
				@SuppressWarnings("unused")
				SchwellenDialog dialog = new SchwellenDialog(athlet, null);
			}
		});
		
		JButton okButton = new JButton("Best\u00E4tigen");
		buttonPanel.add(okButton, "cell 2 0,grow");
		okButton.setToolTipText("Pr\u00FCfen der Eingabe und Anlegen der Leistung");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
					bestaetigenClicked();
			}
		});
		
		JButton cancelButton = new JButton("Abbrechen");
		buttonPanel.add(cancelButton, "cell 3 0,grow");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				release();
			}
		});
		cancelButton.setActionCommand("Cancel");
	}
	
	private void initTextFieldBezeichnung() {
		textFieldBezeichnung = new JTextField();
		textFieldBezeichnung.setToolTipText("Bezeichnung der Leistung");
		textFieldBezeichnung.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				isValidBezeichnung(textFieldBezeichnung.getText());
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
	
	private void initComboBoxStrecke() {
		comboBoxStrecke = new JComboBox<String>();
		comboBoxStrecke.setToolTipText("Absolvierte Strecke");
		comboBoxStrecke.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if (rdbtnZeit.isSelected()) {
					if(isValidZeit(textFieldZeit.getText())) {
						setzeGeschwindigkeiten();
					}					
				} else if (rdbtnkmH.isSelected()) {
					if (isValidKmh(textFieldkmH.getText())) {
						setzeZeit(geschwindigkeit);
					}
				} else if (rdbtnms.isSelected()) {
					if (isValidMs(textFieldMs.getText())) {
						setzeZeit(geschwindigkeit);
					}
				} else if (rdbtnminkm.isSelected()) {
					if(isValidMinKm(textFieldminKm.getText())) {
						setzeZeit(geschwindigkeit);
					}
				}
					
			}
		});
		String[] StreckenlaengenStrings = Strecken.getStreckenlaengenStringArray();
		comboBoxStrecke.setModel(new DefaultComboBoxModel<String>(StreckenlaengenStrings));
		comboBoxStrecke.setBounds(98, 67, 128, 20);
		contentPanel.add(comboBoxStrecke);
	}
	
	private void initCalendar() {
		calendar = new JDateChooser();
		calendar.setToolTipText("Datum der Leistung");
		calendar.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent arg0) {
				Date datum = calendar.getDate();
				isValidDatum(datum);
			}
		});
		calendar.setBounds(98, 98, 128, 17);
		calendar.setDateFormatString("dd.MM.yyyy");
		calendar.setDate(new Date());
		calendar.getDateEditor().getUiComponent().addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				Date datum = calendar.getDate();
				isValidDatum(datum);
			}
			@Override
			public void focusGained(FocusEvent e) {
				lblCalendarError.setText("");
			}
		});
		contentPanel.add(calendar);
	}
	
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
					if(isValidZeit(zeit)) {
						setzeGeschwindigkeiten();
					}
				}
				@Override
				public void focusGained(FocusEvent e) {
					lblZeitError.setText("");
				}
			});
		} catch (ParseException e) {
		}
		textFieldZeit.setBounds(125, 148, 101, 20);
		textFieldZeit.setToolTipText("Ben\u00F6tigte Zeit f\u00FCr die Strecke (hh:minmin:secsec,msecmsec)");
		contentPanel.add(textFieldZeit);
		textFieldZeit.setColumns(10);
	}
	
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
	
	private void initTextFieldKmH() {
		textFieldkmH = new JTextField();
		textFieldkmH.setToolTipText("Geschwindigkeit in [km/h] mit zwei Nachkommastellen");
		textFieldkmH.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				String geschwindigkeitString = textFieldkmH.getText();
				if (isValidKmh(geschwindigkeitString)) {
					setzeZeit(geschwindigkeit);
					setzeMs(geschwindigkeit);
					setzeMinKm(geschwindigkeit);
					isValidMinMaxGeschwindigkeit(lblKmhError, geschwindigkeit);
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
	
	private void initTextFieldMs() {
		textFieldMs = new JTextField();
		textFieldMs.setToolTipText("Geschwindigkeit in [m/s] mit zwei Nachkommastellen");
		textFieldMs.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				String geschwindigkeitString = textFieldMs.getText();
				if (isValidMs(geschwindigkeitString)) {
					setzeZeit(geschwindigkeit);
					setzeKmH(geschwindigkeit);
					setzeMinKm(geschwindigkeit);
					isValidMinMaxGeschwindigkeit(lblMsError, geschwindigkeit);
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
					if (isValidMinKm(geschwindigkeitString)) {
						setzeZeit(geschwindigkeit);
						setzeKmH(geschwindigkeit);
						setzeMs(geschwindigkeit);
						isValidMinMaxGeschwindigkeit(lblMinKmError, geschwindigkeit);
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
		}
		contentPanel.add(textFieldminKm);
	}
	
	private void setFocus(){
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent arg0) {
				textFieldBezeichnung.requestFocus();
			}
		});
	}
	
	private void clearWarnings() {
		lblBezeichnungError.setText("");
		lblCalendarError.setText("");
		lblZeitError.setText("");
		lblKmhError.setText("");
		lblMsError.setText("");
		lblMinKmError.setText("");
	}
	
	private void release(){
		// TODO: model.deleteObserver(this);
		controller.release();
		controller = null;
		setVisible(false);
		dispose();
	}
}