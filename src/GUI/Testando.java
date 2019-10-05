package GUI;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class Testando {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		final MaxentTagger tagger = new MaxentTagger("taggers/english-left3words-distsim.tagger");
		
		final String sample1 = "This is a simple text";
		final String sample2 = "The sailor";
		
		final String tagged1 = tagger.tagString(sample1);
		final String tagged2 = tagger.tagString(sample2);
	}

}
