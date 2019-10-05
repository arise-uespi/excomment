package search;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.KeywordTokenizer;
import org.apache.lucene.analysis.core.LetterTokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.core.WhitespaceTokenizer;
import org.apache.lucene.analysis.miscellaneous.LengthFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.CharTokenizer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.spell.LevensteinDistance;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.util.packed.PackedInts.Reader;

import edu.stanford.nlp.ie.machinereading.common.SimpleTokenize;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.util.CoreMap;

public class MyAnalyzer extends Analyzer{	
	@Override
	protected TokenStreamComponents createComponents(String fieldName) {
	   final Tokenizer source = new WhitespaceTokenizer();    
	   TokenStream sink = new LowerCaseFilter(source);
	    //sink = new LengthFilter(sink, 3, Integer.MAX_VALUE);
	   final List<String> stopWords = Arrays.asList(
			   "an", "a", "at", "on", "in", "the"
	   );
	   final CharArraySet stopSet = new CharArraySet(stopWords, false); 
	   sink = new StopFilter(sink, stopSet);
	   sink = new MyFilter(sink);
	   return new TokenStreamComponents(source, sink);
	 }
}

