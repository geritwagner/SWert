package helper;

import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import view.ProfilTab;

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
