package leistung_bearbeiten;

import java.text.DateFormat;
import java.util.Date;

import model.Athlet;
import model.Leistung;

public class SchwellenDialogController {

	Athlet athlet;
	Leistung leistung;
	SchwellenDialog view;
	
	protected SchwellenDialogController(Athlet athlet, Leistung leistung, SchwellenDialog view){
		this.athlet = athlet;
		this.leistung = leistung;
		this.view = view;
	}

	protected void release(){
		view = null;
		athlet = null;
	}
	
	protected boolean leistung�ndern(){
		if(true == view.validateInput()) {
			�nderungenDurchf�hren();
			return true;
		} else {
			return false;
		}
	}
	
	private void �nderungenDurchf�hren(){
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
}