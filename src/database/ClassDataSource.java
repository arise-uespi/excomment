package database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import suporte.Variaveis;
/**
 * ClassDataSource contains the methods to handle
 * the classes records in the database
 */
public class ClassDataSource {
	/**Returns the record id passing its name 
	 * 
	 * @param name - the name of the class
	 * @return the database id of the desired class or -1
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public static int getIdClass(String name, String path, int idProject) throws ClassNotFoundException, SQLException {
		PostgreSQLJDBC.open();

		Statement stmt = null;
		int id = -1;

			stmt = PostgreSQLJDBC.connection.createStatement();
			ResultSet rs = stmt.executeQuery( "SELECT ID FROM CLASSES WHERE NAME LIKE '" + name + "' AND IDPROJECT = " + idProject + " " +
					" AND PATH LIKE '" + path + "';" );

			while ( rs.next() ) {
				id = rs.getInt("ID");
			}

			rs.close();
			stmt.close();
			PostgreSQLJDBC.close();

			return id;
	}

	/**Inserts a class record on the database
	 * 
	 * @param name - the name of the class
	 * @param path - the file location
	 * @return the database id of the inserted class or -1
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public static int insertClass(String name, String path, int idProject) throws SQLException, ClassNotFoundException {
		PostgreSQLJDBC.open();

		Statement stmt = null;
		int key = -1;


		stmt = PostgreSQLJDBC.connection.createStatement();
		String sql = "INSERT INTO CLASSES (NAME, PATH, IDPROJECT) "
				+ "VALUES ('" + name + "', '" + path + "', " + idProject + ");";

		//Insert and return the id
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
	
	public static int upsertClass(String cls, String path) throws SQLException, ClassNotFoundException {
		int idClass = -1;
		if (cls.length() > 0) {
			//Verifica se essa classe ja foi inserida e obtém a id
			//usamos path.replace("\\", "\\\\") pois "\" é caractere reservado do postgre
			String aux = path.replace("\\", "\\\\");
			idClass = DataAccess.classDataSource.getIdClass(cls, aux, Variaveis.idProject);

			//Se ainda não foi inserida a insere e obtém a id
			if (!(idClass > 0)) {
				idClass = ClassDataSource.insertClass(cls, path, Variaveis.idProject);
			}
		}
		return idClass;
	}
}
