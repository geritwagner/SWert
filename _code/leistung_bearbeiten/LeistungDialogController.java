package leistung_bearbeiten;

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
	
}
