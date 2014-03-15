package leistung_bearbeiten;

import java.text.*;
import java.util.*;

import model.*;

public class LeistungDialogController {

	Athlet athlet;
	Leistung leistung;
	LeistungDialog view;
	
	protected LeistungDialogController(Athlet athlet, Leistung leistung, LeistungDialog view){
		this.athlet = athlet;
		this.leistung = leistung;
		this.view = view;
	}

	protected void release(){
		view = null;
		athlet = null;
	}

	protected boolean leistungÄndern () {
		if(true == view.validateInput()) {
			änderungenDurchführen();
			return true;
		} else {
			return false;
		}
	}

	private void änderungenDurchführen(){
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
		// TODO: 
		// setSpeicherStatus(false);
	}
	
}