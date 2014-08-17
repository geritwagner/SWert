package datenbank;

import java.sql.SQLException;
import java.sql.Statement;

public class DBTableLeistung implements DBTableInterface{

	public void installiereSchemaVersion1(Statement stmt) throws SQLException {
		String createLeistung = "CREATE TABLE IF NOT EXISTS Leistung "
				+ "(leistung_id INT PRIMARY KEY AUTO_INCREMENT(1,1) NOT NULL, "
				+ "beschreibung VARCHAR(255) NOT NULL,"
				+ "datum DATE NOT NULL,"
				+ "zeit DOUBLE NOT NULL,"
				+ "selectedForCalculatingSlopeFaktor BOOLEAN,"
				+ "athlet_id INT,"
				+ "strecke_id INT,"
				+ "FOREIGN KEY(strecke_id) REFERENCES STRECKE(STRECKE_ID),"
				+ "FOREIGN KEY(athlet_id) REFERENCES ATHLET(ATHLET_ID))";
		stmt.executeUpdate(createLeistung);
	}

}
