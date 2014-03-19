package leistung_bearbeiten;

import java.awt.event.*;
import java.text.*;
import java.util.*;
import model.*;

/**
 * @author Honors-WInfo-Projekt (Fabian Böhm, Alexander Puchta), Gerit Wagner
 */

public class SchwellenDialogController implements ActionListener {

	private Athlet athlet;
	private Leistung leistung;
	private SchwellenDialog view;
	
	protected SchwellenDialogController(Athlet athlet, Leistung leistung, SchwellenDialog view){
		this.athlet = athlet;
		this.leistung = leistung;
		this.view = view;
	}

	protected void release(){
		view = null;
		athlet = null;
		leistung = null;
	}
	
	private boolean leistungÄndern(){
		if(true == view.validateInput()) {
			änderungenDurchführen();
			return true;
		} else {
			return false;
		}
	}
	
	private void änderungenDurchführen(){
		long id_athlet = athlet.getId();
		int id_strecke = -1;
		String bezeichnungString = "Direkt eingegebene Schwelle";
		Date datum = new Date();
		DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
		String datumString = df.format(datum);
		if (this.leistung == null){
			Leistung leistung = new Leistung(id_strecke, id_athlet, bezeichnungString, datumString, view.getGeschwindigkeit());
			athlet.addLeistung(leistung);
		} else {
			long leistung_id = leistung.getId();
			athlet.updateLeistung(leistung_id, id_strecke, bezeichnungString, datumString, view.getGeschwindigkeit());				
		}
	}

	public void actionPerformed(ActionEvent arg0) {
		String command = arg0.getActionCommand();
		switch (command){
		case "Bestätigen":
			if(leistungÄndern()){		
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
}