package search;

import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;

import models.Comment;
import models.Pattern;

import org.apache.lucene.search.spell.LevensteinDistance;
import org.tartarus.snowball.ext.PorterStemmer;

import database.CommentDataSource;
import database.PatternCommentDataSource;
import database.PatternDataSource;
import database.Rel_patternDataSource;

public class PesquisaUgram {
	public static int HeuristicasUGram(Pattern pattern, Comment comment) throws NumberFormatException, Exception {
	    String comentario = comment.getComment();
	    String padrao = pattern.getPattern();
		//Retirar caracteres especiais do comentario. 
	    comentario = Heuristicas.retirarCaracteres(comentario);
	    
    	StringTokenizer st = new StringTokenizer(comentario);
    	float distancia = 0;
    	int qnt = 0;
        while (st.hasMoreTokens()){	
        	String tok = st.nextToken();
        	LevensteinDistance ls = new LevensteinDistance();            
	        distancia = ls.getDistance(tok, padrao);
	        if(tok.length() <= 2){
	        	if (distancia == 1){
		        	qnt++;
				}
	        }else{
	        	if(tok.length() > 2 && tok.length() <= 5){
		        	if (distancia >= 0.75){
		        		if(Heuristicas.checkStem(padrao, tok)){
		        			if(Heuristicas.checkSufix(padrao, tok)){
		        				qnt++;
		        			}        			
		        		}
		        	}
				}else{
					if (distancia >= 0.85){
						if(Heuristicas.checkStem(padrao, tok)){
		        			if(Heuristicas.checkSufix(padrao, tok)){
		        				qnt++;
		        			}        			
		        		}
					}
				}
	        }
        }	        
    	return qnt;
    }
	
	public static void checkPatternsParentsUgrams(Map<Integer, ArrayList> commentsPatterns) throws NumberFormatException, Exception{
		System.out.println(" ======== Processando UGrams ======== ");
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
							
							qnt = HeuristicasUGram(PatternDataSource.getPattern(idPattern), comentario);
														
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
						
						qntPadroesPais = 0;
						qnt=0;							
				}else{ //Caso o Padrao nao tenha pai, ja relaciona ao comentario encontrado
					for (int i = 0; i < commentsFound.size(); i++){
						int qnt=0;
						Comment comment = CommentDataSource.getComment(commentsFound.get(i));
						Pattern pattern = PatternDataSource.getPattern(idPattern);
						qnt = HeuristicasUGram(pattern, comment);
						for (int k=0; k < qnt; k++){
							PatternCommentDataSource.insertPatternComment(comment.getId(), idPattern);
						}
					}
				}	
			}
		}
	}
}
