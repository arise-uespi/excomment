package extracao;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import javax.swing.text.Document;

import org.apache.lucene.search.spell.LevensteinDistance;

import database.ClassDataSource;
import database.CommentDataSource;
import database.DataAccess;
import database.MethodDataSource;
import database.PostgreSQLJDBC;
import preprocessamento.Preprocessing;
import suporte.Funcoes;
import suporte.Variaveis;


public class ParseComments {
	static int level = -1;
	//static int contTotalComments = 0;
	//static int contTotalDiscartedComments = 0;
	private static String directoryTo;
	private static String originalFileExtension;
	private static String commentFileExtension;

	static String pathFile = "";

	/**Metodo para procurar comentários em todos os arquivos com a extenção desejada 
	 * 
	 * @param from - O path do projeto a ser analisado
	 * @param classFileExtension - Extenção do arquivo sem o "." ex: java
	 * @param to - Sem funcionalidade no momento
	 * @param TexFileExtension - Sem funcionalidade no momento
	 * @param isJSON - Sem funcionalidade no momento
	 * @return
	 */
	public static boolean getComments(String from, String classFileExtension,
			String to, String TexFileExtension, boolean isJSON) {
		originalFileExtension = classFileExtension;
		directoryTo = to;
		commentFileExtension = TexFileExtension;
		boolean b;

		try {
			b = searchEntirePath(new File(from), classFileExtension, isJSON);
			
			//TODO Depois deve-se considerar utilizar o mesmo builder da extração e mudar a transação de deferred para initially immediate
			StringBuilder builderPattern = findPatterns();
			DataAccess.dataManager.execSQL(builderPattern.toString());
		} catch (Exception e) {
			e.printStackTrace();
			b = false;
		}
		return b;
	}

	/**Analisa um diretório ou um arquivo em busca de comentários
	 * 
	 * @param path - Local do diretório/arquivo
	 * @param extension - Extenção do(s) arquivo(s) a ser(em) analisado(s)
	 * @param isJSON - Informa se o arquivo gerado será um JSON - Sem funcionalidade no momento
	 * @throws Exception 
	 */
	public static boolean searchEntirePath(File path, final String extension, final boolean isJSON) throws Exception{
		final File files[]; 
		level++; 
		files = path.listFiles();
		Process process;

		//Se há arquivos
		if (files != null) {
			Arrays.sort(files);
			int size = files.length;

			for (final File file : files) {

				//Se a extenção do arquivo é a desejada
				if (getFileExtension(file.toString()).toLowerCase().equals(extension)){
					
										Funcoes.printExtracting("\nAnalisando:", file.toString());

					//Limpa-se as variaveis auxiliares temporarias
					Variaveis.comments.clear();
					Variaveis.linked_comments.clear();

					//Utiliza-se o src2srcml.ex para extrair o conteudo do arquivo em xml com marcadores
					process = new ProcessBuilder(new String[] {
							"src2srcml.exe", file.toString()}).start();
					InputStream is = process.getInputStream();
					InputStreamReader isr = new InputStreamReader(is, "UTF-8");

					StringBuilder builderComments = extraiInfos(isr, file);

					//Executamos todos os inserts acima
					DataAccess.dataManager.execSQL(builderComments.toString());
				}

				//Continua vasculhando os diretórios recursivamente
				if (file.isDirectory()) {			
					searchEntirePath(file, extension, isJSON);
				}
			}
			level--;			
					
			//Foi passado um arquivo unico e não um diretorio
		} else if (path.toString().length() > 0) { 
			process = new ProcessBuilder(new String[] {
					"src2srcml.exe", path.toString()}).start();
			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line;

			Document doc = Variaveis.docEditExtracting;
			doc.insertString(doc.getLength(), "FILE> " + path.toString() + "\n", null);

			StringBuilder builderComments = extraiInfos(isr, new File(path.toString()));

			//Executamos todos os inserts acima
			DataAccess.dataManager.execSQL(builderComments.toString());
		}

		System.out.println("<><><><><><><><><><><><><><><><><><><><><><><><><><><><><><>");	
		System.out.println("Total of Comments: " + Variaveis.countTotalComments);
		System.out.println("Total of Valid Comments: " + Variaveis.countVaideComments);
		System.out.println("Total of Discated Comments: " + Variaveis.countTotalDiscartedComments);
		
		return true;
	}

