package database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PatternCommentDataSource {
	
	public static boolean checkRelacaoExiste(int idComment, int idPattern) throws ClassNotFoundException, SQLException{
		try {
			PostgreSQLJDBC.open();
			//PostgreSQLJDBC.open("localhost", "5432", "Banco3", "postgres", "admin" );
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		String sql = "SELECT ID FROM PATTERN_COMMENT WHERE IDPATTERN=? AND IDCOMMENT=?";
		PreparedStatement stmt = PostgreSQLJDBC.connection.prepareStatement(sql);
		stmt.setInt(1, idPattern);
		stmt.setInt(2, idComment);

		ResultSet rs;
		rs = stmt.executeQuery();
        boolean existe = rs.next();
		rs.close();
		stmt.close();
		PostgreSQLJDBC.close();

		return existe;
	}
	
	public static void insertPatternComment(int idComment, int idPattern) throws ClassNotFoundException, SQLException{
		try {
			PostgreSQLJDBC.open();
			//PostgreSQLJDBC.open("localhost", "5432", "Banco3", "postgres", "admin" );
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		String sql = "INSERT INTO PATTERN_COMMENT (IDCOMMENT, IDPATTERN) VALUES (?,?);";
		PreparedStatement statement = PostgreSQLJDBC.connection.prepareStatement(sql);
		statement.setInt(1, idComment);
		statement.setInt(2, idPattern);
		statement.executeUpdate();
		
		statement.close();
		PostgreSQLJDBC.close();
		
	}
}
