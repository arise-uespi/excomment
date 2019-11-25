package database;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.StringTokenizer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;



import org.omg.PortableServer.ID_ASSIGNMENT_POLICY_ID;

import extracao.ExtractPatterns;
import IO.ManageFiles;
import suporte.Constantes;
import suporte.Variaveis;

/**
 * Classe para deixar o banco no estado necessário com as tabelas e colunas
 * necessárias. Roda também os updates.
 * 
 * @author AndreBTS
 *
 */
public class PostgreSQLJDBC {
	public static Connection connection = null;

	public PostgreSQLJDBC() throws ClassNotFoundException, SQLException, IOException {
		int versao = -1;
		try {
			versao = getConfigValue("version");

			open();
			Statement stmt = null;

			stmt = connection.createStatement();

			String sql = "SELECT * FROM comments LIMIT 1; " + "SELECT * FROM linked_comments LIMIT 1;"
					+ "SELECT * FROM methods LIMIT 1; " + "SELECT * FROM classes LIMIT 1;"
					+ "SELECT * FROM projects LIMIT 1;";
			stmt.execute(sql);

			stmt.close();
			close();
			update(versao);
			
			//System.out.println("==== APAGAR =====");
			//ExtractPatterns.extractRelPatterns();
		} catch (Exception e) {
			try {
				update(versao);
			} catch (Exception e2) {
				createDB();
				//savePatternsOnDB(); // Get Patterns from file and save on DB
			}
		}
	}

