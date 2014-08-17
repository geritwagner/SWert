package datenbank;

import java.sql.SQLException;
import java.sql.Statement;

public interface DBTableInterface {
	public void installiereSchemaVersion1 (Statement stmt) throws SQLException;
}
