package database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import models.Comment;
import models.Pattern;

/**
 * CommentDataSource contains the methods to handle
 * the comments records in the database
 */
public class CommentDataSource {

	/**Insert a comment record on the database
	 * 
	 * @param comment - the comment's content
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public void insertComment(String comment) throws ClassNotFoundException, SQLException {
		try {
			PostgreSQLJDBC.open();
			//PostgreSQLJDBC.open("localhost", "5432", "Banco3", "postgres", "admin" );
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		Statement stmt = null;

		stmt = PostgreSQLJDBC.connection.createStatement();
		String sql = "INSERT INTO COMMENTS (COMMENT) "
				+ "VALUES ('" + PostgreSQLJDBC.treatStoreDB(comment) + "');";
		stmt.executeUpdate(sql);

		stmt.close();
		PostgreSQLJDBC.close();

	}

	/**Generic method to get a comment record from the database
	 * 
	 * @param coluna - target column
	 * @param valor - search value
	 * @param isText - boolean variable to inform if the column is a text column
	 * @return the id of the record or -1
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public static int getComment(String coluna, String valor, boolean isText, int idProject) throws ClassNotFoundException, SQLException {
		PostgreSQLJDBC.open();

		Statement stmt = null;
		int id = -1;

		stmt = PostgreSQLJDBC.connection.createStatement();
		ResultSet rs;

		//Se a coluna é do tipo texto usamos o comando LIKE caso contrário usamos =
		if (isText) {
			rs = stmt.executeQuery( "SELECT ID FROM COMMENTS WHERE " + coluna + " LIKE '" + valor + "' AND IDPROJECT = " + idProject + ";" );
		} else {
			rs = stmt.executeQuery( "SELECT ID FROM COMMENTS WHERE " + coluna + " = " + valor + " AND IDPROJECT = " + idProject + ";" );
		}

		if ( rs.next() ) {
			id = rs.getInt("ID");
		}

		rs.close();
		stmt.close();
		PostgreSQLJDBC.close();

		return id;
	}
	
	public static Comment getComment(int idComment) throws ClassNotFoundException, SQLException{
		Comment comment = new Comment();
		try {
			PostgreSQLJDBC.open();
			//PostgreSQLJDBC.open("localhost", "5432", "Banco3", "postgres", "admin" );
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		String sql = "SELECT ID, COMMENT FROM COMMENTS WHERE ID = ?";
		PreparedStatement stmt = PostgreSQLJDBC.connection.prepareStatement(sql);
		stmt.setInt(1, idComment);
		ResultSet rs;
		rs = stmt.executeQuery();
		if ( rs.next() ) {
			comment.setId(rs.getInt("ID"));
			comment.setComment(rs.getString("COMMENT"));
		}
		
		rs.close();
		stmt.close();
		PostgreSQLJDBC.close();
		return comment;
	}

	public static ArrayList<String> getComments(int idProject) throws ClassNotFoundException, SQLException {
		try {
			PostgreSQLJDBC.open();
			//PostgreSQLJDBC.open("localhost", "5432", "Banco3", "postgres", "admin" );
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		Statement stmt = null;
		ArrayList<String> comentarios = new ArrayList<>();

		stmt = PostgreSQLJDBC.connection.createStatement();
		ResultSet rs;
		rs = stmt.executeQuery( "SELECT COMMENT FROM COMMENTS WHERE IDPROJECT = " 
				+ idProject + " ORDER BY ID;" );

		while (rs.next()) {
			comentarios.add(rs.getString("COMMENT"));
		}

		rs.close();
		stmt.close();
		PostgreSQLJDBC.close();

		return comentarios;
	}
	
	public static ArrayList<Comment> getAllComments(int idProject) throws ClassNotFoundException, SQLException {
		try {
			PostgreSQLJDBC.open();
			//PostgreSQLJDBC.open("localhost", "5432", "Banco3", "postgres", "admin" );
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		ArrayList<Comment> comentarios = new ArrayList<>();
		Comment comentario;
		String sql = "SELECT ID, COMMENT FROM COMMENTS WHERE IDPROJECT =" + idProject+ " ORDER BY ID;";
		
		Statement statement = PostgreSQLJDBC.connection.createStatement();
		ResultSet rs = statement.executeQuery(sql);

		while (rs.next()) {
			comentario = new Comment();
			comentario.setId(rs.getInt(1));
			comentario.setComment(rs.getString(2));
			comentarios.add(comentario);
		}

		rs.close();
		statement.close();
		PostgreSQLJDBC.close();

		return comentarios;
	}

	public static int getCount(int idProject) throws ClassNotFoundException, SQLException {
		try {
			PostgreSQLJDBC.open();
			//PostgreSQLJDBC.open("localhost", "5432", "Banco3", "postgres", "admin" );
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		Statement stmt = null;
		int id = -1;

		stmt = PostgreSQLJDBC.connection.createStatement();
		ResultSet rs;
		rs = stmt.executeQuery( "SELECT COUNT(COMMENT) FROM COMMENTS WHERE IDPROJECT = " + idProject + ";" );

		if ( rs.next() ) {
			id = rs.getInt("COUNT");
		}

		rs.close();
		stmt.close();
		PostgreSQLJDBC.close();

		return id;
	}
}
