package database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PatternThemeDataSource {
	public static void insertPatternTheme(int idPattern, int idTheme) throws ClassNotFoundException, SQLException{
		try {
			PostgreSQLJDBC.open();
			//PostgreSQLJDBC.open("localhost", "5432", "Banco3", "postgres", "admin" );
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		String sql = "INSERT INTO PATTERN_THEME (IDPATTERN, IDTHEME) VALUES (?,?);";
		PreparedStatement statement = PostgreSQLJDBC.connection.prepareStatement(sql);
		statement.setInt(1, idPattern);
		statement.setInt(2, idTheme);
		statement.executeUpdate();
		
		statement.close();
		PostgreSQLJDBC.close();
		
	}
	
	public static boolean existePatternTheme(int idPattern, int idTheme) throws ClassNotFoundException, SQLException{
		boolean existe = false;
		try {
			PostgreSQLJDBC.open();
			//PostgreSQLJDBC.open("localhost", "5432", "Banco3", "postgres", "admin" );
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		String sql = "SELECT ID FROM PATTERN_THEME WHERE IDPATTERN=? and IDTHEME=? ;";
		PreparedStatement stmt = PostgreSQLJDBC.connection.prepareStatement(sql);
		stmt.setInt(1, idPattern);
		stmt.setInt(2, idTheme);

		ResultSet rs;
		rs = stmt.executeQuery();
        existe = rs.next();
		rs.close();
		stmt.close();
		PostgreSQLJDBC.close();
		return existe;
	}
}
