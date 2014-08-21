package datenbank_zugriff;

import datenbank_kommunikation.DBTableAthlet;
import model.Athlet;

public class DBAthlet {
	
	DBTableAthlet tableAthlet;

	public DBAthlet() {
		tableAthlet = new DBTableAthlet();
	}
	
	public int neuerAthlet(Athlet athlet) {
		String name = athlet.getName();
		return tableAthlet.neuerAthlet(name);
	}
}
