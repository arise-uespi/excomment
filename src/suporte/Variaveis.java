package suporte;

import extracao.Comment;
import extracao.Tagger;

import java.util.ArrayList;

import javax.swing.text.Document;

public class Variaveis {
	//Configurações da base
	public static String host = "localhost";
	public static String port = "5432";
	public static String database = "";
	public static String user = "";
	public static String password = "";
	
	public static int countTotalComments = 0;
	public static int countVaideComments = 0;
	public static int countTotalDiscartedComments = 0;
	
	public static String contentMethod = "";
	public static String contentComments = "";
	public static String Projectname = "";
	public static String path = "";
	public static int idProject = -1;
	
	public static ArrayList<String> palavrasReservadas = new ArrayList<String>();
	public static ArrayList<String> caracteres = new ArrayList<String>();
	public static ArrayList<String> licencas = new ArrayList<String>();
	public static ArrayList<String> coringas = new ArrayList<String>();
	public static ArrayList<String> palavrasStem = new ArrayList<String>();
	
	public static Document docEditExtracting;
	
	public static ArrayList<Comment> comments = new ArrayList<Comment>();
	public static ArrayList<Comment> commentsTogether = new ArrayList<Comment>();
	public static ArrayList<Comment> linked_comments = new ArrayList<Comment>();
	
	public static Tagger tagger;
	
	public static ArrayList<String> padroes = new ArrayList<String>();
	public static ArrayList<String> padroesSelecionados = new ArrayList<String>();
	public static ArrayList<String> padroesSelecionadosPortugues = new ArrayList<String>();
}