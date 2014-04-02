package analyse_bestzeiten;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import globale_helper.*;
import model.*;

/**
 * @author Honors-WInfo-Projekt (Fabian B�hm, Alexander Puchta), Gerit Wagner
 */
public class BestzeitenDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	private JPanel contentPanel = new JPanel();
	private Dimension d = getToolkit().getScreenSize();
	protected JTable trainingsTabelle;
	protected JTextField txtFieldZeit;
	protected JTextField txtFieldStrecke;
	
	private BestzeitenDialogController controller;
	private Athlet athlet;
	
	public BestzeitenDialog(Athlet inputAthlet) {
		this.athlet = inputAthlet;
		// athlet.addObserver(this) - (noch) nicht notwendig
		controller = new BestzeitenDialogController(athlet, this);
		initProperties();
		Object[][] bestzeiten = controller.generateBestzeitenTableFormat();
		initComponents(bestzeiten);
		setModal(true);
		setVisible(true);
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
		txtFieldStrecke.getDocument().addDocumentListener(controller);
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
		cancelButton.addActionListener(controller);
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
		trainingsTabelle.setModel(new DefaultTableModel(
				bestzeiten,
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
	
	protected void release(){
		// ggf. athlet.deleteObserver(this);
		athlet = null;
		controller.release();
		controller = null;
		setVisible(false);
		dispose();
	}
}	