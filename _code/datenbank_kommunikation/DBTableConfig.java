package datenbank_kommunikation;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import main.Hauptfenster;

public class DBTableConfig implements DBTableInterface {

	private PreparedStatement stmt = null;
	
	public DBTableConfig() {
		super();
	}
	
	public void installiereSchemaVersion1() throws SQLException {
		String createConfig = "CREATE TABLE IF NOT EXISTS Config "
				+ "(db_version INT NOT NULL, "
				+ "active_athlet INT NOT NULL)";
		stmt = DBVerbindung.getStatement(createConfig);
		stmt.executeUpdate();
		String fillConfig = "INSERT INTO Config (db_version, active_athlet) "
				+ "VALUES (?, -1)";
		stmt = DBVerbindung.getStatement(fillConfig);
		stmt.setInt(1, Hauptfenster.Db_Version);
		stmt.executeUpdate();
	}

	public void freeTable() throws SQLException {
		if (stmt != null) {
			stmt.close();			
		}
	}

}
