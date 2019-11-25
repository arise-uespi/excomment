package adapters;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.table.AbstractTableModel;

import database.PostgreSQLJDBC;

public class QueryTableModel extends AbstractTableModel {
	Vector cache;
	int colCount;
	String[] headers;
	Connection db;
	Statement statement;
	String currentURL;

	public QueryTableModel() {
		cache = new Vector();
		try{ 
			new org.postgresql.Driver(); 
		} catch(Exception ee) {
			System.out.println("Não foi possivel usar o driver."); 
		} 
	}

	public String getColumnName(int i) {
		if (headers.length > i) {
			return headers[i];
		}
		return "";
	}

	public int getColumnCount() {
		return colCount;
	}

	public int getRowCount() {
		return cache.size();
	}

	public void clear() {
		cache.clear();
		headers = new String[0];
		fireTableChanged(null);
	}

	public Object getValueAt(int row, int col) {
		return ((String[]) cache.elementAt(row))[col];
	}

	public int setQuery(String q) throws Exception{
		initDB();
		int qtde = 0;
		cache = new Vector();
		try {
			ResultSet rs = statement.executeQuery(q);
			ResultSetMetaData meta = rs.getMetaData();
			colCount = meta.getColumnCount();

			headers = new String[colCount];
			for (int h = 1; h <= colCount; h++) {
				headers[h - 1] = meta.getColumnName(h);
			}

			while (rs.next()) {
				String[] record = new String[colCount];
				for (int i = 0; i < colCount; i++) {
					record[i] = rs.getString(i + 1);
				}
				qtde++;
				cache.addElement(record);
			}
			fireTableChanged(null); // Notifica que há uma nova tabela
		} catch (Exception e) {
			cache = new Vector(); // Limpa o vetor e continua
		}
		PostgreSQLJDBC.close();
		return qtde;
	}

	public void initDB() throws Exception{
		PostgreSQLJDBC.open();
		statement = PostgreSQLJDBC.connection.createStatement();
	}

	public void closeDB() throws SQLException {
			if (statement != null) {
				statement.close();
			}
			if (db != null) {
				db.close();
			}
	}
}