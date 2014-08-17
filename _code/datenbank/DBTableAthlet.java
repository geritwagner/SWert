package datenbank;

import java.sql.SQLException;
import java.sql.Statement;

public class DBTableAthlet implements DBTableInterface{

	public void installiereSchemaVersion1(Statement stmt) throws SQLException {
		String createAthlet = "CREATE TABLE IF NOT EXISTS Athlet "
				+ "(athlet_id INT PRIMARY KEY AUTO_INCREMENT(1,1) NOT NULL, "
				+ "name VARCHAR(255) NOT NULL)";
		stmt.executeUpdate(createAthlet);
	}

}
