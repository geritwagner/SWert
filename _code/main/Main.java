package main;

import java.awt.EventQueue;

import javax.swing.UIManager;

import view.MainFrame;
import controller.AthletController;
import controller.CSVController;
import controller.DiagrammController;
import controller.FunktionenController;
import controller.LeistungController;
import controller.StreckenController;

/**
 * Main-Klasse zum Ausführen des Programms
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta)
 */
public class Main {
	
//----------------------- VARIABLEN -----------------------
	
	public static AthletController athletController;
	public static LeistungController leistungController;
	public static StreckenController streckenController;
	public static CSVController csvController;
	public static DiagrammController diagrammController;
	public static FunktionenController funktionenController;
	public static MainFrame mainFrame;
	
//----------------------- ÖFFENTLICHE METHODEN -------------
	
	public static void main (String args[]) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					athletController = new AthletController();
					csvController = new CSVController();
					diagrammController = new DiagrammController();
					leistungController = new LeistungController();
					streckenController = new StreckenController();
					funktionenController = new FunktionenController();
					mainFrame = new MainFrame();
					mainFrame.getContext().setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}


