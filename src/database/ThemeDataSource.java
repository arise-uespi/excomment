package database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ThemeDataSource {
	
	public static void insertTheme(String theme) throws ClassNotFoundException, SQLException {
		PostgreSQLJDBC.open();

		Statement stmt = null;

		String sql = "INSERT INTO THEME (THEME) "
				+ "VALUES (?);";
		
		PreparedStatement statement = PostgreSQLJDBC.connection.prepareStatement(sql);
		statement.setString(1, theme);
		statement.executeUpdate();
		statement.close();
		PostgreSQLJDBC.close();
	}
	
	public static int getIdTheme (String theme) throws ClassNotFoundException, SQLException {
		int idTheme = -2;
		PostgreSQLJDBC.open();
		//PostgreSQLJDBC.open("localhost", "5432", "experimento3", "postgres", "postgres" );

		String sql = "SELECT ID FROM THEME WHERE UPPER(THEME) = UPPER(?);";
		PreparedStatement stmt = PostgreSQLJDBC.connection.prepareStatement(sql);
		stmt.setString(1, theme);
		ResultSet rs;
		rs = stmt.executeQuery();
		if ( rs.next() ) {
			idTheme = rs.getInt("ID");
		}
		
		rs.close();
		stmt.close();
		PostgreSQLJDBC.close();
		return idTheme;
	}
	
	public static int getTheme(String theme) throws ClassNotFoundException, SQLException {
		PostgreSQLJDBC.open();
		//PostgreSQLJDBC.open("localhost", "5432", "experimento3", "postgres", "postgres" );

		String sql = "SELECT ID FROM THEME WHERE UPPER(THEME) = UPPER(?) ";
		PreparedStatement stmt = PostgreSQLJDBC.connection.prepareStatement(sql);
		stmt.setString(1, theme);
		ResultSet rs;
		rs = stmt.executeQuery();
        boolean existe = rs.next();
		rs.close();
		stmt.close();
		PostgreSQLJDBC.close();
		
		if(!existe){
			insertTheme(theme);
		}
		
		int idTheme = getIdTheme(theme);
		return idTheme;
	}
}
