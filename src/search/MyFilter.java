package search;

import java.io.IOException;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

public class MyFilter extends TokenFilter{
	protected MyFilter(TokenStream ts) {
		super(ts);
		this.charTermAttr = addAttribute(CharTermAttribute.class);
	}

	private CharTermAttribute charTermAttr;

	@Override
	public boolean incrementToken() throws IOException {
		if (!input.incrementToken()) {
			return false;
		}
		
//		int length = charTermAttr.length();
//		if(length >=3){
//			char[] buffer = charTermAttr.buffer();
//			char[] newBuffer = new char[length];
//			int cont=0;
//			for (int i = 0; i < length; i++) {
//				//System.out.print(buffer[i] + "-");
//				Heuristicas.retirarCaracteres();
//				if(buffer[i] == '@' || buffer[i] == '\'' || buffer[i] == '\"' || buffer[i] == '*' || buffer[i] == '/' || buffer[i] == ',' || buffer[i] == '.' || buffer[i] == ':' || buffer[i] == ')' || buffer[i] == '('){
//					//System.out.print("Achou");
//				}else{
//					newBuffer[cont] = buffer[i];
//					cont++;
//				}
//			}
//			String token = new String(newBuffer);
//			token = token.trim();
//			char[] newBuffer2 = new char[token.length()];
//			newBuffer2 = token.toCharArray();
//			charTermAttr.setEmpty();
//			charTermAttr.copyBuffer(newBuffer2, 0, newBuffer2.length);
//			return true;
//		}else{
//			return true;
//		}
		Heuristicas.retirarCaracteres(charTermAttr);
		return true;
	}
}
