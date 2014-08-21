package datenbank_kommunikation;

import java.sql.SQLException;
import java.sql.Statement;

public abstract class DBTableAbstract implements DBTableInterface{
	
	protected Statement stmt;
	
	public DBTableAbstract () {
		this.stmt = DBVerbindung.getStatement();
	}
	
	public void freeTable () {
		try {
			stmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
