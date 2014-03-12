package view;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;

import controller.TrainingsbereichController;
import model.*;
import helper.*;

/**
 * Dialog zum Anzeigen einer Tabelle der Trainingsbereiche des Athleten
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta)
 */
public class TrainingsbereichDialog extends JDialog {
	
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private final Dimension d = this.getToolkit().getScreenSize();
		
	private JTextField txtAnaerobeSchwelle;
	private JTextField txtAnaerobeProfilierteSchwelle;
	public JTable trainingsTabelle;
	private JSlider slider;
	private JLabel lblRundenanzahl;
	private JScrollPane scrollPane;
	private final ButtonGroup buttonGroup = new ButtonGroup();
		
	TrainingsbereichController controller;
	Athlet athlet;
	
	public TrainingsbereichDialog(Athlet athlet) {
		try {
			this.athlet = athlet;
			controller = new TrainingsbereichController(athlet, this);
			initProperties();
			double anaerobeSchwelle = athlet.getAnaerobeSchwelle();
			double anaerobeProfilierteSchwelle = anaerobeSchwelle*TrainingsbereichController.getWinzererAufschlag();
			initComponents(anaerobeSchwelle, anaerobeProfilierteSchwelle);
			setModal(true);
			setVisible(true);
		} catch (Exception e) {
			abbrechen();
		}
	}
	
	private void abbrechen(){
		JOptionPane.showMessageDialog(this, "Für die Berechnung der Trainingsbereiche müssen zwei Leistungen für die Berechnung" +
				"des Slope-Faktors ausgewählt werden.", "Leistungen auswählen",JOptionPane.ERROR_MESSAGE);
		setVisible(false);
		dispose();
	}
	
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
	
	private void initComponents(double anaerobeSchwelle, double anaerobeProfilierteSchwelle) {
		initComponentsGeneral();
		initComponentsSpecific(anaerobeSchwelle, anaerobeProfilierteSchwelle);
		initJTable();
		initButtonPane();
	}
	
	private void initComponentsGeneral() {
		JLabel lblAnaerobeSchwelle = new JLabel("Aerob/Anaerobe Schwelle");
		lblAnaerobeSchwelle.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblAnaerobeSchwelle.setBounds(10, 11, 258, 14);
		contentPanel.add(lblAnaerobeSchwelle);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(10, 27, 324, 2);
		contentPanel.add(separator);		
	}

	private void initComponentsSpecific(double anaerobeSchwelle, double anaerobeProfilierteSchwelle) {
		JLabel lblTrainingsbereiche = new JLabel("Trainingsbereiche");
		lblTrainingsbereiche.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblTrainingsbereiche.setBounds(10, 135, 258, 14);
		contentPanel.add(lblTrainingsbereiche);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(10, 150, 324, 2);
		contentPanel.add(separator_1);		
		
		LeistungHelper l = new LeistungHelper();
		
		txtAnaerobeSchwelle = new JTextField();
		txtAnaerobeSchwelle.setText(l.parseSecInMinutenstring(anaerobeSchwelle));
		txtAnaerobeSchwelle.setEditable(false);
		txtAnaerobeSchwelle.setColumns(10);
		txtAnaerobeSchwelle.setBounds(20, 36, 86, 20);
		contentPanel.add(txtAnaerobeSchwelle);
		
		txtAnaerobeProfilierteSchwelle = new JTextField();
		txtAnaerobeProfilierteSchwelle.setText(l.parseSecInMinutenstring(anaerobeProfilierteSchwelle));
		txtAnaerobeProfilierteSchwelle.setEditable(false);
		txtAnaerobeProfilierteSchwelle.setColumns(10);
		txtAnaerobeProfilierteSchwelle.setBounds(20, 60, 86, 20);
		contentPanel.add(txtAnaerobeProfilierteSchwelle);
		
		slider = new JSlider();
		slider.setPaintLabels(true);
		slider.setPaintTicks(true);
		slider.setSnapToTicks(true);
		slider.setMinimum(1);
		slider.setValue(1);
		slider.setMaximum(6);
		slider.setBounds(112, 95, 115, 39);
	    slider.setLabelTable(slider.createStandardLabels(1));	
	    slider.setVisible(false);	    
	    
	    slider.addChangeListener(new ChangeListener() {
	        @Override
			public void stateChanged(ChangeEvent evt) {
	          JSlider slider = (JSlider) evt.getSource();
	          if (!slider.getValueIsAdjusting()) {
	        	int rundenZahl = slider.getValue();
	            controller.updateRundenzeit(rundenZahl);	            
	          }
	        }
	      });
	      
		contentPanel.add(slider);
		
		scrollPane = new JScrollPane();
		scrollPane.setViewportBorder(null);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(10, 163, 324, 206);
		contentPanel.add(scrollPane);
			
		lblRundenanzahl = new JLabel("Rundenanzahl");
		lblRundenanzahl.setBounds(20, 95, 86, 14);
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
		rdbtnProfilierteStrecke.setBounds(112, 60, 156, 23);
		contentPanel.add(rdbtnProfilierteStrecke);
		rdbtnProfilierteStrecke.addActionListener(new ActionListener(){
		    @Override
			public void actionPerformed(ActionEvent e) {
		      initJTableProfiliert();
		    }
		});
	}
	
	private void initButtonPane() {
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		
		JButton cancelButton = new JButton("Schlie\u00DFen");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				dispose();
			}
		});
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);
	}
	
	private void initJTable() {	
		trainingsTabelle = new JTable();
		trainingsTabelle.setModel(new DefaultTableModel(
			controller.berechneTrainingsBereiche(),
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

	private void initJTableProfiliert() {	
		trainingsTabelle = new JTable();
		int rundenZahl = slider.getValue();
		trainingsTabelle.setModel(new DefaultTableModel(
			controller.berechneTrainingsBereicheProfiliert(rundenZahl),
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

		slider.setVisible(true);
	    lblRundenanzahl.setVisible(true);
	}
}