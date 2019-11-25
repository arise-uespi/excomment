package preprocessamento;

import java.io.StringReader;
import java.util.StringTokenizer;
import org.apache.lucene.analysis.TokenStream;

/**Classe para fazer STEM
 * Atualmente não está sendo usada.
 * @author AndreBTS
 */
public class StemDictionary {
	private static String path_stopwords = "palavrasstem.txt";
	private static String grama = "u";

	public static String loadStems(String descriptor) throws Exception {
		String stem;
		StringReader sr = null;

		String type_equ = "U";
		int tamanhoStem = 0;
		int numStem;
		PorterStemAnalyzer ps = new PorterStemAnalyzer();
		ps.ReadFile(path_stopwords);

		descriptor = descriptor.replace("'", "\\'");
		sr = new StringReader(descriptor);
		TokenStream tokenstream = ps.tokenStream(null, sr);
		stem = PorterStemAnalyzer.displayTokenStream(tokenstream);
		StringTokenizer token = new StringTokenizer(stem);
		tamanhoStem = token.countTokens();
		numStem = 0;
		
		if (numStem > 0) {
			stem = null;
			type_equ = "U";
			tamanhoStem = 0;
		}
		else {
			stem = stem.trim();
			if (getGrama().equalsIgnoreCase("N")) {
				//Verificando se o termo continua N-Grama
				if (tamanhoStem > 1) type_equ = "N";
			}
		}
		return stem;
	}

	public static String getGrama() {
		return grama;
	}

	public void setGrama(String grama) {
		this.grama = grama;
	}
}
