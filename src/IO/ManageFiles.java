package IO;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.table.TableModel;

import suporte.Funcoes;

import adapters.QueryTableModel;

public class ManageFiles {

	public String readFileToString(String filePath) throws IOException {
		StringBuilder fileData = new StringBuilder(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));

		char[] buf = new char[10];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}

		reader.close();
		return fileData.toString();
	}

	public ArrayList<String> readFileSeparateLines(String path) throws IOException{
		FileInputStream fis = new FileInputStream(path);
		InputStreamReader isr = new InputStreamReader(fis, "ISO-8859-1");
		BufferedReader in = new BufferedReader(isr);

		ArrayList<String> words =  new ArrayList<String>();
		String lines = "";
		while ((lines = in.readLine()) != null)
		{
			words.add(lines);
		}
		return words;
	}
	
	public void writeFileLineByLine(String path, ArrayList<String> content) throws IOException {
		File fout = new File(path);
		
		if(!fout.exists()) {
			fout.createNewFile();
		} 
		
		FileOutputStream fos = new FileOutputStream(fout);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
	 
		for (int i = 0; i < content.size(); i++) {
			bw.write(content.get(i));
			bw.newLine();
		}
	 
		bw.close();
	}
	
	public void toExcel(JTable table, File file) throws IOException{
	    	if (!file.exists()) {
				file.createNewFile();
			}
	        QueryTableModel model = (QueryTableModel) table.getModel();
	        FileWriter excel = new FileWriter(file);

	        for(int i = 0; i < model.getColumnCount(); i++){
	            excel.write(model.getColumnName(i) + "\t");
	        }

	        excel.write("\n");

	        for(int i=0; i< model.getRowCount(); i++) {
	            for(int j=0; j < model.getColumnCount(); j++) {
	            	if (model.getValueAt(i,j) != null ) {
	            		 excel.write(Funcoes.removeSeparators(model.getValueAt(i,j).toString()) + "\t");
					} else{
						excel.write(" \t");
					}
	            }
	            excel.write("\n");
	        }

	        excel.close();
	}
}
