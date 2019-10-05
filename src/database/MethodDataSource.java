package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import suporte.Variaveis;

/**
 * MethodDataSource contains the methods to handle
 * the methods records in the database
 */
public class MethodDataSource {

	/**Method to get a method record from the database
	 * 
	 * @param name - the name of the method
	 * @param idClass - the class' id that contains it
	 * @return the method's id or -1
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public static int getIdMethod(String name, int idClass) throws ClassNotFoundException, SQLException {
		PostgreSQLJDBC.open();

		Statement stmt = null;
		int id = -1;

		stmt = PostgreSQLJDBC.connection.createStatement();
		ResultSet rs = stmt.executeQuery( "SELECT ID FROM METHODS WHERE NAME LIKE '" + name + "' AND IDCLASS = " + idClass + ";" );

		while ( rs.next() ) {
			id = rs.getInt("ID");
		}

		rs.close();
		stmt.close();
		PostgreSQLJDBC.close();
		return id;
	}

	/**Insert a method record on the database
	 * 
	 * @param name - the name of the method
	 * @param idClass - the class' id that contains it
	 * @return the method's id or -1
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public static int insertMethod(String name, int idClass) throws ClassNotFoundException, SQLException {
		PostgreSQLJDBC.open();

		Statement stmt = null;
		int key = -1;

		stmt = PostgreSQLJDBC.connection.createStatement();

		String sql = "INSERT INTO METHODS (NAME, IDCLASS) "
				+ "VALUES ('" + name + "', " + idClass + ");";
		stmt.execute(sql, stmt.RETURN_GENERATED_KEYS);
		ResultSet rs = stmt.getGeneratedKeys();

		if ( rs.next() ) {
			// Retrieve the auto generated key(s). 
			key = rs.getInt(1);
		}

		stmt.close();
		PostgreSQLJDBC.close();
		return key;
	}
	
	public static int upsertMethod(String method, int idClass) throws SQLException, ClassNotFoundException {
		int idMethod = -1;
		if (method.length() > 0) {
			//Verifica se esse metodo ja foi inserido e obtém a id
			idMethod = MethodDataSource.getIdMethod(method, idClass);

			//Se ainda não foi inserido a insere
			if (!(idMethod > 0)) {
				idMethod = MethodDataSource.insertMethod(method, idClass);
			}
		}
		return idMethod;
	}
}