	/**Obtém a extenção de um determinado arquivo
	 * 
	 * @param fileName - Nome do arquivo a ser analisado
	 * @return
	 */
	private static String getFileExtension(String fileName) {
		String extension = "";

		int i = fileName.lastIndexOf('.');
		int p = Math.max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'));

		if (i > p) {
			extension = fileName.substring(i+1);
		} 

		return extension;
	}

	public static StringBuilder extraiInfos(InputStreamReader isr, File file) throws Exception{
		//Imprime na interface o que está acontecendo
		Document doc = Variaveis.docEditExtracting;
		doc.insertString(doc.getLength(), "\n" + "FILE> " + file.toString(), null);
		
		BufferedReader br = new BufferedReader(isr);
		String line;
		
		//Zera as informações sobre metodo e classe para os futuros comentários
		//Lembrando que isso é feito para todos arquivos de codigo .java por exemplo
		int cont = 0;
		ArrayList<String> idClasses = new ArrayList<String>();
		int idMethod = -1;
		String classe = "", metodo = "";
		StringBuilder builderComments = new StringBuilder();

		//Enquanto houver conteúdo no arquivo
		while ((line = br.readLine()) != null) {
			String comentario;
			cont++;
			
			//contTotalComments++; //Contanto quantos comentários analisamos.
			//Variaveis.countTotalComments++;

			//Se a marcação da declaração de uma classe for encontrada adicionamos ao array
			if (line.contains("<class>") && !line.contains("new")) {
				classe = Funcoes.tratarClasse(line);
				idClasses.add(classe);
			}

			//Se a marcação da declaração de um metodo for encontrado adicionamos ao array
			if (line.contains("<function>")) {
				metodo = Funcoes.tratarMetodo(line);
			}

			//Se a marcação da declaração de um comentário for encontrado adicionamos ao array
			if (line.contains("<comment")) {
				
				//Contando quantos comentários temos
				Variaveis.countTotalComments++;
				
				//Obtemos a primeira linha do comentário
				String aux = line.substring(line.indexOf("<comment"), line.length());
				comentario = Funcoes.tratarComentario(aux);

				//Se este ainda não terminou na proxima linha
				if (!(line.contains("</comment>"))) {
					//Continuamos a leitura do mesmo comentário de várias linhas
					while (((line = br.readLine()) != null) && !(line.contains("</comment>"))) {
						cont++;
						comentario = comentario + "\n" + Funcoes.tratarComentario(line);
					}
					//Imprimimos na interface
					doc.insertString(doc.getLength(),"\n" + comentario, null);
					comentario = comentario + Funcoes.tratarComentario(line);
				}
				//Preprocessamento do comentário extraído. Verifica se é de licença
				comentario = Preprocessing.preprocess(comentario);

				//Se após o preprocessamento este ainda é válido
				if (comentario.length() > 0) {
					
					//Contando os comentários válidos
					Variaveis.countVaideComments++;
					
					//Verificamos se a classe já está inserida na base
					int idClass = ClassDataSource.upsertClass(classe, file.toString());
					doc.insertString(doc.getLength(),"\n" + classe, null);

					//Verificamos se o metodo já está inserido
					idMethod = MethodDataSource.upsertMethod(metodo, idClass);
					doc.insertString(doc.getLength(),"\n" + metodo, null);

					//Verifica se o comentário já está inserido
					doc.insertString(doc.getLength(),"\n" + comentario, null);
					int idComment = CommentDataSource.getComment("COMMENT", PostgreSQLJDBC.treatReadDB(comentario), true, Variaveis.idProject);

					//Um comentário igual já foi inserido
					if (idComment > 0) {
						//Este está dentro de uma classe
						if (idClasses.size() > 0) {
							//e de um metodo
							if (idMethod > 0) {
								builderComments.append("INSERT INTO LINKED_COMMENTS (IDCOMMENT, IDMETHOD, IDCLASS, PATH) "
										+ "VALUES (" + idComment + ", " + idMethod + ", " + idClass + ", '" + 
										file.getAbsolutePath() + "');");
							} else { //O comentário tem classe apenas
								builderComments.append("INSERT INTO LINKED_COMMENTS (IDCOMMENT, IDMETHOD, PATH, IDCLASS) "
										+ "VALUES (" + idComment + ", NULL, '" +
										file.getAbsolutePath() + "', " + idClass + ");");
							}

						} else { //O comentário não tem classe nem metodo
							builderComments.append("INSERT INTO LINKED_COMMENTS (IDCOMMENT, PATH) "
									+ "VALUES (" + idComment + ", '" + file.getAbsolutePath() + "');");	
						}
						//Um comentário igual ainda não existe
					} else {
						if (idClasses.size() > 0) {
							//O comentário tem classe e método
							if (idMethod > 0) {
								//Criamos uma string com os inserts para melhorar o tempo de execução
								builderComments.append("INSERT INTO COMMENTS (COMMENT, IDMETHOD, IDCLASS, STEAMED_COMMENT, PATH, IDPROJECT, POSTAGGED, HASFLAG) "
										+ "VALUES ('" + PostgreSQLJDBC.treatStoreDB(comentario) + "', " + 
										idMethod + ", " + idClass + ", '', '" + file.getAbsolutePath() + "', " +
										Variaveis.idProject + ", '" + PostgreSQLJDBC.treatStoreDB(Variaveis.tagger.tag(comentario)) + "', " + 
										Preprocessing.verifySpecialWords(comentario) + ");");
							} else { //O comentário tem classe apenas
								builderComments.append("INSERT INTO COMMENTS (COMMENT, IDMETHOD, STEAMED_COMMENT, PATH, IDCLASS, IDPROJECT, POSTAGGED, HASFLAG) "
										+ "VALUES ('" + PostgreSQLJDBC.treatStoreDB(comentario) + "', " + "NULL" + ", '', '" +
										file.getAbsolutePath() + "', " + idClass + ", " + Variaveis.idProject + ", '" + 
										PostgreSQLJDBC.treatStoreDB(Variaveis.tagger.tag(comentario)) + "', " +  
										Preprocessing.verifySpecialWords(comentario) + ");");
							}
						} else { //O comentário não tem classe nem metodo
							builderComments.append("INSERT INTO COMMENTS (COMMENT, STEAMED_COMMENT, PATH, IDPROJECT, POSTAGGED, HASFLAG) "
									+ "VALUES ('" + PostgreSQLJDBC.treatStoreDB(comentario) + "', '', '" + file.getAbsolutePath() + "', " +
									Variaveis.idProject + ", '" + PostgreSQLJDBC.treatStoreDB(Variaveis.tagger.tag(comentario)) + "', " +  
									Preprocessing.verifySpecialWords(comentario) + ");");
						}
					}
				}
			}

			//Se a classe já acabou removemos do array
			if (line.contains("</class>") && idClasses.size() > 0 && !(line.contains("</class></expr>"))) {
				idClasses.remove(idClasses.size() -1);
			}

			//Se o metodo já acabou zeramos a informação
			if (line.contains("</function>")) {
				idMethod = -1;
			}
		}
		return builderComments;
	}
	
	//Utiliza lucene para analisar os comentarios contra os padroes
	public static StringBuilder findPatterns() throws ClassNotFoundException, SQLException {
		StringBuilder builderPattern = new StringBuilder();
	    LevensteinDistance distance = new LevensteinDistance();
	    
	    ArrayList<String> comentarios = CommentDataSource.getComments(Variaveis.idProject);
	    for (String comentario : comentarios) {
	    	for (int i = 0; i < Variaveis.padroes.size(); i++) {
				double distancia = distance.getDistance(comentario, Variaveis.padroes.get(i));
				//TODO Colocar na interface a possibilidade de alterar esse valor
				if (distancia > 0.25) {
					int idComment = CommentDataSource.getComment("COMMENT", PostgreSQLJDBC.treatReadDB(comentario), true, Variaveis.idProject);

					builderPattern.append("INSERT INTO PATTERN_COMMENT (IDCOMMENT, PROXIMITY) "
							+ "VALUES (" + idComment + ", " + distancia + ");");
				}
			}
		}
	    
		return builderPattern;
	}
	
	/*public static StringBuilder findPatternOnDB(String pattern) throws Exception{
		
	}*/
}
