package search;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.queryparser.xml.FilterBuilderFactory;

public class MyFilterFactory extends FilterBuilderFactory{

	   public TokenStream create(TokenStream ts) {
	     return new MyFilter(ts);
	   }
	   
}
