package main;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class NeuerAthletDialog extends JDialog{

	private static final long serialVersionUID = 1L;
	private final Dimension D = this.getToolkit().getScreenSize();
	private final JPanel contentPanel = new JPanel();
	private JTextField textFieldName = new JTextField();

	public NeuerAthletDialog() {
		initProperties();
		initLayout();
		setVisible(true);
	}

	private void initProperties() {
		// Position, Name, Container, etc.
		setIconImage(Toolkit.getDefaultToolkit().getImage(NeuerAthletDialog.class.getResource("/bilder/NeuerAthlet_24x24.png")));
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
	
	private void initLayout() {
		initInputPane();
		initButtonPane();
	}

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
				setVisible(false);
				dispose();
			}
		});
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);
	}

	private void inputValidieren() {
		String name = textFieldName.getText();
		if(isValid(name)) {
			setVisible(false);
			dispose();
			// TODO: anders umsetzen:
			Main.mainFrame.createTab(name, null);
		}
		else {
			JOptionPane.showMessageDialog(NeuerAthletDialog.this,
					"Bitte geben Sie einen Namen für den Athleten ein.",
				    "Fehler",
				    JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private boolean isValid(String name) {
		if (name.toCharArray().length > 0) {
			return true;
		}
		return false;
	}
}