package preprocessamento;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import suporte.Variaveis;

/**Classe para fazer o preprocessamento de acordo
 * com as informações dadas nas listas da tela incial
 * e armazenadas em arquivos
 * @author AndreBTS
 *
 */

public class Preprocessing {
	public static ArrayList<Palavra> palavras = new ArrayList<Palavra>();

	static ArrayList<String> keyWords = new ArrayList<String>();

	private String grama = null;

	public static String removeChars(String text) {
		return text;
	}

	public static String filterSpecial(String text) {
		for (int i = 0; i < Variaveis.caracteres.size(); i++) {
			if (text != null) {
				text = text.replace(Variaveis.caracteres.get(i), "").trim();
			} else text = "";
		}
		return text;
	}

	public static String filter(String text) {
		//Verifica se tem palavras coringa
		if (text != null && !verifySpecialWords(text)) {

			//Obtém a quantidade e o tamanho das palavras no comentário
			String aux[] = text.split(" ");
			int cont = 0;
			for (int i = 0; i < aux.length; i++) {
				//se as palavras tem mais de 2 letras são validas
				if (aux[i].length() > 2) {
					cont++;
				}
			}

			//Se não houver coringas verifica-se se é um comentario de license ou codigo
			if (!(cont > 2) || verifyLicense(text) || verifyCode(text)){
				text = "";
				System.out.println("É licença");
				Variaveis.countTotalDiscartedComments++;
			}
		}

		return text;
	}

	//Verifica se é um comentário de licensa
	private static boolean verifyLicense(String text){
		String aux = filterSpecial(text.replaceAll("\\r|\\n", "").toLowerCase());
		int cont = 0;

		for (int i = 0; i < Variaveis.licencas.size(); i++) {
			if (aux.contains(Variaveis.licencas.get(i).toLowerCase())) {
				cont++;

			//	System.out.println("--------------------------------------------------");
			//	System.out.println("Licença: "  + Variaveis.licencas.get(i).toLowerCase());
				//int x = System.in.read(); // TODO: remover depois;				
			}
		}
		//System.out.println("Cont de licença: " + cont);

		//Se nao conter ao menos 3 palavras de licensa não consideramos um comentario de licensa
		return (cont > 2);
	}

	public static boolean verifySpecialWords(String text) {
		for (int i = 0; i < Variaveis.coringas.size(); i++) {
			if (text.toLowerCase().contains(Variaveis.coringas.get(i).toLowerCase())) {
				return true;
			}
		}
		return false;
	}

	private static boolean verifyCode(String text) {
		//		text = text.replaceAll("\r", "");
		//		text = text.replaceAll("\n", "");

		String patterns[] = {
				"try.*\\{.*?\\}",
				"catch.*\\{.*?\\}",
				"finally.*\\{.*?\\}",
				"for.*?\\{.*?\\}",
				"while.*\\{.*?\\}",
				"if.*\\(.*?\\)",
				"else.*\\{.*?\\}",
				"\\w+ \\w+ = new.*?\\;",
				" \\w+\\(.*?\\)",
				" \\w+\\..*?\\(.*?\\)",
				"\\w+ \\w+ = \\w+.*?\\;",
				"\\w+ = \\w+.*?\\;"
		};

		String aux[] = removeCode(text, patterns).split(" ");
		int cont = 0;
		for (int i = 0; i < aux.length; i++) {
			if (aux[i].length() > 3) {
				cont++;
			}
		}

		//Se for um comentario somente de codigo o cont < 3 entao retornamos true
		return !(cont > 2);
	}

	public static String removeCode(String text, String[] patterns){
		Pattern pattern;
		Matcher matcher;
		for (int i = 0; i < patterns.length; i++) {
			pattern = Pattern.compile(patterns[i], Pattern.CASE_INSENSITIVE | Pattern.DOTALL | Pattern.MULTILINE);
			matcher = pattern.matcher(text);
			while (matcher.find()) {
				text = text.substring(0,matcher.start()) + text.substring(matcher.end(), text.length());
				matcher = pattern.matcher(text);
			}
		}

		//Retira os especiais
		text = filterSpecial(text);
		text = removeStopWords(text);

		return text;
	}

	private static String removeStopWords(String text) {
		//Não sendo removemos as stopWords
		for (int i = 0; i < Variaveis.palavrasReservadas.size(); i++) {
			text = text.toLowerCase().replace(Variaveis.palavrasReservadas.get(i).toLowerCase(), "").trim();
		}
		return text;
	}

	public static boolean CountWord(String text)
	{
		boolean result = false;

		String[] words;
		words = text.split(" ");  

		for (int i=0; i < words.length; i++)
		{
			int match = -1;
			if (palavras.size() > 0) {
				for (int j = 0; j < palavras.size(); j++) {
					if (palavras.get(j).palavra.toUpperCase().equals(words[i].toUpperCase())) {
						match = j;
						break;
					} else {
						match = -1;
					}
				}

				if (!(match > -1)) {
					palavras.add(new Palavra(words[i], 1));
				} else {
					palavras.get(match).qtde = palavras.get(match).qtde + 1;
				}

			} else
				palavras.add(new Palavra(words[i], 1));
		}

		return result;
	}

	public static String printWords() {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < palavras.size(); i++) {
			buffer.append(palavras.get(i).toString() + " \n");
			System.out.println(palavras.get(i).toString());
		}
		return buffer.toString();
	}

	public static String preprocess(String text) {
		
		
		text = Preprocessing.removeChars(text);
		
		//System.out.println("<------------------------------------------>");
		//System.out.println("Comentário: " + text);
		
		//Usar o codigo abaixo se quiser que nao remova-se permanentemente os caracteres especiais
		//		String aux = Preprocessing.filterSpecial(text);
		String aux = Preprocessing.filter(text);
		//System.out.println("Tradado licença: " + aux);
		//System.out.println("<------------------------------------------>");
		

		/*String[] array = aux.split(" ");

		int size = array.length;
		for (int i = 0; i < array.length; i++) {
			if (!(array[i].length() > 2)) {
				size--;
			} else {
				if (array[i].endsWith(".java")) {
					aux.replace(array[i], "");
					size--;
				}
			}
		}*/

		//O codigo abaixo serve para verificar na documentacao oracle todas as classes e remover o nome delas
		//		readURL("https://docs.oracle.com/javase/7/docs/api/allclasses-noframe.html");

		//O codigo abaixo serve para contar as palavras encontradas e verificar as que mais aparecem
		//		Preprocessing.CountWord(aux);

		//TODO Implementar a heuristica de eliminar comentarios com somente codigo aqui
		//Por enquanto utilizando 2 (sujeito e verbo) como heuristica
		if (aux != null && aux.length() > 0) {
			return text;
		} else {
			return "";
		}
	}

	public static void readURL(String URL) throws IOException {
		URL oracle = new URL(URL);
		BufferedReader in = new BufferedReader(
				new InputStreamReader(oracle.openStream()));

		String inputLine;
		while ((inputLine = in.readLine()) != null) {
			if (inputLine.contains("<li>")) {
				String aux = inputLine.substring(inputLine.lastIndexOf("\">") + 2, inputLine.lastIndexOf("</a>"));
				if (inputLine.contains("<i>")) {
					aux = aux.replace("<i>", "").replace("</i>", "");
				}
				keyWords.add(aux);
			}
		}
		in.close();
	}
}