	// Method to create the tables in the database, the database should be
	// created before
	public void createDB() throws ClassNotFoundException, SQLException {
		connection = null;
		Statement stmt = null;

		open();
		stmt = connection.createStatement();

		String sql = "CREATE TABLE IF NOT EXISTS PROJECTS " + "(ID 		SERIAL PRIMARY KEY,"
				+ " NAME 		VARCHAR(500) NOT NULL);";

		sql += "CREATE TABLE IF NOT EXISTS CLASSES " + "(ID 		SERIAL PRIMARY KEY,"
				+ " NAME		VARCHAR(500) NOT NULL," + " PATH		TEXT NOT NULL, "
				+ " IDPROJECT INTEGER REFERENCES PROJECTS (ID));";

		sql += "CREATE TABLE IF NOT EXISTS METHODS " + "(ID 		SERIAL PRIMARY KEY,"
				+ " NAME		VARCHAR(500) NOT NULL, " + " IDCLASS INTEGER REFERENCES CLASSES (ID));";

		sql += "CREATE TABLE IF NOT EXISTS COMMENTS " + "(ID 		SERIAL PRIMARY KEY,"
				+ " COMMENT   TEXT    NOT NULL, " + " STEAMED_COMMENT   TEXT    NOT NULL, "
				+ " IDMETHOD  INTEGER REFERENCES METHODS (ID)," + " IDCLASS   INTEGER REFERENCES CLASSES (ID),"
				+ " HASFLAG   BOOLEAN NOT NULL DEFAULT FALSE," + " POSTAGGED TEXT," + " PATH		TEXT NOT NULL);";

		sql += "CREATE TABLE IF NOT EXISTS LINKED_COMMENTS " + "(ID 		SERIAL PRIMARY KEY,"
				+ " IDCOMMENT INTEGER REFERENCES COMMENTS (ID), " + " IDMETHOD  INTEGER REFERENCES METHODS (ID),"
				+ " IDCLASS   INTEGER REFERENCES CLASSES (ID)," + " PATH		TEXT NOT NULL);";

		sql += "CREATE TABLE IF NOT EXISTS CONFIG " + "(KEY 		TEXT PRIMARY KEY," + " VALUE 	INTEGER NOT NULL);";

		sql += "CREATE TABLE IF NOT EXISTS PATTERN " + "(ID 		SERIAL PRIMARY KEY," + " PATTERN 	TEXT NOT NULL,"
				+ " LENGTH INTEGER, LEVEL INTEGER,IDORIGINAL INTEGER,SCORE FLOAT, CLASSE TEXT);";

		sql += "CREATE TABLE IF NOT EXISTS PATTERN_COMMENT " + "(ID 		SERIAL PRIMARY KEY,"
				+ " IDCOMMENT INTEGER REFERENCES COMMENTS (ID), " + " IDPATTERN INTEGER REFERENCES PATTERN (ID),"
				+ " PROXIMITY NUMERIC(13,4) NOT NULL DEFAULT 0," + " ISLINKED  BOOLEAN DEFAULT FALSE);";

		sql += "CREATE TABLE IF NOT EXISTS REL_PATTERNS " + "(ID 		SERIAL PRIMARY KEY,"
				+ " IDPATTERN INTEGER REFERENCES PATTERN (ID),"
				+ " IDPARENT INTEGER REFERENCES PATTERN (ID)); ";
		
		sql += "CREATE TABLE IF NOT EXISTS THEME " + "(ID 	SERIAL PRIMARY KEY,"
				+ " THEME TEXT NOT NULL);";	
				
		sql += "CREATE TABLE IF NOT EXISTS PATTERN_THEME " + "(ID SERIAL PRIMARY KEY,"
				+ " IDPATTERN INTEGER REFERENCES PATTERN (ID),"
				+ " IDTHEME INTEGER REFERENCES THEME (ID));";
			
		sql += "CREATE TABLE IF NOT EXISTS TDTYPE " + "(ID 	SERIAL PRIMARY KEY,"
				+ " TDTYPE TEXT NOT NULL);";	
		
		sql += "CREATE TABLE IF NOT EXISTS THEME_TDTYPE " + "(ID SERIAL PRIMARY KEY,"
				+ " IDTHEME INTEGER REFERENCES THEME (ID),"
				+ " IDTDTYPE INTEGER REFERENCES TDTYPE (ID));";
		
		sql += "UPDATE config SET value = 5 where key = 'version';";

		stmt.executeUpdate(sql);
		stmt.close();

		try {
			stmt = connection.createStatement();
			sql = "INSERT INTO CONFIG VALUES ('version', 5);";
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (Exception e) {
			// Valor version já inserido
		}

		int versao = getConfigValue("version");
		update(versao);

		close();
	}
    /*
     * Método desenvolvido para carregar os vacabulários e sinônimos de palavras 
     * que identificam DT's ao carregar pela primeira vez o projeto onde na 
     * tabela CONFIG no BD do postgres o key version tem como value igual a -1
     */	
	public static void savePatternsOnDB() throws IOException, ClassNotFoundException, SQLException {
		
		/* Método criado para gerar os sinônimos dos vocabulários em Inglês 
		 * oriundas do arquivo padroesselecionados.txt*/
		//sinonimoIngles();

	}
	
	//Metodo que traz os Sinonimos dos Sinomimos. 
	//Precisa melhorar depois pois e praticamente uma copia do metodo sinonimosIngles. 
	private static void getSinonimo(String padrao, int idPadraoOriginal, boolean TODO) throws IOException{
		//Usarei para passar ao algoritmo que procura a classe gramatical no Json
		//O padrao Precisa ser classificado corretamente ou podera vir padroes fora de contexto nos sinonimos.
		String classeGramatical = "adverb"; 

		PatternDataSource patternDataSource;
		patternDataSource = new PatternDataSource();
		URL serverAddress = new URL(
				"http://words.bighugelabs.com/api/2/e18e48b3d3217010cbc7c3d1a73dfe70/" + padrao
						+ "/json");

		HttpURLConnection connection = (HttpURLConnection) serverAddress.openConnection();
		int rc = -1;
		do {
			try {
				connection.connect();
				rc = connection.getResponseCode();

				String line = null;
				//verifico se a execução da pesquisa retornou 
				//erro de página não encontrada, em caso negativo
				//entra no método e extrai os sinonimos, caso encontre 
				//erro passa para o próximo termo
				if (rc != 404) {
					BufferedReader br = new BufferedReader(
							new java.io.InputStreamReader(connection.getInputStream()));
					StringBuilder sb = new StringBuilder();
					while ((line = br.readLine()) != null){
						sb.append(line + '\n');
					}
					JSONObject obj = (JSONObject) JSONValue.parse(sb.toString());
					if (obj != null) {
						//Extraindo sinônimo do termo passado relacionado a Classe Gramatical Desejada 
						if (obj.get(classeGramatical) != null) {
							JSONObject noun = (JSONObject) obj.get(classeGramatical);
							JSONArray syn = (JSONArray) noun.get("syn");
							System.out.println("Sinônimo Associado a " + classeGramatical + " de " + padrao);
							int i = 0;
							int cont = 1;
							//Limito a pegar apenas até 3 sinônimos do termo passado
							//Verifique que poderíamos criar uma forma de extender e
							//passarmos a pegar sinônimos em níveis mais profuntos não
							//limitando ao primeiro nível apenas do termo passado
							//podendo ser uma melhoria
							while (i < syn.size() && cont <= 3) {
								System.out.println(syn.get(i));
								//Aqui se o padrao for do tipo 'TODO: Ugrama' devo tratar para evitar duplicacoes
								if(!TODO){
									if (!PatternDataSource.isPattern(syn.get(i).toString())) {
										//verifico se o termo já está contido na base de dados para evitar
										//duplicidade de vocabulários, podemos criar uma chave unica 
										//retirando esse trecho
										float score=0;//TODO: Precisa definir o valor do score igual ao do original
										String classe="";//TODO: Precisa definir quais serao as classes
										patternDataSource.insertPattern(syn.get(i).toString(), syn.get(i).toString().split(" ").length, 3, idPadraoOriginal, score, classe);
									}	
								}else{
									if (!PatternDataSource.isPattern("TODO: "+syn.get(i).toString())) {
										//verifico se o termo já está contido na base de dados para evitar
										//duplicidade de vocabulários, podemos criar uma chave unica 
										//retirando esse trecho
										float score=0;//TODO: Precisa definir o valor do score igual ao do original
										String classe="";//TODO: Precisa definir quais serao as classes
										patternDataSource.insertPattern("TODO: " + syn.get(i).toString(), syn.get(i).toString().split(" ").length, 3, idPadraoOriginal, score, classe);
									}			
								}
								cont++;
								i++;
							}
						}
					}
				}
			} catch (Exception e) {

			}
		} while (rc != 200 && rc != 404);
			connection.disconnect();
	}
	

	private static void sinonimoIngles() throws ClassNotFoundException, SQLException {
		String classeGramatical = "adverb";
		int idPadraoOriginal = -1; // Sera inserido no campo idOriginal nos sinonimos
		try {
			ManageFiles m = new ManageFiles();
			//Carrega a lista de vocabulários em inglês dispostas no arquivo txt
			Variaveis.padroesSelecionados = m.readFileSeparateLines(Constantes.PADROESSELECIONADOS);
			PatternDataSource patternDataSource;
            //Laço que pega expressão - linha a linha
			for (String pattern : Variaveis.padroesSelecionados) {
				patternDataSource = new PatternDataSource();
				//Expressão utilizada para tokenizar palavras separadas por espaço em branco
				//para evitar erros na hora da pesquisa
				String[] words = pattern.split(" ");
				//Verifica se termo já existe no BD
				if (!PatternDataSource.isPattern(pattern)) {
					//Insere o nivel 1 e o padrao -1 pois e o padrao original
					String classe="";//TODO: Precisa definir quais serao as classes
					float score=0;//TODO: Precisa definir o valor do score igual ao do original
					patternDataSource.insertPattern(pattern, words.length, 1, -1, score, classe); 
					//Retorna o id do Padrao que acabei de inserir para usar nos sinonimos
					idPadraoOriginal = patternDataSource.getIdPattern(pattern);
				}			
			//APENAS DOS UGRAMS			
			if (words.length == 1){
				//Substituo 'barra' por espaço em branco e tokenizo 
				//para evitar erros na hora da pesquisa
				StringTokenizer token = new StringTokenizer(pattern.replaceAll("/", " "), " ");
				int qtd = 1;
				//Se limito a pegar apenas 3 termos, porém isso pode ser configurável
				while (token.hasMoreTokens() && qtd <= 3) {
					String patternToken = token.nextToken().trim();
					//Mais uma vez mando substituir para evitar erros na 
					//pesquisa pelo sinônimo já que não é aceitado caracteres 
					//especiais como os listados abaixo
					patternToken = patternToken.replaceAll(",", "");
					patternToken = patternToken.replaceAll("\\?", "");
					patternToken = patternToken.replaceAll("!", "");
					patternToken = patternToken.replaceAll(":", "");
					if (patternToken != null && !patternToken.trim().equals("")) {
						//URL utilizada com uma key que foi gerado após 
						//cadastro realizado no referido site, passando 
						//o termo corrente e retorno uma JSON com seu 
						//respectivo sinônimo que pode ser de substantivo, de verbo, etc
						URL serverAddress = new URL(
								"http://words.bighugelabs.com/api/2/e18e48b3d3217010cbc7c3d1a73dfe70/" + patternToken
										+ "/json");

						HttpURLConnection connection = (HttpURLConnection) serverAddress.openConnection();
						int rc = -1;
						do {
							try {
								connection.connect();
								rc = connection.getResponseCode();

								String line = null;
								//verifico se a execução da pesquisa retornou 
								//erro de página não encontrada, em caso negativo
								//entra no método e extrai os sinonimos, caso encontre 
								//erro passa para o próximo termo
								if (rc != 404) {
									BufferedReader br = new BufferedReader(
											new java.io.InputStreamReader(connection.getInputStream()));
									StringBuilder sb = new StringBuilder();
									while ((line = br.readLine()) != null)
										sb.append(line + '\n');
									JSONObject obj = (JSONObject) JSONValue.parse(sb.toString());
									if (obj != null) {
										//Extraindo sinônimo do termo passado relacionado a substantivo 
										if (obj.get(classeGramatical) != null) {
											JSONObject noun = (JSONObject) obj.get(classeGramatical);
											JSONArray syn = (JSONArray) noun.get("syn");
											System.out.println("Sinônimo Associado a" + classeGramatical + " de " + patternToken);
											int i = 0;
											int cont = 1;
											//Limito a pegar apenas até 3 sinônimos do termo passado
											//Verifique que poderíamos criar uma forma de extender e
											//passarmos a pegar sinônimos em níveis mais profuntos não
											//limitando ao primeiro nível apenas do termo passado
											//podendo ser uma melhoria
											while (i < syn.size() && cont <= 3) {
												System.out.println(syn.get(i));
												if (!PatternDataSource.isPattern(syn.get(i).toString())) {
													//verifico se o termo já está contido na base de dados para evitar
													//duplicidade de vocabulários, podemos criar uma chave unica 
													//retirando esse trecho
													float score=0; //TODO: Precisa definir o score baseado no padrao original. 
													String classe="";//TODO: Precisa definir quais serao as classes
													patternDataSource.insertPattern(syn.get(i).toString(),syn.get(i).toString().split(" ").length, 2, idPadraoOriginal, score, classe);
													getSinonimo(syn.get(i).toString(), idPadraoOriginal, false);
													cont++;
												}
												i++;
											}
										}
									}
									System.out.println("----------------------------------------");
								}
							} catch (Exception e) {

							}
						} while (rc != 200 && rc != 404);
						connection.disconnect();
					}
					qtd++;
				}
			//Else do If que pega sinonimos apenas UGRAMS. 	
			}else{
			//Tratar os Padroes que sao da Tag TODO
				int qtd = 1;
				//while ( qtd <= 3) { 
				if(words[0].equalsIgnoreCase("TODO:")){
					words[1] = words[1].replaceAll(",", "");
					words[1] = words[1].replaceAll("\\?", "");
					words[1] = words[1].replaceAll("!", "");
					words[1] = words[1].replaceAll(":", "");
						//Se o que tiver depois do TODO for Ugrama
						if (words.length == 2){ 
							URL serverAddress = new URL(
									"http://words.bighugelabs.com/api/2/e18e48b3d3217010cbc7c3d1a73dfe70/" + words[1]
											+ "/json");
	
							HttpURLConnection connection = (HttpURLConnection) serverAddress.openConnection();
							int rc = -1;
							do {
								try {
									connection.connect();
									rc = connection.getResponseCode();
									String line = null;
									//verifico se a execução da pesquisa retornou 
									//erro de página não encontrada, em caso negativo
									//entra no método e extrai os sinonimos, caso encontre 
									//erro passa para o próximo termo
									if (rc != 404) {
										BufferedReader br = new BufferedReader(
												new java.io.InputStreamReader(connection.getInputStream()));
										StringBuilder sb = new StringBuilder();
										while ((line = br.readLine()) != null)
											sb.append(line + '\n');
										JSONObject obj = (JSONObject) JSONValue.parse(sb.toString());
										if (obj != null) {
											//Extraindo sinônimo do termo passado relacionado a substantivo 
											if (obj.get(classeGramatical) != null) {
												JSONObject noun = (JSONObject) obj.get(classeGramatical);
												JSONArray syn = (JSONArray) noun.get("syn");
												System.out.println("Sinônimo Associado a " + classeGramatical + " de " + words[1]);
												int i = 0;
												int cont = 1;
												//Limito a pegar apenas até 3 sinônimos do termo passado
												//Verifique que poderíamos criar uma forma de extender e
												//passarmos a pegar sinônimos em níveis mais profuntos não
												//limitando ao primeiro nível apenas do termo passado
												//podendo ser uma melhoria
												while (i < syn.size() && cont <= 3) {
													System.out.println(syn.get(i));
													if (!PatternDataSource.isPattern("TODO: "+syn.get(i).toString())) {
														//verifico se o termo já está contido na base de dados para evitar
														//duplicidade de vocabulários, podemos criar uma chave unica 
														//retirando esse trecho
														String padrao = "TODO: " + syn.get(i).toString();
														float score=0;//TODO: Precisa definir o valor do score igual ao do original
														String classe="";//TODO: Precisa definir quais serao as classes
														patternDataSource.insertPattern(padrao ,syn.get(i).toString().split(" ").length, 2, idPadraoOriginal, score, classe);
														getSinonimo(syn.get(i).toString(), idPadraoOriginal, true);
														cont++;
													}
													i++;
												}
											}
										}
										System.out.println("----------------------------------------");
									}
								
							} catch (Exception e) {

							}
						} while (rc != 200 && rc != 404);
							connection.disconnect();
						}
					}
					qtd++;
				}
			}
			//}
		} catch (java.net.MalformedURLException e) {
			e.printStackTrace();
		} catch (java.net.ProtocolException e) {
			e.printStackTrace();
		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
	}

	private static void sinonimoPortugues()
			throws IOException, ClassNotFoundException, SQLException, MalformedURLException {
		PatternDataSource patternDataSource;
		// Para Padrões em português
		ManageFiles m = new ManageFiles();
		//Carrega a lista de vocabulários em português dispostas no arquivo txt		
		Variaveis.padroesSelecionadosPortugues = m.readFileSeparateLines(Constantes.PADROESSELECIONADOSPORTUGUES);
        //Laço que pega expressão - linha a linha
		for (String padrao : Variaveis.padroesSelecionadosPortugues) {
			patternDataSource = new PatternDataSource();
			//Expressão utilizada para tokenizar palavras separadas por espaço em branco colocando-as num array de string
			//para evitar erros na hora da pesquisa
			String[] pad = padrao.split(" ");
			//Verifica se termo já existe no BD
			if (!PatternDataSource.isPattern(padrao)) {
				//patternDataSource.insertPattern(padrao, pad.length, 0);
			}
			//Link utilizado para passar o termo e retornar uma página HTML contendo conteúdo que podem ou não conter sinônimos
			URL sinonimo = new URL("http://www.sinonimos.com.br/" + padrao + "/");
			try {
				BufferedReader in = new BufferedReader(new InputStreamReader(sinonimo.openStream()));
				String inputLine;
				String conjuntoSinonimos = null;
				//M;etodo que faz uma varredura linha a linha da página até encontrar o meta og:description que é onde contém os sinônimos
				while ((inputLine = in.readLine()) != null) {
					// System.out.println(inputLine);
					if (inputLine.contains("og:description")) {
						//Pego através do trecho abaixo o conjunto de sinônimos após o 'dois-pontos' até a última 'vírgula' contida nessa meta tag
						conjuntoSinonimos = inputLine.substring(inputLine.lastIndexOf(":") + 1,
								inputLine.lastIndexOf(","));
						System.out.println(conjuntoSinonimos);
					}
				}
				in.close();
				//Se retornou conjunto de sinônimos a condição é satisfeita
				if (conjuntoSinonimos != null && !conjuntoSinonimos.equals("")) {
					//Pego sinônimo a sinônimo através da tokenização separando os termos por 'vírgula'
					StringTokenizer token = new StringTokenizer(conjuntoSinonimos, ",");
					String sinon = null;
					int qtd = 1;
					//Limito a pegar apenas até 3 sinônimos do termo passado
					//Verifique que poderíamos criar uma forma de extender e
					//passarmos a pegar sinônimos em níveis mais profuntos não
					//limitando ao primeiro nível apenas do termo passado
					while (token.hasMoreTokens() && qtd <= 3) {
						
						sinon = token.nextToken().trim();
						pad = sinon.split(" ");
						//verifico se o termo já está contido na base de dados para evitar
						//duplicidade de vocabulários, podemos criar uma chave unica 
						//retirando esse trecho
						if (!PatternDataSource.isPattern(sinon)) {
							//patternDataSource.insertPattern(sinon, pad.length, 0);
							qtd++;
						}
					}
				}
			} catch (Exception ex) {
				ex.getMessage();
			}
		}
	}

	// Create the connection to the database
	public static void open() throws ClassNotFoundException, SQLException {
		connection = null;

		Class.forName("org.postgresql.Driver");
		connection = DriverManager.getConnection(
				"jdbc:postgresql://" + Variaveis.host + ":" + Variaveis.port + "/" + Variaveis.database, Variaveis.user,
				Variaveis.password);
	}

	public static void open(String host, String port, String database, String user, String pass)
			throws ClassNotFoundException, SQLException {
		connection = null;

		Class.forName("org.postgresql.Driver");
		connection = DriverManager.getConnection("jdbc:postgresql://" + host + ":" + port + "/" + database, user, pass);
	}

	// Close and destroy the connection
	public static void close() throws SQLException {
		if (connection != null) {
			connection.close();
		}
		connection = null;
	}

	// Method to execute a generic SQL statement
	public int execSQL(String sql) throws SQLException, ClassNotFoundException {
		open();
		Statement stmt = null;
		int key = -1;

		stmt = connection.createStatement();
		stmt.execute(sql);
		/*
		 * stmt.execute(sql, stmt.RETURN_GENERATED_KEYS); ResultSet rs =
		 * stmt.getGeneratedKeys();
		 * 
		 * if ( rs.next() ) { // Retrieve the auto generated key(s). key =
		 * rs.getInt(1); }
		 */

		stmt.close();
		close();
		return key;
	}

	// Delete the data from all the tables
	private void deleteData() throws SQLException, ClassNotFoundException {
		open();
		Statement stmt = null;

		stmt = connection.createStatement();

		String sql = "DELETE FROM LINKED_COMMENTS; " + "DELETE FROM COMMENTS; " + "DELETE FROM METHODS; "
				+ "DELETE FROM CLASSES;" + "DELETE FROM PROJECTS;";
		stmt.executeUpdate(sql);

		stmt.close();
		close();
	}

	public static int getConfigValue(String key) throws SQLException, ClassNotFoundException {
		open();
		Statement stmt = null;
		int versao = -1;
		//System.out.println("Versao: " + versao);
		stmt = PostgreSQLJDBC.connection.createStatement();
		ResultSet rs = stmt.executeQuery("SELECT VALUE FROM CONFIG WHERE KEY = '" + key + "';");

		while (rs.next()) {
			versao = rs.getInt("value");
		}

		rs.close();
		stmt.close();
		PostgreSQLJDBC.close();

		return versao;
	}

	private void update(int versao) throws ClassNotFoundException, SQLException {
		Statement stmt = null;
		String sql = "";
		switch (versao) {
		case -1:
			createDB();
		case 1:
			connection = null;

			open();
			stmt = connection.createStatement();

			sql = "ALTER TABLE comments ADD COLUMN IDPROJECT INTEGER REFERENCES PROJECTS (ID);";
			sql += "UPDATE config SET value = 2 where key = 'version';";

			stmt.executeUpdate(sql);
			stmt.close();
			close();
			update(getConfigValue("version"));
			break;
		case 2:
			connection = null;

			open();
			stmt = connection.createStatement();

			sql = "ALTER TABLE comments ADD COLUMN postagged TEXT;";
			sql += "UPDATE config SET value = 3 where key = 'version';";

			stmt.executeUpdate(sql);
			stmt.close();
			close();
			update(getConfigValue("version"));
			break;
		case 3:
			connection = null;

			open();
			stmt = connection.createStatement();

			sql = "ALTER TABLE classes ADD COLUMN name VARCHAR(500);";
			sql = "ALTER TABLE methods ADD COLUMN name VARCHAR(500);";
			sql = "ALTER TABLE projects ADD COLUMN name VARCHAR(500);";
			sql += "UPDATE config SET value = 4 where key = 'version';";

			stmt.executeUpdate(sql);
			stmt.close();
			close();
			update(getConfigValue("version"));
			break;
		case 4:
			connection = null;

			open();
			stmt = connection.createStatement();

			sql += "CREATE TABLE IF NOT EXISTS PATTERN " + "(ID 		SERIAL PRIMARY KEY,"
					+ " PATTERN 	TEXT NOT NULL);";

			sql += "CREATE TABLE IF NOT EXISTS PATTERN_COMMENT " + "(ID 		SERIAL PRIMARY KEY,"
					+ " IDCOMMENT INTEGER REFERENCES COMMENTS (ID), " + " IDPATTERN INTEGER REFERENCES PATTERN (ID),"
					+ " PROXIMITY NUMERIC(13,4) NOT NULL DEFAULT 0," + " ISLINKED  BOOLEAN DEFAULT FALSE);";

			sql += "UPDATE config SET value = 5 where key = 'version';";

			stmt.executeUpdate(sql);
			stmt.close();
			close();
			update(getConfigValue("version"));
			break;
		default:
			break;
		}
	}

	// Treat the comment to store it in the database
	public static String treatStoreDB(String comment) {
		comment = comment.replace("'", "''");
		return comment;
	}

	// Treat the comment to store it in the database
	public static String treatReadDB(String comment) {
		comment = comment.replace("'", "''");
		comment = comment.replace("\\", "\\\\");
		return comment;
	}
}
