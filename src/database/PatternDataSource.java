package database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import models.Pattern;

/**
 * PatternDataSource contains the methods to handle
 * the Pattern records in the database
 */
public class PatternDataSource {

	/**Insert a Pattern record on the database
	 * 
	 * @param Pattern - the Pattern's content
	 * @throws SQLException 
	 * @throws ClassNotFoundException 
	 */
	public static boolean isPattern(String pattern) throws ClassNotFoundException, SQLException {
		try {
			PostgreSQLJDBC.open();
			//PostgreSQLJDBC.open("localhost", "5432", "Banco3", "postgres", "admin" );
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		String sql = "SELECT PATTERN FROM PATTERN WHERE UPPER(PATTERN) = UPPER(?) ";
		PreparedStatement stmt = PostgreSQLJDBC.connection.prepareStatement(sql);
		stmt.setString(1, pattern);
		ResultSet rs;
		rs = stmt.executeQuery();
        boolean existe = rs.next();
		rs.close();
		stmt.close();
		PostgreSQLJDBC.close();

		return existe;
	}
	
	public static int getIdPattern (String pattern) throws ClassNotFoundException, SQLException {
		int idPattern = -2;
		try {
			PostgreSQLJDBC.open();
			//PostgreSQLJDBC.open("localhost", "5432", "Banco3", "postgres", "admin" );
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		String sql = "SELECT ID FROM PATTERN WHERE UPPER(PATTERN) = UPPER(?)";
		PreparedStatement stmt = PostgreSQLJDBC.connection.prepareStatement(sql);
		stmt.setString(1, pattern);
		ResultSet rs;
		rs = stmt.executeQuery();
		if ( rs.next() ) {
			idPattern = rs.getInt("ID");
		}
		
		rs.close();
		stmt.close();
		PostgreSQLJDBC.close();
		return idPattern;
	}
	
	public static Pattern getPattern (int idPattern) throws ClassNotFoundException, SQLException {
		Pattern pattern = new Pattern();
		try {
			PostgreSQLJDBC.open();
			//PostgreSQLJDBC.open("localhost", "5432", "Banco3", "postgres", "admin" );
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		String sql = "SELECT ID, PATTERN, LENGTH FROM PATTERN WHERE ID = ?";
		PreparedStatement stmt = PostgreSQLJDBC.connection.prepareStatement(sql);
		stmt.setInt(1, idPattern);
		ResultSet rs;
		rs = stmt.executeQuery();
		if ( rs.next() ) {
			pattern.setId(rs.getInt("ID"));
			pattern.setPattern(rs.getString("PATTERN"));
			pattern.setLength(rs.getInt("LENGTH"));
		}
		
		rs.close();
		stmt.close();
		PostgreSQLJDBC.close();
		return pattern;
	}
	
	public static void insertPattern(String pattern, int length, int level, int idPadraoOriginal, float score, String classe) throws ClassNotFoundException, SQLException {
		try {
			PostgreSQLJDBC.open();
			//PostgreSQLJDBC.open("localhost", "5432", "Banco3", "postgres", "admin" );
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		String sql = "INSERT INTO PATTERN (PATTERN, LENGTH, LEVEL, IDORIGINAL, SCORE, CLASSE) "
				+ "VALUES (?, ?, ?, ?, ?, ?)";
		PreparedStatement statement = PostgreSQLJDBC.connection.prepareStatement(sql);
		statement.setString(1, pattern);
		statement.setInt(2, length);
		statement.setInt(3, level);
		statement.setInt(4, idPadraoOriginal);
		statement.setFloat(5, score);
		statement.setString(6, classe);
		statement.executeUpdate();
		
		statement.close();
		PostgreSQLJDBC.close();

	}
	
	public static void insertScore(int idPattern, float score) throws ClassNotFoundException, SQLException{
		try {
			PostgreSQLJDBC.open();
			//PostgreSQLJDBC.open("localhost", "5432", "Banco3", "postgres", "admin" );
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		String sql = "UPDATE PATTERN SET SCORE=? WHERE ID=?;";
		PreparedStatement statement = PostgreSQLJDBC.connection.prepareStatement(sql);
		statement.setFloat(1, score);
		statement.setInt(2, idPattern);
		statement.executeUpdate();

		statement.close();
		PostgreSQLJDBC.close();
	}
	

	//TODO Analisar se é necessário separar os padrões por projeto
	public static ArrayList<String> getPatterns() throws ClassNotFoundException, SQLException {
		try {
			PostgreSQLJDBC.open();
			//PostgreSQLJDBC.open("localhost", "5432", "Banco3", "postgres", "admin" );
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		Statement stmt = null;
		ArrayList<String> patterns = new ArrayList<>();

		stmt = PostgreSQLJDBC.connection.createStatement();
		ResultSet rs;
		rs = stmt.executeQuery( "SELECT PATTERN FROM PATTERN ORDER BY CODIGO;" );

		if ( rs.next() ) {
			patterns.add(rs.getString("PATTERN"));
		}

		rs.close();
		stmt.close();
		PostgreSQLJDBC.close();

		return patterns;
	}
	
	public static ArrayList<Pattern> getAllPatterns() throws ClassNotFoundException, SQLException{
		try {
			PostgreSQLJDBC.open();
			//PostgreSQLJDBC.open("localhost", "5432", "Banco3", "postgres", "admin" );
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		Pattern pattern;
		ArrayList<Pattern> patternsList = new ArrayList<>();
		
		String sql = "SELECT ID, PATTERN, LENGTH, SCORE FROM PATTERN ORDER BY LENGTH DESC";
		
		Statement statement = PostgreSQLJDBC.connection.createStatement();
		ResultSet rs = statement.executeQuery(sql);

		while ( rs.next() ) {
			pattern = new Pattern();
			pattern.setId(rs.getInt(1));
			pattern.setPattern(rs.getString(2));
			pattern.setLength(rs.getInt(3));
			pattern.setScore(rs.getFloat(4));
			patternsList.add(pattern);
		}

		rs.close();
		statement.close();
		PostgreSQLJDBC.close();
		
		return patternsList;
	}
	
	public static ArrayList<Pattern> getNgramPatterns() throws ClassNotFoundException, SQLException{
		try {
			PostgreSQLJDBC.open();
			//PostgreSQLJDBC.open("localhost", "5432", "Banco3", "postgres", "admin" );
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		Pattern pattern;
		ArrayList<Pattern> patternsList = new ArrayList<>();
		
		String sql = "SELECT ID, PATTERN, LENGTH, SCORE FROM PATTERN WHERE LENGTH > 1 ORDER BY LENGTH";
		
		Statement statement = PostgreSQLJDBC.connection.createStatement();
		ResultSet rs = statement.executeQuery(sql);

		while ( rs.next() ) {
			pattern = new Pattern();
			pattern.setId(rs.getInt(1));
			pattern.setPattern(rs.getString(2));
			pattern.setLength(rs.getInt(3));
			pattern.setScore(rs.getFloat(4));
			//System.out.println(pattern.getPattern());
			patternsList.add(pattern);
		}

		rs.close();
		statement.close();
		PostgreSQLJDBC.close();
		
		return patternsList;
	}
	
	public static ArrayList<Pattern> getUgramPatterns() throws ClassNotFoundException, SQLException{
		try {
			PostgreSQLJDBC.open();
			//PostgreSQLJDBC.open("localhost", "5432", "Banco3", "postgres", "admin" );
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		Pattern pattern;
		ArrayList<Pattern> patternsList = new ArrayList<>();
		
		String sql = "SELECT ID, PATTERN, LENGTH, SCORE FROM PATTERN WHERE LENGTH = 1 ORDER BY LENGTH ";
		
		Statement statement = PostgreSQLJDBC.connection.createStatement();
		ResultSet rs = statement.executeQuery(sql);

		while ( rs.next() ) {
			pattern = new Pattern();
			pattern.setId(rs.getInt(1));
			pattern.setPattern(rs.getString(2));
			pattern.setLength(rs.getInt(3));
			pattern.setScore(rs.getFloat(4));
			//System.out.println(pattern.getPattern());
			patternsList.add(pattern);
		}

		rs.close();
		statement.close();
		PostgreSQLJDBC.close();
		
		return patternsList;
	}
}
