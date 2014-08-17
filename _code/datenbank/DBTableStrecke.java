package datenbank;

import java.sql.SQLException;
import java.sql.Statement;

public class DBTableStrecke implements DBTableInterface{

	public void installiereSchemaVersion1(Statement stmt) throws SQLException {
		String createStrecke = "CREATE TABLE IF NOT EXISTS Strecke "
				+ "(strecke_id INT PRIMARY KEY AUTO_INCREMENT(1,1) NOT NULL, "
				+ "bezeichnung VARCHAR(255) NOT NULL,"
				+ "laenge INT NOT NULL)";
		stmt.executeUpdate(createStrecke);
	}

}
