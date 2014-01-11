package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import main.Main;

/**
 * Dialog zum Anlegen eines neuen Athletenprofils
 */
public class AthletDialog extends JDialog{

//----------------------- VARIABLEN -----------------------	
	private static final long serialVersionUID = 1L;
	private final Dimension D = this.getToolkit().getScreenSize();
	private final JPanel contentPanel = new JPanel();
	private JTextField textFieldName = new JTextField();

//----------------------- KONSTRUKTOREN -----------------------
	/**
	 * Konstruktor zum Erzeugen des Dialog-Fensters
	 */
	public AthletDialog() {
		initProperties();
		initLayout();
	}
	
//----------------------- ÖFFENTLICHE METHODEN -----------------------
	
//----------------------- PRIVATE METHODEN -----------------------
	/**
	 * Initialisieren der Fenster-Eigenschaften (Position, Name, Container, etc.)
	 */
	private void initProperties() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(AthletDialog.class.getResource("/bilder/NeuerAthlet_24x24.png")));
		setResizable(false);
		setTitle("Neues Athletenprofil anlegen");
		setBounds(100, 100, 312, 146);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		setLocation((int) ((D.getWidth() - this.getWidth()) / 2), (int) ((D.getHeight() - this.getHeight()) / 2));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}
	
	/**
	 * Initialisieren des Fenster-Layouts
	 */
	private void initLayout() {
		initInputPane();
		initButtonPane();
	}
	 /**
	  * Initialisieren der Eingabe
	  */
	private void initInputPane() {
		JLabel lblInfo = new JLabel("Bitte w\u00E4hlen Sie einen Namen f\u00FCr das Profil:");
		lblInfo.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblInfo.setBounds(10, 11, 286, 14);
		contentPanel.add(lblInfo);
		
		JSeparator separator = new JSeparator();
		separator.setBounds(10, 33, 286, 2);
		contentPanel.add(separator);
		
		JLabel lblName = new JLabel("Name:");
		lblName.setBounds(10, 49, 46, 14);
		contentPanel.add(lblName);
		
		textFieldName = new JTextField();
		textFieldName.setToolTipText("Name f\u00FCr das neue Athletenprofil");
		textFieldName.setBounds(66, 46, 230, 20);
		contentPanel.add(textFieldName);
		textFieldName.setColumns(10);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setBounds(10, 83, 286, 2);
		contentPanel.add(separator_1);
	}
	
	/**
	 * Initialisieren der beiden Buttons "Bestätigen" und "Abbrechen"
	 */
	private void initButtonPane() {
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
		
		JButton okButton = new JButton("Best\u00E4tigen");
		okButton.setIcon(null);
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				inputValidieren();
			}
		});
		okButton.setActionCommand("OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
	
		JButton cancelButton = new JButton("Abbrechen");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				AthletDialog.this.setVisible(false);
				AthletDialog.this.dispose();
			}
		});
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);
	}

	/**
	 * Validieren der Eingabe in das Namens-Input-Feld
	 */
	private void inputValidieren() {
		String name = textFieldName.getText();
		if(checkName(name)) {
			AthletDialog.this.setVisible(false);
			AthletDialog.this.dispose();
			Main.mainFrame.createTab(name);
		}
		else {
			JOptionPane.showMessageDialog(AthletDialog.this,
					"Bitte geben Sie einen Namen für den Athleten ein.",
				    "Fehler",
				    JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Überprüfen, ob mindestens 1 Zeichen als Name eingegeben wurde
	 * @param name: zu prüfender String
	 * @return TRUE falls 'name' mindestens 1 Zeichen lang ist | FALSE falls nicht
	 */
	private boolean checkName(String name) {
		if (name.toCharArray().length > 0) {
			return true;
		}
		return false;
	}
}