package datenbank_kommunikation;

import java.sql.SQLException;

public class DBTableAthlet extends DBTableAbstract{

	public DBTableAthlet() {
		super();
	}

	public void installiereSchemaVersion1() throws SQLException {
		String createAthlet = "CREATE TABLE IF NOT EXISTS Athlet "
				+ "(athlet_id INT PRIMARY KEY AUTO_INCREMENT(1,1) NOT NULL, "
				+ "name VARCHAR(255) NOT NULL)";
		stmt.executeUpdate(createAthlet);
	}
	
	public int neuerAthlet (String name) {
		String insertAthlet = "INSERT INTO athlet (name)"
				+ "VALUES ('"+name+"')";
		try {
			return stmt.executeUpdate(insertAthlet);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
}
