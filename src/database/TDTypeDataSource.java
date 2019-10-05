package database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TDTypeDataSource {
	public static void insertTDType(String tdtype) throws ClassNotFoundException, SQLException {
		PostgreSQLJDBC.open();
		Statement stmt = null;

		String sql = "INSERT INTO TDTYPE (TDTYPE) "
				+ "VALUES (?);";
		
		PreparedStatement statement = PostgreSQLJDBC.connection.prepareStatement(sql);
		statement.setString(1, tdtype);
		statement.executeUpdate();
		statement.close();
		PostgreSQLJDBC.close();
	}
	
	public static int getIdTDType (String tdtype) throws ClassNotFoundException, SQLException {
		int idTDType = -2;
		PostgreSQLJDBC.open();
		//PostgreSQLJDBC.open("localhost", "5432", "experimento3", "postgres", "postgres" );

		String sql = "SELECT ID FROM TDTYPE WHERE UPPER(TDTYPE) = UPPER(?);";
		PreparedStatement stmt = PostgreSQLJDBC.connection.prepareStatement(sql);
		stmt.setString(1, tdtype);
		ResultSet rs;
		rs = stmt.executeQuery();
		if ( rs.next() ) {
			idTDType = rs.getInt("ID");
		}
		
		rs.close();
		stmt.close();
		PostgreSQLJDBC.close();
		return idTDType;
	}
	
	public static int getTDType(String tdtype) throws SQLException, ClassNotFoundException{
		PostgreSQLJDBC.open();
		//PostgreSQLJDBC.open("localhost", "5432", "experimento3", "postgres", "postgres" );
		String sql = "SELECT ID FROM TDTYPE WHERE UPPER(TDTYPE) = UPPER(?) ";
		PreparedStatement stmt = PostgreSQLJDBC.connection.prepareStatement(sql);
		stmt.setString(1, tdtype);
		ResultSet rs;
		rs = stmt.executeQuery();
        boolean existe = rs.next();
		rs.close();
		stmt.close();
		PostgreSQLJDBC.close();

		if(!existe){
			insertTDType(tdtype);
		}
		
		int idTDType = getIdTDType(tdtype);
		return idTDType;
	}
}
