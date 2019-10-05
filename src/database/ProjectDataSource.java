package database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * ProjectDataSource contains the methods to handle
 * the projects records in the database
 */
public class ProjectDataSource {
	
	/**Insert a project record on the database
	 * 
	 * @param name - the name of the project
	 * @return the project's id or -1
	 */
	public static int insertProject(String name) {
		try {
			PostgreSQLJDBC.open();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		int key = -1;
		
		try {
			String sql = "SELECT ID FROM PROJECTS WHERE NAME LIKE '" + name + "';";
			
			PreparedStatement statement = PostgreSQLJDBC.connection.prepareStatement(sql);
			ResultSet rs = statement.executeQuery();
			if ( rs.next() ) {
				key = rs.getInt(1);
			}
			statement.close();
			
			if (!(key > 0)) {
				sql = "INSERT INTO PROJECTS (NAME) "
						+ "VALUES ('" + name + "') RETURNING ID;";
				
				statement = PostgreSQLJDBC.connection.prepareStatement(sql);
				rs = statement.executeQuery();
				if ( rs.next() ) {
					key = rs.getInt(1);
				}
				statement.close();
			}
			
			PostgreSQLJDBC.close();
		} catch (SQLException e) {
		} finally {
			return key;
		}
	}
}
