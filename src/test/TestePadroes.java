package test;

import database.PatternDataSource;
import database.CommentDataSource;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.util.logging.StanfordRedwoodConfiguration;
import models.*;

import java.io.IOException;
import java.util.ArrayList;
//import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.TextField;
//import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
//import org.apache.lucene.index.Term;
//import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.complexPhrase.ComplexPhraseQueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.spell.LevensteinDistance;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.tartarus.snowball.ext.PorterStemmer;

//import org.apache.lucene.util.Version;
import suporte.Variaveis;

public class TestePadroes {

	public static void main(String[] args) throws NumberFormatException, Exception
	{		
		
//		//Usando os Analyzers
//		StandardAnalyzer analyzer = new StandardAnalyzer();
//		SimpleAnalyzer an = new SimpleAnalyzer();
//		KeywordAnalyzer kw = new KeywordAnalyzer();
//		WhitespaceAnalyzer ws = new WhitespaceAnalyzer();
//		EnglishAnalyzer ea = new EnglishAnalyzer();
//		
//		Directory index = new RAMDirectory();
//		IndexWriterConfig config = new IndexWriterConfig(an);
//		IndexWriter w = null;
//		int id = 0;
//		w = new IndexWriter(index, config);
//		//Adicionando documentos no index
//		addDoc(w, "In the test code this is a test", ++id);
//		w.close();	
//		
//		String query1 = "this is a test";
//		String query2 = "test";
//		PhraseQuery pq;
//		Query q;
//	
//		System.out.println("Padrao 1");
//		pq = new PhraseQuery(0,"title", query1);//PhraseQuery();
//		q = new QueryParser("title", an).parse(pq.toString());	
//		int hitsPerPage = 10;
//
//		IndexReader reader = null;
//		TopScoreDocCollector collector = null;
//		IndexSearcher searcher = null;
//		reader = DirectoryReader.open(index);
//		searcher = new IndexSearcher(reader);
//		collector = TopScoreDocCollector.create(hitsPerPage);
//		searcher.search(q, collector);
//		
//		ScoreDoc[] hits = collector.topDocs().scoreDocs;
//		System.out.println("Found " + hits.length + " hits.");
//		  for (int i = 0; i < hits.length; ++i) {
//		  	int docId = hits[i].doc;
//			Document d;
//			d = searcher.doc(docId);
//			String  newPhrase	= d.get("title");
//			String id2 = d.get("id");
//			System.out.println(id2 + ". " + newPhrase);
//		  }
//		  
//		//verificar palavra por palavra 
//		System.out.println("Padrao 2");
//			pq = new PhraseQuery(0,"title",query2);//PhraseQuery();
//			q = new QueryParser("title", an).parse(pq.toString() + "NOT " + "this is a test");	
//			hitsPerPage = 10;
//
//			reader = null;
//		    collector = null;
//			searcher = null;
//			reader = DirectoryReader.open(index);
//			searcher = new IndexSearcher(reader);
//			collector = TopScoreDocCollector.create(hitsPerPage);
//			searcher.search(q, collector);
//			
//			 hits = collector.topDocs().scoreDocs;
//			System.out.println("Found " + hits.length + " hits.");
//			  for (int i = 0; i < hits.length; ++i) {
//			  	int docId = hits[i].doc;
//				Document d;
//				d = searcher.doc(docId);
//				String  newPhrase	= d.get("title");
//				String id2 = d.get("id");
//				System.out.println(id2 + ". " + newPhrase);
//			  }
//		reader.close();
		
		//ExtractPatterns.extractThemes();

	}
	
	private static void addDoc(IndexWriter w, String title, int id) throws IOException {
			Document doc = new Document();
			//doc.add(new StringField("id", Integer.toString(id), StringField.Store.YES));
			doc.add(new TextField("title", title, TextField.Store.YES));
			doc.add(new IntField("id", id, Field.Store.YES));
			w.addDocument(doc);
	}

}
