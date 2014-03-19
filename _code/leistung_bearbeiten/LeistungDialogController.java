package leistung_bearbeiten;

import java.awt.event.*;
import java.text.*;
import java.util.*;
import model.*;

/**
 * @author Honors-WInfo-Projekt (Fabian B�hm, Alexander Puchta), Gerit Wagner
 */

public class LeistungDialogController implements ActionListener, WindowListener {

	private Athlet athlet;
	private Leistung leistung;
	private LeistungDialog view;
	
	protected LeistungDialogController(Athlet athlet, Leistung leistung, LeistungDialog view){
		this.athlet = athlet;
		this.leistung = leistung;
		this.view = view;
	}

	protected void release(){
		view = null;
		athlet = null;
		leistung = null;
	}

	protected boolean leistung�ndern () {
		if(true == view.validateInput()) {
			�nderungenDurchf�hren();
			return true;
		} else {
			return false;
		}
	}

	private void �nderungenDurchf�hren(){
		long id_athlet = athlet.getId();
		int id_strecke = view.comboBoxStrecke.getSelectedIndex();
		String bezeichnungString = view.textFieldBezeichnung.getText();
		DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
		Date datum = view.calendar.getDate();
		String datumString = df.format(datum);
		if (this.leistung == null){
			double geschwindigkeit = view.getGeschwindigkeit();
			Leistung leistung = new Leistung(id_strecke, id_athlet, bezeichnungString, datumString, geschwindigkeit);
			athlet.addLeistung(leistung);
		} else {
			long leistung_id = leistung.getId();
			athlet.updateLeistung(leistung_id, id_strecke, bezeichnungString, datumString, view.getGeschwindigkeit());				
		}
	}

	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		System.out.println(command);
		switch (command){
		case "Aerob/Anaerobe Schwelle direkt eingeben": 		
			view.release();
			new SchwellenDialog(athlet, null);
			break;
		case "Best�tigen":
			if(leistung�ndern()){		
				view.release();
			} else {
				view.showErrorNichtErstellt();
			}
			break;
		case "Cancel":
			view.release();
			break;
		}
		
	}

	public void windowActivated(WindowEvent arg0) {
		// Auto-generated method stub		
	}

	public void windowClosed(WindowEvent arg0) {
		// Auto-generated method stub		
	}

	public void windowClosing(WindowEvent arg0) {
		// Auto-generated method stub		
	}

	public void windowDeactivated(WindowEvent arg0) {
		// Auto-generated method stub		
	}

	public void windowDeiconified(WindowEvent arg0) {
		// Auto-generated method stub
	}

	public void windowIconified(WindowEvent arg0) {
		// Auto-generated method stub
	}

	public void windowOpened(WindowEvent arg0) {
		view.textFieldBezeichnung.requestFocus();
	}
}