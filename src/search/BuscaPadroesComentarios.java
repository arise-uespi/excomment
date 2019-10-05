package search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import models.Comment;
import models.Pattern;

import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
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
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import suporte.Variaveis;
import database.CommentDataSource;
import database.PatternDataSource;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;

public class BuscaPadroesComentarios {
	
	private static void addDoc(IndexWriter w, String title, int id) throws IOException {
		Document doc = new Document();
		//doc.add(new StringField("id", Integer.toString(id), StringField.Store.YES));
		doc.add(new TextField("title", title, TextField.Store.YES));
		doc.add(new IntField("id", id, Field.Store.YES));
		w.addDocument(doc);
		
	}
	
	public static void Busca() throws NumberFormatException, Exception{
		//System.out.println(applyPorterStemmer("countries"));
		
		Variaveis var = new Variaveis();
		LemmaAnnotation annotation = new LemmaAnnotation();
		
		//Usando os Analyzers
		StandardAnalyzer analyzer = new StandardAnalyzer(); //Retira Stopwords e parece usar Levenshtein
		SimpleAnalyzer an = new SimpleAnalyzer(); //Nao retira Stop Words e Used e use nao sao mesmas coisas
		KeywordAnalyzer kw = new KeywordAnalyzer(); //Apenas para Ugrama
		WhitespaceAnalyzer ws = new WhitespaceAnalyzer(); //So quando o termo for exatamente igual
		EnglishAnalyzer ea = new EnglishAnalyzer();
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
		System.out.println("======= Indexando os comentários ========= ");
		for (int i=0; i < comentarios.size(); i++){
			addDoc(w, comentarios.get(i).getComment(), comentarios.get(i).getId());
		}
			
		w.close();			

		//Rodando padroes nos comentarios
		Map<Integer, ArrayList> mapPatternsComments = new HashMap<Integer, ArrayList>();
		String querystr = "";
		PhraseQuery pq;
		Query q;
		
		//For pra testar cada padrao
		for (int i=0; i < patterns.size(); i++){
			Pattern pattern = new Pattern();
			pattern = patterns.get(i);
			querystr = pattern.getPattern(); 
			int idPattern = pattern.getId();
			//Montando a Query
			//Wildquery varia de acordo com o tamanho da string. Pensar numa heuristica mais estudada
			if(pattern.getLength() > 1){
				pq = new PhraseQuery(0,"title", querystr);
				q = new QueryParser("title", ma).parse(pq.toString());
			}else{
				if(querystr.length() <= 2){
					querystr = querystr;
					pq = new PhraseQuery(0,"title", querystr);
					q = new QueryParser("title", ma).parse(pq.toString());
				}else{
					if(querystr.length() > 2 && querystr.length() <= 5){
						querystr = querystr + "~0.7";
						//pq = new PhraseQuery(0,"title", querystr);
						q = new QueryParser("title", ma).parse(querystr.toString());
					}else{
						querystr = querystr + "~0.8";
						//pq = new PhraseQuery(0,"title", querystr);
						q = new QueryParser("title", ma).parse(querystr.toString());
					}			
				}	
			}
			
			int hitsPerPage = 10000;
			IndexReader reader = null;
			TopScoreDocCollector collector = null;
			IndexSearcher searcher = null;
			reader = DirectoryReader.open(index);
			searcher = new IndexSearcher(reader);
			collector = TopScoreDocCollector.create(hitsPerPage);
			searcher.search(q, collector); //Procura Padrao nos comentarios
			ScoreDoc[] hits = collector.topDocs().scoreDocs; //Armazena Resultados
			
			//Se encontrar a query, armazena o ID do comentario
			ArrayList<Integer> commentsFound = new ArrayList<>();
			for (int j = 0; j < hits.length; ++j) {
				//flag = true;
			  	int docId = hits[j].doc;
				Document d;
				d = searcher.doc(docId);
				//System.out.println("\n---------" + d.get("title") + " - " + querystr);
				String newPhrase = d.get("title");
				String x = d.get("id"); //Codigo do Padrao Encontrado
				commentsFound.add(Integer.parseInt(x));
			}
			//Insere no Map o id do Padrao e os comentarios em que o padrao foi encontrado.
			mapPatternsComments.put( idPattern, commentsFound);
		}
		//Tratando os resultados encontrados pelo Lucene
		PesquisaNgram.checkPatternsParents(mapPatternsComments);
		PesquisaUgram.checkPatternsParentsUgrams(mapPatternsComments);
		System.out.println("========= Dados Processados =========");
	}
}

