package leistung_bearbeiten;

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
}
