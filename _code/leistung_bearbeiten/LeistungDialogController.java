package leistung_bearbeiten;

import java.text.DateFormat;
import java.util.Date;

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
			Leistung leistung = new Leistung(id_strecke, id_athlet, bezeichnungString, datumString, view.getGeschwindigkeit());
			// TODO: hier sollte die �nderung des views (�ber observer) und des models (athlet.set..., bis jetzt in der Methode addZeile enthalten) strikt getrennt werden!
			athlet.addLeistung(leistung);
		} else {
			long leistung_id = leistung.getId();
			athlet.updateLeistung(leistung_id, id_strecke, bezeichnungString, datumString, view.getGeschwindigkeit());				
		}
	}
	
}