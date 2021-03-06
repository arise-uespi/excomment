package test;
import java.io.IOException;

import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.spell.LevensteinDistance;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

public class LuceneTest 
{
	public static void main(String[] args)
	{
		try
		{
			//	Specify the analyzer for tokenizing text.
		    //	The same analyzer should be used for indexing and searching
			SimpleAnalyzer analyzer = new SimpleAnalyzer();
			
			//	Code to create the index
			Directory index = new RAMDirectory();
			
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
			
			IndexWriter w = new IndexWriter(index, config);
			addDoc(w, "The should arg specifies the default field to use when no field is explicitly specified in the query", "193398817");
			addDoc(w, "should never to disallow continue here ", "23k43413");
			w.close();
			
			//	Text to search
			String querystr = args.length > 0 ? args[0] : "should check~1";
			
			//	The "title" arg specifies the default field to use when no field is explicitly specified in the query
			Query q = new QueryParser("title", analyzer).parse(querystr);
			
			// Searching code
			int hitsPerPage = 10;
		    IndexReader reader = DirectoryReader.open(index);
		    IndexSearcher searcher = new IndexSearcher(reader);
		    TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage);
		    searcher.search(q, collector);
		    ScoreDoc[] hits = collector.topDocs().scoreDocs;
		    
		    //	Code to display the results of search
		    System.out.println("Found " + hits.length + " hits.");
		    
		    for(int i=0;i<hits.length;++i) 
		    {
		      int docId = hits[i].doc;
		      Document d = searcher.doc(docId);
		      LevensteinDistance distance = new LevensteinDistance();
		      System.out.println((i + 1) + ". " + d.get("isbn") + " - " + d.get("title") + " - " + distance.getDistance(d.get("title"), "should check~0"));
		    }
		    
		    // reader can only be closed when there is no need to access the documents any more
		    reader.close();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	private static void addDoc(IndexWriter w, String title, String isbn) throws IOException 
	{
		  Document doc = new Document();
		  // A text field will be tokenized
		  doc.add(new TextField("title", title, Field.Store.YES));
		  // We use a string field for isbn because we don\'t want it tokenized
		  doc.add(new StringField("isbn", isbn, Field.Store.YES));
		  w.addDocument(doc);
	}
}