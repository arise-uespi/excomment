package preprocessamento;
import java.io.*;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.LowerCaseTokenizer;
import org.apache.lucene.analysis.PorterStemFilter;

import java.io.Reader;
import java.util.HashSet;
//import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Set;
import java.util.Vector;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;

import java.io.*;

/**
 * PorterStemAnalyzer processes input
 * text by stemming English words to their roots.
 * This Analyzer also converts the input to lower case
 * and removes stop words.  A small set of default stop
 * words is defined in the STOP_WORDS
 * array, but a caller can specify an alternative set
 * of stop words by calling non-default constructor.
 */
public class PorterStemAnalyzer extends Analyzer
{
    //private static Hashtable _stopTable;
    Set stopWords = new HashSet();
   
   public void ReadFile(String path_stopwords) throws IOException
   {
	   FileInputStream fis = new FileInputStream(path_stopwords);
       InputStreamReader isr = new InputStreamReader(fis, "ISO-8859-1");
       BufferedReader in = new BufferedReader(isr);
       
	   ArrayList<String> Words =  new ArrayList<String>();
	   String lines = "";
	   while ((lines = in.readLine()) != null)
	   {
		   Words.add(lines);
	   }
	   
	   for (int i=0; i < Words.size(); i++)
       {          
         stopWords.add(Words.get(i));
       }
   }

    /**
     * Construtor .
     */
    public PorterStemAnalyzer()
    {
    	
    }

    /**
     * Builds an analyzer with the given stop words.
     *
     * @param stopWords a String array of stop words
     */
//    public PorterStemAnalyzer(Set stop)
//   {
       // stopTable = StopFilter.makeStopTable(stopWords);
 //        stopWords = StopFilter.makeStopTable(stop);
//    }

    /**
     * Processes the input by first converting it to
     * lower case, then by eliminating stop words, and
     * finally by performing Porter stemming on it.
     *
     * @param reader the Reader that
     *               provides access to the input text
     * @return an instance of TokenStream
     */
 //   public final TokenStream tokenStream(Reader reader)
 //   {
 //       return new PorterStemFilter(new StopFilter(new LowerCaseTokenizer(reader), stopWords));
 //   }
    
    public TokenStream tokenStream(String fieldName, Reader reader)
    {
        TokenStream result = new LowerCaseTokenizer(reader);
        result = new StopFilter(true, result, stopWords, true);
        result = new PorterStemFilter(result);
        return result;        
    }

    public static String displayTokenStream(TokenStream tokenStream) throws IOException
    {
    	String exit = "";
    	
        TermAttribute termAtt = (TermAttribute) tokenStream.getAttribute(TermAttribute.class);
        //TypeAttribute typeAtt = tokenStream.getAttribute(TypeAttribute.class); Nao versao 3.0

        while (tokenStream.incrementToken())
        {
            exit += termAtt.term();
            exit += " ";
            //System.out.println(typeAtt.type());
        }
        
        return exit;
    }

//    public TokenStream tokenStream(String fieldName, Reader reader) {
//         TokenStream result = new LowerCaseTokenizer(reader);
//         if (!stopWords.isEmpty()) {
//             result = new StopFilter(result, stopWords, true);
//         }
//         if (usePorterStemming) {
//             result = new PorterStemFilter(result);
//         }
//         return result;
//     }
}
