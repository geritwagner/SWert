package main;

import java.awt.EventQueue;
import javax.swing.UIManager;
import view.MainFrame;

/**
 * Main-Klasse zum Ausführen des Programms
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta)
 */
public class Main {
	
	public static MainFrame mainFrame;	
	
	public static void main (String args[]) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					mainFrame = new MainFrame();
					mainFrame.getContext().setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});		
	}
}