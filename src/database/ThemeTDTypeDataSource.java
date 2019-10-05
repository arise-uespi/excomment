package database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ThemeTDTypeDataSource {
	public static void insertThemeTDType(int idTheme, int idTdType) throws ClassNotFoundException, SQLException{
		try {
			PostgreSQLJDBC.open();
			//PostgreSQLJDBC.open("localhost", "5432", "Banco3", "postgres", "admin" );
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		String sql = "INSERT INTO THEME_TDTYPE (IDTHEME, IDTDTYPE) VALUES (?,?);";
		PreparedStatement statement = PostgreSQLJDBC.connection.prepareStatement(sql);
		statement.setInt(1, idTheme);
		statement.setInt(2, idTdType);
		statement.executeUpdate();
		
		statement.close();
		PostgreSQLJDBC.close();
		
	}
	
	public static boolean existeThemeTDType(int idTheme, int idTDType) throws ClassNotFoundException, SQLException{
		boolean existe = false;
		try {
			PostgreSQLJDBC.open();
			//PostgreSQLJDBC.open("localhost", "5432", "Banco3", "postgres", "admin" );
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		String sql = "SELECT ID FROM THEME_TDTYPE WHERE IDTHEME=? and IDTDTYPE=? ;";
		PreparedStatement stmt = PostgreSQLJDBC.connection.prepareStatement(sql);
		stmt.setInt(1, idTheme);
		stmt.setInt(2, idTDType);

		ResultSet rs;
		rs = stmt.executeQuery();
        existe = rs.next();
		rs.close();
		stmt.close();
		PostgreSQLJDBC.close();
		return existe;
	}
}
