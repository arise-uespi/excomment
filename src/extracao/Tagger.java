package extracao;

import java.io.IOException;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;
/** Classe para fazer o Part of speech
 * @author AndreBTS
 *
 */
public class Tagger {
	MaxentTagger tagger;
	public Tagger(String path) {
		tagger = new MaxentTagger(path);
	}
	
	public String tag(String toTag) throws IOException, ClassNotFoundException{
		String tagged = tagger.tagString(toTag);
		return tagged;
	}
}
