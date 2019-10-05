package test;

import database.PatternCommentDataSource;
import database.PatternDataSource;
import database.CommentDataSource;
import database.Rel_patternDataSource;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.util.logging.StanfordRedwoodConfiguration;
import models.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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

public class HelloLucene {

	public static void main(String[] args) throws NumberFormatException, Exception
	{
		//System.out.println(applyPorterStemmer("countries"));
		
		Variaveis var = new Variaveis();
		
		//Usando os Analyzers
		StandardAnalyzer analyzer = new StandardAnalyzer();
		SimpleAnalyzer an = new SimpleAnalyzer();
		KeywordAnalyzer kw = new KeywordAnalyzer();
		WhitespaceAnalyzer ws = new WhitespaceAnalyzer();
		EnglishAnalyzer ea = new EnglishAnalyzer();
		
		Directory index = new RAMDirectory();
		IndexWriterConfig config = new IndexWriterConfig(an);
		IndexWriter w = null;
		int id = 0;
		w = new IndexWriter(index, config);
		
		//Trazendo os comentarios do Banco
		CommentDataSource comments = new CommentDataSource();
		ArrayList<Comment> comentarios = new ArrayList<>();
		comentarios = comments.getAllComments(1); //Trocar por ID do Projeto;
		
		//Trazendo padroes do Banco
		ArrayList<Pattern> patterns = new ArrayList<>();
		patterns = PatternDataSource.getNgramPatterns();

		//Indexando os comentarios
		for (int i=0; i < comentarios.size(); i++){
			addDoc(w, comentarios.get(i).getComment(), comentarios.get(i).getId());
		}
		w.close();			

		
		//Rodando padroes nos comentarios
		Map<Integer, ArrayList> mapPatternsComments = new HashMap<Integer, ArrayList>();
		String querystr = "";
		PhraseQuery pq = new PhraseQuery(0,"title", querystr);//PhraseQuery();
		Query q = new QueryParser("title", an).parse(pq.toString());	
		
		//For pra testar cada padrao
		for (int i=0; i < patterns.size(); i++){
			querystr = patterns.get(i).getPattern(); 
			int idPattern = patterns.get(i).getId();
			//Montando a Query
			pq = new PhraseQuery(0,"title", querystr);
			q = new QueryParser("title", an).parse(pq.toString());
			
			int hitsPerPage = 10;
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
				//System.out.println("aqui");
			  	int docId = hits[j].doc;
				Document d;
				d = searcher.doc(docId);
				String newPhrase = d.get("title");
				String x = d.get("id"); //Codigo do Padrao Encontrado
				commentsFound.add(Integer.parseInt(x));
			}
			mapPatternsComments.put( idPattern, commentsFound);
		}
		checkPatternsParents(mapPatternsComments);
		//checkUgrams(mapPatternsComments);
	}
	
	
	private static void checkPatternsParents(Map<Integer, ArrayList> commentsPatterns) throws ClassNotFoundException, SQLException{
		//Cada elemento do Map(Chave=idPattern / Value=Vetor de Comentarios com o padrao)
		for (Map.Entry<Integer, ArrayList> entry : commentsPatterns.entrySet())
		{
			

			ArrayList<Integer> commentsFound = new ArrayList<>();
			int idPattern = entry.getKey();
			commentsFound = entry.getValue();
			System.out.println("=======================");
			System.out.println("Padrao: " + idPattern);
			System.out.println("Comentarios: " + commentsFound.toString());
			
			//Retorna os pais do padrao atual
			ArrayList<Integer> parents = new ArrayList<>();
			Rel_patternDataSource rel_patterns = new Rel_patternDataSource();
			PatternCommentDataSource patternComment = new PatternCommentDataSource();
			parents = rel_patterns.getParents(idPattern);
			
			System.out.println("Pais de " + idPattern + ": " + parents.toString());
			//Checar se o padrao tem pai. Se nao tiver, relacionar PadraoComentario
			if(!parents.isEmpty()){
				ArrayList<Integer> commentsOfParents = new ArrayList<>();

				for (int k = 0; k < parents.size(); k++){
					commentsOfParents = commentsPatterns.get(parents.get(k));
					System.out.println("Comentarios do Pai: " + commentsOfParents.toString());
					for(int i = 0; i < commentsFound.size(); i++){
						int exists = -1;
						exists = checkCommentArray(commentsFound.get(i), commentsOfParents);
						if(exists != -1){
							System.out.println("Achou comentario "+commentsFound.get(i) + " no pai");
							//Tem que adicionar Padrao 2 comentario 1 e 3
							//System.out.println("Inseriu " + commentsFound.get(i) + " = " + parents.get(k));
							patternComment.insertPatternComment(commentsFound.get(i), parents.get(k));
							//System.out.println("Encontrou " + commentsFound.get(i) + " em " + parents.get(k));
						}else{
							patternComment.insertPatternComment(commentsFound.get(i), idPattern);
						}
					}
				}		
			}else{
				//System.out.println("Pais: de "+ idPattern + " = " + parents.toString());
				for(int i = 0; i < commentsFound.size(); i++){
					//patternComment.insertPatternComment(commentsFound.get(i), idPattern);
					//System.out.println("===============================");
					//System.out.println("Inserido Comentario="+commentsFound.get(i) + " Padrao="+idPattern);
				}
			}			    
		}
	}
	
	public void checkUgrams(Map<Integer, ArrayList> commentsPatterns){
		
	}
	
	//Testar se um comentario esta no arraylist de comentarios do pai
	private static int checkCommentArray(int comment, ArrayList<Integer> comments){
		int comentarioAchado=-1;
		for(int i =0; i < comments.size(); i++){
			if(comments.get(i) == comment ){
				comentarioAchado = comments.get(i);
			}
		}	
		return comentarioAchado;
	}
	
	//Pegar Comentarios do map de um determinado padrao
	private void getCommentsByPattern(Map<Integer, ArrayList> commentsPatterns, int idPattern){
		commentsPatterns.get(idPattern);
	}
	
	private static void addDoc(IndexWriter w, String title, int id) throws IOException {
			Document doc = new Document();
			//doc.add(new StringField("id", Integer.toString(id), StringField.Store.YES));
			doc.add(new TextField("title", title, TextField.Store.YES));
			doc.add(new IntField("id", id, Field.Store.YES));
			w.addDocument(doc);
	}

}
