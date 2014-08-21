package datenbank_kommunikation;

import java.sql.SQLException;

import main.Hauptfenster;

public class DBTableConfig extends DBTableAbstract {

	public DBTableConfig() {
		super();
	}
	
	public void installiereSchemaVersion1() throws SQLException {
		String createConfig = "CREATE TABLE IF NOT EXISTS Config "
				+ "(db_version INT NOT NULL, "
				+ "swert_version DOUBLE NOT NULL,"
				+ "active_athlet INT NOT NULL)";
		stmt.executeUpdate(createConfig);
		String fillConfig = "INSERT INTO Config (db_version, swert_version, active_athlet) "
				+ "VALUES ("+Hauptfenster.Db_Version+",4.1, -1)";
		stmt.executeUpdate(fillConfig);
	}

}
