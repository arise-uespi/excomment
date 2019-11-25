package test;
import java.io.IOException;

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
import org.apache.lucene.search.spell.LuceneLevenshteinDistance;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

public class Test 
{
	public static void main(String[] args)
	{
		try
		{
			StandardAnalyzer analyzer = new StandardAnalyzer();
			Directory index = new RAMDirectory();
			IndexWriterConfig config = new IndexWriterConfig(analyzer);
			
			IndexWriter w = new IndexWriter(index, config);
			addDoc(w, "/** require Creates a new demo instance.* * @param title the frame title.*/");
			w.close();
			
			//	Texto para procurar (padrao)
			String querystr = args.length > 0 ? args[0] : "required~1"; //Padrao = should not e tolerancia 0
			
			Query q = new QueryParser("comment", analyzer).parse(querystr);
			
			int hitsPerPage = 10;
		    IndexReader reader = DirectoryReader.open(index);
		    IndexSearcher searcher = new IndexSearcher(reader);
		    TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage);
		    searcher.search(q, collector);
		    ScoreDoc[] hits = collector.topDocs().scoreDocs;
		    
		    for(int i = 0; i < hits.length; ++i) 
		    {
		      int docId = hits[i].doc;
		      Document d = searcher.doc(docId);
		      System.out.println(d.get("comment"));
		    }
		    
		    reader.close();
		}
		catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	private static void addDoc(IndexWriter w, String title) throws IOException 
	{
		  Document doc = new Document();
		  doc.add(new TextField("comment", title, Field.Store.YES));
		  w.addDocument(doc);
	}
}