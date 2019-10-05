package test;

import database.PatternDataSource;
import database.CommentDataSource;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.util.logging.StanfordRedwoodConfiguration;
import models.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
//import java.util.ArrayList;
import java.util.StringTokenizer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.util.CharArraySet;
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
import org.apache.lucene.util.Version;
import org.tartarus.snowball.ext.PorterStemmer;


import search.MyAnalyzer;
//import org.apache.lucene.util.Version;
import suporte.Variaveis;

public class BackupHelloLucene {
	
	public static String applyPorterStemmer(String term)throws IOException {
        PorterStemmer stemmer = new PorterStemmer();
        stemmer.setCurrent(term);
        stemmer.stem();
        return stemmer.getCurrent();
    }

	public static void main(String[] args) throws NumberFormatException, Exception
	{
		//System.out.println(applyPorterStemmer("countries"));
		
		Variaveis var = new Variaveis();
		LemmaAnnotation annotation = new LemmaAnnotation();
		
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
		
		//Trazendo os comentarios do Banco
		CommentDataSource comments = new CommentDataSource();
		ArrayList<Comment> comentarios = new ArrayList<>();
		comentarios = comments.getAllComments(1); //Trocar por ID do Projeto;
		
		//Trazendo padroes do Banco
		ArrayList<Pattern> patterns = new ArrayList<>();
		patterns = PatternDataSource.getAllPatterns();

		//Indexando os comentarios
		for (int i=0; i < comentarios.size(); i++){
			addDoc(w, comentarios.get(i).getComment(), comentarios.get(i).getId());
		}
		w.close();	
		
		//System.out.println(StopAnalyzer.ENGLISH_STOP_WORDS_SET);
		
		//Rodando padroes nos comentarios
		String querystr = "";
		PhraseQuery pq = new PhraseQuery(1,"title", querystr);//PhraseQuery();
		Query q = new QueryParser("title", ma).parse(pq.toString());	
		
		//For pra testar cada padrao
		for (int i=0; i < patterns.size(); i++){
			querystr = patterns.get(i).getPattern(); 
			//Montando a Query
			pq = new PhraseQuery(1,"title", querystr);
			q = new QueryParser("title", ma).parse(pq.toString());
			
			int hitsPerPage = 10;
			IndexReader reader = null;
			TopScoreDocCollector collector = null;
			IndexSearcher searcher = null;
			reader = DirectoryReader.open(index);
			searcher = new IndexSearcher(reader);
			collector = TopScoreDocCollector.create(hitsPerPage);
			searcher.search(q, collector); //Procura Padrao nos comentarios
			
			//searcher.count(arg0);
			
			ScoreDoc[] hits = collector.topDocs().scoreDocs; //Armazena Resultados
			System.out.println("Testando Padrao: " + querystr);
			
			//Se encontrar a query, armazena o ID do comentario
			int cont = 0;
			boolean flag = false;
			int[] commentsFound = new int[1000];
			int[] patternsFound = new int[1000];
			for (int j = 0; j < hits.length; ++j) {
				flag = true;
				//System.out.println("aqui");
			  	int docId = hits[j].doc;
				Document d;
				d = searcher.doc(docId);
				
				String newPhrase = d.get("title");
				String x = d.get("id"); //Codigo do Padrao Encontrado
				commentsFound[cont] = Integer.parseInt(x);
				System.out.println(x);
			}
		}
		


		// Use the same analyzer for both indexing and searching
		//QueryParser parser = new QueryParser("title", analyzer);
		//Query q = null;
		//q = parser.parse(querystr);
		//pq.setSlop(2);
		//pq.add(new Term("title", querystr));
		
		//Query q2 = new ComplexPhraseQueryParser("title", analyzer).parse(pq.toString());
        // 3. search
//		querystr = "new chart";
//		pq = new PhraseQuery(0,"title", querystr);//PhraseQuery();
//		q = new QueryParser("title", ws).parse(pq.toString());
//		int hitsPerPage = 10;
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
//			System.err.println("Look for '" + dic + "'");
//	        for(int k = 0; k < hits.length;++k)
//	        {
//			  CheckSteamUGram(dic, newPhrase);
//	        }	
//		  }
//		reader.close();
	}
	
	private static void checkPatternsParents(int[] hits){
		for (int i = 0; i < hits.length; i++){
			if(hits[i] != 0)
				System.out.println(hits[i]);
		}
	}
	
	private static void addDoc(IndexWriter w, String title, int id) throws IOException {
			Document doc = new Document();
			//doc.add(new StringField("id", Integer.toString(id), StringField.Store.YES));
			doc.add(new TextField("title", title, TextField.Store.YES));
			doc.add(new IntField("id", id, Field.Store.YES));
			w.addDocument(doc);
	}

	 public static void CheckSteamUGram(String dic, String frase) throws NumberFormatException, Exception
	    {
		    //System.err.println("Frase: " + frase);
	    	StringTokenizer st = new StringTokenizer(frase);
        	boolean find = false;
        	float distancia = 0;
        	
	        while (st.hasMoreTokens())
	        {		
	        	String tok = st.nextToken();
	        	System.err.println("TOK Frase: " + tok.toString());
	        	LevensteinDistance ls = new LevensteinDistance();            
	        	        	
	       // 	for (int i = 0; i < dic.size(); i++)
	       // 	{
		        	distancia = ls.getDistance(tok, dic);
		        	if (distancia >= 0.8)// termos iguais
					{
		        		find = true;
		        		break;
					}
	        	//}
	        }
        	if (find)// termos iguais
			{
				System.out.println("Achou Ugrama : "+ dic + " Distance: " + distancia);
			//	insertTermoTemp(day, Integer.parseInt(month), Integer.parseInt(year), Integer.parseInt(hp_dic_u.get(steam)),idDeveloper);
			}
	    }

}
