package extracao;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import database.PatternDataSource;
import database.PatternThemeDataSource;
import database.Rel_patternDataSource;
import database.TDTypeDataSource;
import database.ThemeDataSource;
import database.ThemeTDTypeDataSource;
import suporte.Constantes;
import suporte.Funcoes;
import suporte.Variaveis;

public class ExtractPatterns {
	public static void extractPatterns() throws IOException, ClassNotFoundException, SQLException, BadLocationException {
		//1 - Carga de todos os padroes do arquivos padroesselecionados.txt
		//Padrao e a classe gramatical separado por tab

		FileReader file = new FileReader(Constantes.PADROESSELECIONADOS);
		BufferedReader readFile = new BufferedReader(file);
		String line = readFile.readLine();
		int k = 0;
		while (line != null && !line.equals(null) && !line.equalsIgnoreCase("")){
			k++;
			String[] patternClasse = line.split("\t");
			String pattern = patternClasse[0].trim();
			String classe = patternClasse[1].trim();
			String[] words = pattern.split(" ");
			pattern = pattern.replaceAll("\"", "");
			pattern = pattern.replaceAll("!", "");
			pattern = pattern.toLowerCase();
			PatternDataSource.insertPattern(pattern, words.length, 0, -1, 0, classe);
			line = readFile.readLine();
		}
			file.close();
	}
	
	public static void extractScores() throws IOException, ClassNotFoundException, SQLException {
		//2 - Carga dos Padroes com os Scores separados por tab do arquivo padroesscores.txt
		//    Retorna o ID do padrao e faz o update na tabela pattern
		FileReader file = new FileReader(Constantes.PADROESSCORES);
		BufferedReader readFile = new BufferedReader(file);
		String line = readFile.readLine();
		while (line != null && !line.equals(null) && !line.equalsIgnoreCase("")){
			String[] patternScore = line.split("\t");
			String pattern = patternScore[0].trim();
			pattern = pattern.replaceAll("\"", "");
			pattern = pattern.replaceAll("!", "");
			pattern = pattern.toLowerCase();
			int idPattern = PatternDataSource.getIdPattern(pattern);
			float score = 0;
			if(!patternScore[1].equalsIgnoreCase("")){
				score = Float.parseFloat(patternScore[1]);
			}
					
			if(idPattern == -2){
				System.out.println(idPattern + " - " + pattern + " - "+ score);
			}else{
				PatternDataSource.insertScore(idPattern, score);
			}
			
			line = readFile.readLine();
		}
		file.close();
	}
	
	public static void extractThemes() throws IOException, ClassNotFoundException, SQLException {
		//3 - Carrega o arquivos com os padroes e os themes arquivo padroestheme.txt
		//Separados padroes e themes por tab
		//Avalia se o theme ja existe. Existindo insere o ID em pattern_theme
		//Nao existindo insere na tabela theme, depois relaciona no pattern_theme
		//Pode haver 2 themes relacionados a 1 padrao. Separados por virgula
		FileReader file = new FileReader(Constantes.PADROESTHEMES);
		BufferedReader readFile = new BufferedReader(file);
		String line = readFile.readLine();
		while (line != null && !line.equals(null) && !line.equalsIgnoreCase("")){
			String[] patternTheme = line.split("\t");
			String[] themes = patternTheme[1].split(", ");
			String pattern = patternTheme[0];
			pattern = pattern.replaceAll("\"", "");
			pattern = pattern.replaceAll("!", "");
			pattern = pattern.trim();
			pattern = pattern.toLowerCase();
			int idPattern = PatternDataSource.getIdPattern(pattern);
			for(int i=0; i < themes.length; i++){
				themes[i] = themes[i].replaceAll("\"", "");
				themes[i] = themes[i].replaceAll("!", "");
				themes[i] = themes[i].trim();
				int idTheme = ThemeDataSource.getTheme(themes[i]);
				if(!PatternThemeDataSource.existePatternTheme(idPattern, idTheme)){
					if(idPattern == -2){
						System.out.println(idPattern + " - " + pattern + " - "+ idTheme);
					}else{
					    //System.out.println(idPattern + " - " + idTheme);
						PatternThemeDataSource.insertPatternTheme(idPattern, idTheme);
					}
				}
			}	
			line = readFile.readLine();
		}
		file.close();
	}
	
	public static void extractTDtypes() throws IOException, ClassNotFoundException, SQLException{
		//4 - Carga de todos os TDTypes
		FileReader file = new FileReader(Constantes.THEMESTDTYPES);
		BufferedReader readFile = new BufferedReader(file);
		String line = readFile.readLine();
		System.out.println("===== TD TYPES =====");
		
		while (line != null && !line.equals(null) && !line.equalsIgnoreCase("")){
			String[] ThemeTDType = line.split("\t");
			String tdType = ThemeTDType[0].trim();
			String theme = ThemeTDType[1].trim();
			tdType = tdType.replaceAll("\"", "");
			tdType = tdType.replaceAll("!", "");
			theme = theme.replaceAll("\"", "");
			theme = theme.replaceAll("!", "");
			
			int idTheme = ThemeDataSource.getTheme(theme);
			int idTDType = TDTypeDataSource.getTDType(tdType);
			System.out.println(theme + " - " + tdType);

			if(!ThemeTDTypeDataSource.existeThemeTDType(idTheme, idTDType)){
				if(idTheme == -2){
					System.out.println(idTheme + " - " + theme + " - "+ idTDType);
				}else{
					ThemeTDTypeDataSource.insertThemeTDType(idTheme, idTDType);
				}
			}
			line = readFile.readLine();
		}
		file.close();
	}
	
	public static void extractRelPatterns() throws IOException, ClassNotFoundException, SQLException {
		//5 - Carrega os padrões e os relacionamentos entre eles. 
		FileReader file = new FileReader(Constantes.RELPATTERNS);
		BufferedReader readFile = new BufferedReader(file);
		String line = readFile.readLine();
		while (line != null && !line.equals(null) && !line.equalsIgnoreCase("")){
			String[] patternRelacionamentos = line.split("\t");
			String parent = patternRelacionamentos[1];
			//Na planilha padroes sem relacionamentos tem um '-' na coluna
			if(!patternRelacionamentos[0].equalsIgnoreCase("-")){
				String[] patterns = patternRelacionamentos[0].split(", ");
				parent = parent.replaceAll("\"", "");
				parent = parent.replaceAll("!", "");
				parent = parent.trim();
				parent = parent.toLowerCase();

				int idParent = PatternDataSource.getIdPattern(parent);
				for(int i=0; i < patterns.length; i++){
					patterns[i] = patterns[i].replaceAll("\"", "");
					patterns[i] = patterns[i].replaceAll("!", "");
					patterns[i] = patterns[i].trim();
					patterns[i] = patterns[i].toLowerCase();
					int idPattern = PatternDataSource.getIdPattern(patterns[i]);

					if(!Rel_patternDataSource.existeRelPattern(idPattern, idParent)){
						if(idPattern == -2 || idParent == -2){
							System.err.println("Erro: " + parent + " - " + patterns[i]);
						}else{
						    //System.out.println(idPattern + " - " + idParent);
							Rel_patternDataSource.insertRelPattern(idPattern, idParent);
						}
					}
				}	
			}
			line = readFile.readLine();
		}
		file.close();
	}
	
}
