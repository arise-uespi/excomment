package search;

import java.util.ArrayList;
import java.util.Map;

import database.PatternCommentDataSource;
import database.PatternDataSource;
import database.Rel_patternDataSource;

public class PesquisaNgram {
	public static void checkPatternsParents(Map<Integer, ArrayList> commentsPatterns) throws NumberFormatException, Exception{
		//Percorre cada elemento do Map
		System.out.println("======== Processando NGrams ========= ");
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
}
