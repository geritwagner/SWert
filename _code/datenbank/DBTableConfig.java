package datenbank;

import java.sql.SQLException;
import java.sql.Statement;

public class DBTableConfig implements DBTableInterface {

	private final int DB_VERSION = 1;
	
	public void installiereSchemaVersion1(Statement stmt) throws SQLException {
		String createConfig = "CREATE TABLE IF NOT EXISTS Config "
				+ "(db_version INT NOT NULL, "
				+ "swert_version DOUBLE NOT NULL,"
				+ "active_athlet INT NOT NULL)";
		stmt.executeUpdate(createConfig);
		String fillConfig = "INSERT INTO Config (db_version, swert_version, active_athlet) "
				+ "VALUES ("+DB_VERSION+",4.1, -1)";
		stmt.executeUpdate(fillConfig);
	}

}
