package analyse_bestzeiten;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import javax.swing.table.*;



import globale_helper.*;
import model.*;

/**
 * Dialog zum Anzeigen der möglichen Bestzeiten des Athleten über eine wählbare Strecke
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta)
 */
public class BestzeitenDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel contentPanel = new JPanel();
	private Dimension d = this.getToolkit().getScreenSize();
	
	private JTable trainingsTabelle;
	private JTextField txtFieldZeit;
	private JTextField txtFieldStrecke;
	
	BestzeitenDialogController controller;
	
	public BestzeitenDialog(Athlet inputAthlet) {	
		if ("set" == inputAthlet.getSlopeFaktorStatus()){
			controller = new BestzeitenDialogController(inputAthlet, this);
			initProperties();
			Object[][] bestzeiten = controller.berechneBestzeiten();
			initComponents(bestzeiten);
			setModal(true);
			setVisible(true);		
		}
	}	

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
	
	private void initComponents(Object[][] bestzeiten) {
		initComponentsGeneral();		
		initJTable(bestzeiten);
		initButtonPane();	
	}
	
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
	
	private void initButtonPane() {
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		
		JButton cancelButton = new JButton("Schlie\u00DFen");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				release();
			}
		});
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);
	}
	
	private void initJTable(Object[][] bestzeiten) {	
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportBorder(null);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setBounds(10, 36, 234, 226);
		contentPanel.add(scrollPane);
		
		trainingsTabelle = new JTable();
		//TODO
		trainingsTabelle.setModel(new DefaultTableModel(
				bestzeiten,
				new String[] {
					"Streckenlänge", "Bestzeit"
				}
				));
		trainingsTabelle.getColumnModel().getColumn(0).setPreferredWidth(85);
		trainingsTabelle.getColumnModel().getColumn(0).setMinWidth(95);
		trainingsTabelle.getColumnModel().getColumn(1).setPreferredWidth(60);
		trainingsTabelle.getTableHeader().setReorderingAllowed(false);
		trainingsTabelle.setEnabled(false);
		scrollPane.setViewportView(trainingsTabelle);
	}
	
	private void release(){
		// TODO: model.deleteObserver(this);
		controller.release();
		controller = null;
		setVisible(false);
		dispose();
	}

	/**
	 * Erweiterung zum DocumentListener
	 * Echtzeitverarbeitung von Integerwerten 
	 */
	private class IntegerDocumentListener implements DocumentListener {    
		@Override
		public void insertUpdate(DocumentEvent e) {
			String berechneteBestzeit =  controller.berechneBestzeit(txtFieldStrecke.getText());
			txtFieldZeit.setText(berechneteBestzeit);
		}
		
		@Override
		public void removeUpdate(DocumentEvent e) {
			String berechneteBestzeit =  controller.berechneBestzeit(txtFieldStrecke.getText());
			txtFieldZeit.setText(berechneteBestzeit);
		}
		
		@Override
		public void changedUpdate(DocumentEvent e) {
			String berechneteBestzeit =  controller.berechneBestzeit(txtFieldStrecke.getText());
			txtFieldZeit.setText(berechneteBestzeit);
		}
	}
}