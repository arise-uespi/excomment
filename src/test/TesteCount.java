package test;

import java.io.IOException;

import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import search.MyAnalyzer;

public class TesteCount {

	public static void main(String[] args) throws ParseException, IOException {
		//Usando os Analyzers
				StandardAnalyzer analyzer = new StandardAnalyzer();
				SimpleAnalyzer an = new SimpleAnalyzer();
				KeywordAnalyzer kw = new KeywordAnalyzer();
				WhitespaceAnalyzer ws = new WhitespaceAnalyzer();
				EnglishAnalyzer ea = new EnglishAnalyzer();
				StopAnalyzer sa = new StopAnalyzer();
				MyAnalyzer ma = new MyAnalyzer();
				
				Directory index = new RAMDirectory();
				IndexWriterConfig config = new IndexWriterConfig(ma);
				IndexWriter w = null;
				int id = 0;
				w = new IndexWriter(index, config);
				
				addDoc(w, "The should arg specifies the default field to use when no field is explicitly specified in the query", 1);
				addDoc(w, "Lucene in Action", 2);
				addDoc(w, "Dummies Lucene Dummies for Dummies", 3);
				addDoc(w, "Managing Dummies Gigabytes", 4);
				w.close();	

				String querystr = "Dummies";
				PhraseQuery pq = new PhraseQuery(1,"title", querystr);//PhraseQuery();
				Query q = new QueryParser("title", ma).parse(pq.toString());

		
		int hitsPerPage = 10;
		IndexReader reader = null;
		TopScoreDocCollector collector = null;
		IndexSearcher searcher = null;
		reader = DirectoryReader.open(index);
		searcher = new IndexSearcher(reader);
		collector = TopScoreDocCollector.create(hitsPerPage);
		searcher.search(q, collector);
		
		ScoreDoc[] hits = collector.topDocs().scoreDocs; //Armazena Resultados
		System.out.println("Testando Padrao: " + querystr);
		
		//Se encontrar a query, armazena o ID do comentario
		int cont = 0;
		boolean flag = false;
		int[] commentsFound = new int[1000];
		int[] patternsFound = new int[1000];
		int quant = searcher.count(q);
		for (int j = 0; j < hits.length; ++j) {
			flag = true;
			//System.out.println("aqui");
		  	int docId = hits[j].doc;
			Document d;
			d = searcher.doc(docId);
			
			
			String newPhrase = d.get("title");
			String x = d.get("id"); //Codigo do Padrao Encontrado
			commentsFound[cont] = Integer.parseInt(x);
			System.out.println("Quantidade: " + quant); 
		}

	}
	
	private static void addDoc(IndexWriter w, String title, int id) throws IOException {
		Document doc = new Document();
		//doc.add(new StringField("id", Integer.toString(id), StringField.Store.YES));
		doc.add(new TextField("title", title, TextField.Store.YES));
		doc.add(new IntField("id", id, Field.Store.YES));
		w.addDocument(doc);
}

}
