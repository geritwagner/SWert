package view;

import java.awt.*;
import java.awt.event.*;
import java.text.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.text.*;

import main.Main;
import helper.*;
import model.Leistung;
import controller.Einheitenumrechner;

/**
 * Dialog zum Anlegen einer neuen Leistung durch
 * direkte Eingabe einer Schwelle
 * @author Honors-WInfo-Projekt (Fabian B�hm, Alexander Puchta)
 */
public class SchwellenDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	private final JPanel contentPanel = new JPanel();
	private final Dimension d = this.getToolkit().getScreenSize(); 
	
	private final LeistungHelper lController = Main.mainFrame.leistungHelper;
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
	
	private double geschwindigkeit; //Enth�lt immer die aktuelle, berechnete Geschwindigkeits
	private final ButtonGroup buttonGroup = new ButtonGroup();
	private JLabel lblKmhError;
	private JLabel lblMsError;
	private JLabel lblMinKmError;

	public SchwellenDialog() {
		initProperties();
		initAllComponents();
		clearWarnings();
	}
	
	public SchwellenDialog(Leistung leistung) {
		initProperties();
		initAllComponents();
		setTitle("Schwelle bearbeiten");
		initWerte(leistung);
		setVisible(true);
		clearWarnings();		
	}
	
	public void initWerte(Leistung leistung) {
		setIconImage(Toolkit.getDefaultToolkit().getImage(LeistungDialog.class.getResource("/bilder/EditLeistung_24x24.png")));
		this.geschwindigkeit = leistung.getGeschwindigkeit();
		setzeStrecke(geschwindigkeit);
		setzeKmH(geschwindigkeit);
		setzeMs(geschwindigkeit);
		setzeMinKm(geschwindigkeit);
		clearWarnings();
	}

	private void initProperties() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(SchwellenDialog.class.getResource("/bilder/NeueLeistung_16x16.png")));
		setResizable(false);
		setTitle("Aerob/Anaerobe Schwelle eingeben");
		setBounds(100, 100, 454, 219);
		setLocation((int) ((d.getWidth() - this.getWidth()) / 2), (int) ((d.getHeight() - this.getHeight()) / 2));
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setModal(true);
	}
	
	private void initAllComponents() {
		initComponents();
		initButtonPane();
	}
	
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
					// TODO: n�chste Zeile l�schen?
					mainFrame.tabList.get(mainFrame.getAktivesTab()).deleteZeileAusDialog();
					SchwellenDialog.this.setVisible(false);
					SchwellenDialog.this.dispose();					
				} else {
					JOptionPane.showMessageDialog(contentPanel,
							"Schwelle wurde nicht erstellt. Bitte �berpr�fen Sie ihre Eingaben.",
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
				if(isValidStrecke(strecke)) {
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
	
	private void initTextFieldKmH() {
		textFieldkmH = new JTextField();
		textFieldkmH.setToolTipText("Geschwindigkeit in [km/h] mit zwei Nachkommastellen");
		textFieldkmH.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				String geschwindigkeitString = textFieldkmH.getText();
				if (isValidKmh(geschwindigkeitString)) {
					setzeStrecke(geschwindigkeit);
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
		textFieldkmH.setBounds(111, 78, 101, 20);
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
	
	private void initTextFieldMs() {
		textFieldMs = new JTextField();
		textFieldMs.setToolTipText("Geschwindigkeit in [m/s] mit zwei Nachkommastellen");
		textFieldMs.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				String geschwindigkeitString = textFieldMs.getText();
				if (isValidMs(geschwindigkeitString)) {
					setzeStrecke(geschwindigkeit);
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
		textFieldMs.setBounds(111, 104, 101, 20);
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
					if (isValidMinKm(geschwindigkeitString)) {
						setzeStrecke(geschwindigkeit);
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
			textFieldminKm.setBounds(111, 130, 101, 20);
		} catch (ParseException e) {
			//TODO
		}
		contentPanel.add(textFieldminKm);
	}
	
	// TODO: isValid-Methoden: redundant?!?!? -> LeistungDialog?
	private boolean isValidStrecke(String strecke) {
		if (strecke.equals("")) {
			lblStreckeError.setText("Bitte geben Sie eine Strecke ein!");
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
				lblKmhError.setText("Bitte geben Sie eine g�ltige Zahl ein!");
				//TODO
				return false;
			}
			geschwindigkeit = Einheitenumrechner.kmHToSKm(geschwindigkeit);
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
				lblMsError.setText("Bitte geben Sie eine g�ltige Zahl ein!");
				//TODO
				return false;
			}
			geschwindigkeit = Einheitenumrechner.MSToSKm(geschwindigkeit);
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
			double geschwindigkeit = lController.parseMinStringToSec(geschwindigkeitString);
			this.geschwindigkeit = geschwindigkeit;
			return true;			
		}
	}
	
	private void isValidMinMaxGeschwindigkeit (JLabel errorLabel, double geschwindigkeit) {
		if(geschwindigkeit == 0 ) {
			return;
		}
		double kmH = Einheitenumrechner.toKmH(geschwindigkeit);
		if (!(kmH >= 10D)) {
			errorLabel.setText("Geschwindigkeit liegt unter 10km/h");		
		} else if(!(kmH <= 30D) && !(kmH >= 100D)) {
			errorLabel.setText("Geschwindigkeit liegt �ber 30km/h");
		} else if(kmH >= 100D) {
			errorLabel.setText("Geschwindigkeit liegt �ber 100 km/h");
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
	
	private void setzeGeschwindigkeiten() {
		textFieldkmH.setText("");
		textFieldMs.setText("");
		textFieldminKm.setText("");
		textFieldkmH.setText(berechneGeschwindigkeitenAusStrecke(1));
		textFieldMs.setText(berechneGeschwindigkeitenAusStrecke(2));
		textFieldminKm.setText(berechneGeschwindigkeitenAusStrecke(3));
		isValidMinMaxGeschwindigkeit(lblKmhError, this.geschwindigkeit);
	}
	
	private void setzeStrecke(double geschwindigkeit) {
		int strecke = lController.berechneSchwellenStreckeAusGeschwindigkeit(geschwindigkeit);
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
	 * �berpr�fen, ob alle Werte gesetzt wurden, instanziieren eines Leistungs-Objekt
	 * und hinzuf�gen dieses Objekts in die Leistungstabelle
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
			if(!isValidStrecke(streckenString)) {
				ok = false;
			}					
		} else if (rdbtnkmH.isSelected()) {
			if(!isValidKmh(kmhString)) {
				ok = false;
			}
		} else if (rdbtnms.isSelected()) {
			if(!isValidMs(msString)) {
				ok = false;
			}
		} else if (rdbtnminkm.isSelected()) {
			if(!isValidMinKm(minkmString)) {
				ok = false;
			}
		}
		
		if(ok) {
			long id_athlet = mainFrame.tabList.get(mainFrame.getAktivesTab()).getAthlet().getId();
			int id_strecke = -1;
			Leistung leistung = new Leistung(id_strecke,id_athlet,bezeichnungString,datumString,geschwindigkeit);
			mainFrame.tabList.get(mainFrame.getAktivesTab()).addZeile(leistung);			
		}
		return ok;
	}
	
	private void clearWarnings() {
		lblStreckeError.setText("");
		lblMsError.setText("");
		lblMinKmError.setText("");
	}
	
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