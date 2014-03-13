package globale_helper;

import javax.swing.JTable;
import javax.swing.event.*;

import main.ProfilTab;

public class TabelleLeistungsListener implements ListSelectionListener{

	@SuppressWarnings("unused")
	private JTable tabelle;
	private ProfilTab profilTab;
	
	public TabelleLeistungsListener(JTable tabelle, ProfilTab profilTab) {
		this.tabelle = tabelle;
		this.profilTab = profilTab;
	}
	
    @Override
	public void valueChanged(ListSelectionEvent e) {	    	
    	if (e.getValueIsAdjusting()) {
    		return;
        }
        profilTab.setBearbeitenStatus(true);
    }
}