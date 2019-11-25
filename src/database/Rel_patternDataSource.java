package database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import models.Pattern;
import models.Rel_patterns;

public class Rel_patternDataSource {
	
	public static boolean existeRelPattern(int idpattern, int idparent) throws ClassNotFoundException, SQLException{
		boolean existe = false;
		try {
			PostgreSQLJDBC.open();
			//PostgreSQLJDBC.open("localhost", "5432", "Banco3", "postgres", "admin" );
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		String sql = "SELECT ID FROM REL_PATTERNS WHERE IDPATTERN=? and IDPARENT=?;";
		PreparedStatement stmt = PostgreSQLJDBC.connection.prepareStatement(sql);
		stmt.setInt(1, idpattern);
		stmt.setInt(2, idparent);

		ResultSet rs;
		rs = stmt.executeQuery();
        existe = rs.next();
		rs.close();
		stmt.close();
		PostgreSQLJDBC.close();
		return existe;
	}
	
	public static void insertRelPattern(int idPattern, int idParent) throws ClassNotFoundException, SQLException{
		try {
			PostgreSQLJDBC.open();
			//PostgreSQLJDBC.open("localhost", "5432", "Banco3", "postgres", "admin" );
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		String sql = "INSERT INTO REL_PATTERNS (IDPATTERN, IDPARENT) VALUES (?,?);";
		PreparedStatement statement = PostgreSQLJDBC.connection.prepareStatement(sql);
		statement.setInt(1, idPattern);
		statement.setInt(2, idParent);
		statement.executeUpdate();
		
		statement.close();
		PostgreSQLJDBC.close();
		
	}
	
	public static ArrayList<Rel_patterns> getAllPatterns() throws ClassNotFoundException, SQLException{
		try {
			PostgreSQLJDBC.open();
			//PostgreSQLJDBC.open("localhost", "5432", "Banco3", "postgres", "admin" );
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		Rel_patterns relpattern;
		ArrayList<Rel_patterns> relpatternsList = new ArrayList<>();
		
		String sql = "SELECT ID, IDPATTERN, IDPARENT FROM REL_PATTERN ORDER BY ID";
		
		Statement statement = PostgreSQLJDBC.connection.createStatement();
		ResultSet rs = statement.executeQuery(sql);

		while ( rs.next() ) {
			relpattern = new Rel_patterns();
			relpattern.setId(rs.getInt(1));
			relpattern.setIdPattern(rs.getInt(2));
			relpattern.setIdParent(rs.getInt(3));
			//System.out.println(pattern.getPattern());
			relpatternsList.add(relpattern);
		}

		rs.close();
		statement.close();
		PostgreSQLJDBC.close();
		
		return relpatternsList;
	}
	
	public static ArrayList<Integer> getParents(int idPattern) throws ClassNotFoundException, SQLException{
		try {
			PostgreSQLJDBC.open();
			//PostgreSQLJDBC.open("localhost", "5432", "Banco3", "postgres", "admin" );
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		ArrayList<Integer> parents = new ArrayList<>();
		
		String sql = "SELECT IDPARENT FROM REL_PATTERNS WHERE idPattern =" + idPattern;
		
		Statement statement = PostgreSQLJDBC.connection.createStatement();
		ResultSet rs = statement.executeQuery(sql);

		while ( rs.next() ) {
			//System.out.println(pattern.getPattern());
			parents.add(rs.getInt(1));
		}

		rs.close();
		statement.close();
		PostgreSQLJDBC.close();
		return parents;			
	}

}
