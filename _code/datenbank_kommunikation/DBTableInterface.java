package datenbank_kommunikation;

import java.sql.SQLException;

public abstract interface DBTableInterface {
	public void installiereSchemaVersion1 () throws SQLException;
	public void freeTable () throws SQLException;
}
