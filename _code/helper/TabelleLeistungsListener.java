package helper;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import main.Main;
import view.MainFrame;
import view.ProfilTab;

public class TabelleLeistungsListener implements ListSelectionListener{

	@SuppressWarnings("unused")
	private JTable tabelle;
	private ProfilTab profilTab;
	private MainFrame mainFrame = Main.mainFrame;
	
	public TabelleLeistungsListener(JTable tabelle, ProfilTab profilTab) {
		this.tabelle = tabelle;
		this.profilTab = profilTab;
	}
	
    @Override
	public void valueChanged(ListSelectionEvent e) {	    	
    	if (e.getValueIsAdjusting()) {
    		return;
        }

        mainFrame.leistungBearbeitenMenüVerfügbar();
        mainFrame.leistungLöschenMenüVerfügbar();
        profilTab.leistungsButtonsVerfügbar();
    }
}
