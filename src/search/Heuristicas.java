package search;

import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.tartarus.snowball.ext.PorterStemmer;

public class Heuristicas {
	//Chamado no MyFilter para retirar caracteres especiais dos comentarios. 
	public static void retirarCaracteres(CharTermAttribute charTermAttr){
		int length = charTermAttr.length();
		if(length >=3){
			char[] buffer = charTermAttr.buffer();
			char[] newBuffer = new char[length];
			int cont=0;
			for (int i = 0; i < length; i++) {
				//System.out.print(buffer[i] + "-");
				if(buffer[i] == '*' || buffer[i] == '@' || buffer[i] == '\'' || buffer[i] == '\"' || buffer[i] == '*' || buffer[i] == '/' || buffer[i] == ',' || buffer[i] == '.' || buffer[i] == ':' || buffer[i] == ')' || buffer[i] == '('){
					//System.out.print("Achou");
				}else{
					newBuffer[cont] = buffer[i];
					cont++;
				}
			}
			String token = new String(newBuffer);
			token = token.trim();
			char[] newBuffer2 = new char[token.length()];
			newBuffer2 = token.toCharArray();
			charTermAttr.setEmpty();
			charTermAttr.copyBuffer(newBuffer2, 0, newBuffer2.length);
		}
	}
	
	public static String retirarCaracteres(String tok){
		int length = tok.length();
		if(length >=3){
			tok = tok.replaceAll("[^a-zA-Z1-9 ]", "");
        	tok = tok.trim();
        	tok = tok.toLowerCase();
		}
		return tok;
	}
	
	public static boolean checkStem(String padrao, String tok){
		PorterStemmer stemTerm = new PorterStemmer();
		stemTerm.setCurrent(tok);
		stemTerm.stem();
		String stemTok = stemTerm.getCurrent();
		stemTerm.setCurrent(padrao);
		stemTerm.stem();
		String stemDic = stemTerm.getCurrent();
		if (stemDic.equalsIgnoreCase(stemTok)){
			return true;
		}else{
			return false;
		}
	}
	
	public static boolean checkSufix(String padrao, String tok){
		String suffixDic = padrao.substring(padrao.length()-2, padrao.length());
		String suffixTok = tok.substring(tok.length()-2, tok.length());
		if(suffixTok.equalsIgnoreCase("ed")){
			if(suffixDic.equalsIgnoreCase("ed")){
				return true;
			}else{
				return false;
			}
		}else{
			return true;
		}
	}
}
