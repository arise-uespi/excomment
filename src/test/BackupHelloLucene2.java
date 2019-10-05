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
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.TextField;
//import org.apache.lucene.document.StringField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
//import org.apache.lucene.index.Term;
//import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.complexPhrase.ComplexPhraseQueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.FuzzyQuery;
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



























import search.Heuristicas;
import search.MyAnalyzer;
import search.PesquisaUgram;
//import org.apache.lucene.util.Version;
import suporte.Variaveis;

public class BackupHelloLucene2 {

	public static void main(String[] args) throws NumberFormatException, Exception
	{
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
			querystr = patterns.get(i).getPattern(); 
			int idPattern = patterns.get(i).getId();
			//Montando a Query
			//Wildquery varia de acordo com o tamanho da string. Pensar numa heuristica mais estudada
			if(patterns.get(i).getLength() > 1){
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
			//int cont = 0;
			//boolean flag = false; //Se encontrou algum comentario com o padrao
			if (idPattern == 598){
				System.out.println("Padrao " + idPattern + " - " + hits.length);
			}
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
				if (idPattern == 598){
					System.out.println("Achou " + idPattern + " - no comment " + x);
					System.out.println("\n---------" + d.get("title") + " - " + querystr);

				}
			}
			//Insere no Map o id do Padrao e os comentarios em que o padrao foi encontrado.
			mapPatternsComments.put( idPattern, commentsFound);
		}
		checkPatternsParents(mapPatternsComments);
		PesquisaUgram.checkPatternsParentsUgrams(mapPatternsComments);
		//CheckSteamUGram("", "");
	}
	
	private static void checkPatternsParents(Map<Integer, ArrayList> commentsPatterns) throws NumberFormatException, Exception{
		//Percorre cada elemento do Map
		System.out.println("Processando NGrams");
		for (Map.Entry<Integer, ArrayList> entry : commentsPatterns.entrySet()){
			//Extrai os padroes e os comentarios do Map
			ArrayList<Integer> commentsFound = new ArrayList<>();
			int idPattern = entry.getKey();
			commentsFound = entry.getValue();	
			
			//Extrai quais padroes estao relcaionados ao padrao atual
			ArrayList<Integer> parents = new ArrayList<>();
			parents = Rel_patternDataSource.getParents(idPattern);
			
			ArrayList<Integer> commentsParent = new ArrayList<>(); 
			//Checar se o padrao tem pai. Se tiver, verificar os padroes relacionados
			if(PatternDataSource.getPattern(idPattern).getLength() > 1){

				if(!parents.isEmpty()){
					boolean achou = false;
					//padroes Ngrams
						for (int i = 0; i < commentsFound.size(); i++){ //Percorre cada comentario que achou o padrao
							for(int k=0; k < parents.size(); k++){ //Percorre cada padrao pai do padrao
								commentsParent = commentsPatterns.get(parents.get(k));	
								for(int j=0; j< commentsParent.size(); j++){ //Percorre os comentarios dos padroes pai
									//System.out.println(commentsFound.get(i) + " - " + commentsParent.get(j));
									if(commentsFound.get(i).equals(commentsParent.get(j))){ 
										//Se o comentario que existe no padrao tambem existe nos padroes pais
										if (!PatternCommentDataSource.checkRelacaoExiste(commentsFound.get(i), parents.get(k))){
											PatternCommentDataSource.insertPatternComment(commentsFound.get(i), parents.get(k));
											achou = true;
										}
									}
								}
							}
							if(!achou){ //So era usado para os Ngramas
								if (!PatternCommentDataSource.checkRelacaoExiste(commentsFound.get(i), idPattern)){
									PatternCommentDataSource.insertPatternComment(commentsFound.get(i), idPattern);
								}
							}
						}
						achou = false;						
				}else{ //Caso o Padrao nao tenha pai, ja relaciona ao comentario encontrado
					for (int i = 0; i < commentsFound.size(); i++){
						//Primeiro testa se a relacao ja Existe para nao duplicar
						if (!PatternCommentDataSource.checkRelacaoExiste(commentsFound.get(i), idPattern)){
							PatternCommentDataSource.insertPatternComment(commentsFound.get(i), idPattern);
						}
					}
				}	
			}		
		}
	}
	
	private static void checkPatternsParentsUgrams(Map<Integer, ArrayList> commentsPatterns) throws NumberFormatException, Exception{
		for (Map.Entry<Integer, ArrayList> entry : commentsPatterns.entrySet()){
			//Extrai os padroes e os comentarios do Map
			ArrayList<Integer> commentsFound = new ArrayList<>();
			int idPattern = entry.getKey();
			commentsFound = entry.getValue();	
			
			//Extrai quais padroes estao relcaionados ao padrao atual
			ArrayList<Integer> parents = new ArrayList<>();
			parents = Rel_patternDataSource.getParents(idPattern);
			ArrayList<Integer> commentsParent = new ArrayList<>(); 
			if(PatternDataSource.getPattern(idPattern).getLength() == 1){
				if(!parents.isEmpty()){ //Se nao tiver relacao com nenhum outro padrao ja insere no else
					//Vai em cada comentario e conta quantas vezes o padrao apareceu
					//Conta em quantos dos padroes relacionados aquele comentario apareceu
					//Subtrai a quantidade
					int qntPadroesPais=0;
					int qnt=0;
					Comment comentario = new Comment();
						for (int i = 0; i < commentsFound.size(); i++){ //Percorre cada comentario que achou o padrao
							comentario = CommentDataSource.getComment(commentsFound.get(i));
							
							qnt = CheckSteamUGram(PatternDataSource.getPattern(idPattern).getPattern(), comentario.getComment());
														
							for(int k=0; k < parents.size(); k++){ //Percorre cada padrao pai do padrao
								commentsParent = commentsPatterns.get(parents.get(k));	
								for(int j=0; j< commentsParent.size(); j++){ //Percorre os comentarios dos padroes pai
									//System.out.println(commentsFound.get(i) + " - " + commentsParent.get(j));
									if(commentsFound.get(i).equals(commentsParent.get(j))){ 
										//Se o comentario que existe no padrao tambem existe nos padroes pais
											//PatternCommentDataSource.insertPatternComment(commentsFound.get(i), parents.get(k));
											qntPadroesPais++;
									}
								}
							}
							for (int k=0; k < (qnt - qntPadroesPais); k++){
								PatternCommentDataSource.insertPatternComment(comentario.getId(), idPattern);
							}
							qntPadroesPais = 0;
							qnt=0;
						}
						//Comentario
						//System.out.println("Padrao: +" + idPattern + " Qnt: " + qnt + " Pais: " + qntPadroesPais);
						
						//Rodar isso pra achar o problema do duplicates
						qntPadroesPais = 0;
						qnt=0;							
				}else{ //Caso o Padrao nao tenha pai, ja relaciona ao comentario encontrado
					for (int i = 0; i < commentsFound.size(); i++){
						int qnt=0;
						Comment comment = CommentDataSource.getComment(commentsFound.get(i));
						qnt = CheckSteamUGram(PatternDataSource.getPattern(idPattern).getPattern(), comment.getComment());
						if (comment.getId() == 4600){
							System.out.println("OPA - Quantidade de " + idPattern + " - " + qnt);
						}
						for (int k=0; k < qnt; k++){
							PatternCommentDataSource.insertPatternComment(comment.getId(), idPattern);
						}
					}
				}	
			}
		}
	}
	
	private static void addDoc(IndexWriter w, String title, int id) throws IOException {
			Document doc = new Document();
			//doc.add(new StringField("id", Integer.toString(id), StringField.Store.YES));
			doc.add(new TextField("title", title, TextField.Store.YES));
			doc.add(new IntField("id", id, Field.Store.YES));
			w.addDocument(doc);
			
	}

	 public static int CheckSteamUGram(String dic, String frase) throws NumberFormatException, Exception {

		    frase = Heuristicas.retirarCaracteres(frase);
	    	StringTokenizer st = new StringTokenizer(frase);
        	float distancia = 0;
        	int qnt = 0;
	        while (st.hasMoreTokens())
	        {	
	        	String tok = st.nextToken();
	        	LevensteinDistance ls = new LevensteinDistance();            
		        distancia = ls.getDistance(tok, dic);
		        //System.out.println(tok + " - " + dic + " - " + distancia);
		        if(tok.length() <= 2){
		        	if (distancia >= 1){
			        	qnt++;
					}
		        }else{
		        	//Caso tenha entre 3 e 5 caracteres, verifica se o verbo ta no passado e  tira stem
		        	if(tok.length() > 2 && tok.length() <= 5){
			        	if (distancia >= 0.7){
			        		PorterStemmer stemTerm = new PorterStemmer();
			        		stemTerm.setCurrent(tok);
			        		stemTerm.stem();
			        		String stemTok = stemTerm.getCurrent();
			        		stemTerm.setCurrent(dic);
			        		stemTerm.stem();
			        		String stemDic = stemTerm.getCurrent();
			        		if (stemDic.equalsIgnoreCase(stemTok)){
								String suffixDic = dic.substring(dic.length()-2, dic.length());
			        			String suffixTok = tok.substring(tok.length()-2, tok.length());
			        			//System.out.println(suffix + "Sufixo");
			        			if(suffixTok.equalsIgnoreCase("ed")){
									if(suffixDic.equalsIgnoreCase("ed")){
										qnt++;
									}	
								}else{
									qnt++;
								}        			
			        		}
			        	}
					}else{
						if (distancia >= 0.8){
							String suffixTok = tok.substring(tok.length()-2, tok.length());
							String suffixDic = dic.substring(dic.length()-2, dic.length());
							if(suffixTok != null && suffixDic != null){								
								if(suffixTok.equalsIgnoreCase("ed")){
									if(suffixDic.equalsIgnoreCase("ed")){
										qnt++;
									}	
								}else{
									qnt++;
								}
							}
						}
					}
		        }
		        
	        }	        
        	return qnt;
	    }

}
