package suporte;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.swing.text.BadLocationException;

import email.SendMailSSL;

public class Funcoes {

	public static void printExtracting(String tag, String text) throws BadLocationException{
		//Document doc = editExtracting.getDocument();
		if (tag.trim().length() > 0) {
			Variaveis.docEditExtracting.insertString(Variaveis.docEditExtracting.getLength(), tag + " -> " + text+ "\n", null);
		} else {
			Variaveis.docEditExtracting.insertString(Variaveis.docEditExtracting.getLength(), text+ "\n", null);
		}
	}
	
	public static String removeSeparators(String text) {
		text = text.replaceAll("\\r|\\n", "");
		return text;
	}
	
	public static void sendMail(String text) throws AddressException, MessagingException {
		SendMailSSL mailSSL = new SendMailSSL();
		//mailSSL.sendMail(text); Comentado por estava gerando Time attempted
	}
	
	public static void sendStackMail(Throwable aThrowable) {
		try {
		//Comentado pois estava gerando time attempted
		//SendMailSSL mailSSL = new SendMailSSL();
		//mailSSL.sendMail(getStackTrace(aThrowable));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String getStackTrace(Throwable aThrowable) {
	    Writer result = new StringWriter();
	    PrintWriter printWriter = new PrintWriter(result);
	    aThrowable.printStackTrace(printWriter);
	    
	    InetAddress addr;
	    String hostname = "";
	    try {
			addr = InetAddress.getLocalHost();
			 hostname = addr.getHostName();
		} catch (UnknownHostException e) {}
	    
	    String email = "Ocorreu o seguinte erro:\n" + result.toString();
	    
	    email += "\n\n\nConfigurações do sistema:\n";
	    email += "Sistema Operacional: " + System.getProperty("os.name");
	    email += "\nVersão: " + System.getProperty("os.version");
	    email += "\nArquitetura : " + System.getProperty("os.arch");
	    email += "\nMemória livre: " + Runtime.getRuntime().freeMemory();
	    email += "\nMemoria disponível para o Java: " + Runtime.getRuntime().totalMemory();
	    email += "\nUsuário : " + hostname;
	    return email;
	  }
	
	//Necessário pois as vezes as classes vem com um encoding estranho
	public static String tratarClasse(String line) {
		String classe;
		//String a = line.substring(line.indexOf("<name>"), line.indexOf("</name>") + 7);
		String a = line.replaceAll("\\r?\\n", " ");
		String aux = "";
		
		while (a.contains("<")) {
			aux = a.substring(a.indexOf("<"), a.indexOf(">") + 1);
			try {
				a = a.replace(aux, "");
			} catch (Exception e) {
			}
		}
		classe = a.replaceAll("(\\s+)", " ").replace("{", "").trim();
		return classe;
	}
	
	//Necessário pois as vezes os projetos vem com um encoding estranho
	public static String tratarMetodo(String line) {
		String metodo;
		String a = line.replaceAll("\\r?\\n", " ");
		String aux = "";

		while (a.contains("<")) {
			aux = a.substring(a.indexOf("<"), a.indexOf(">") + 1);
			try {
				a = a.replace(aux, "");
			} catch (Exception e) {
			}
		}
		metodo = a.replaceAll("(\\s+)", " ").replace("{", "").trim();
		
		metodo = metodo.replaceAll("&lt;", "<");
		metodo = metodo.replaceAll("&gt;", ">");
		metodo = metodo.substring(0, metodo.indexOf(")") + 1);
		return metodo;
	}
	
	//Necessário pois as vezes os comentários vem com um encoding estranho
	public static String tratarComentario(String line) {
		String comentario;
		String a = line;//line.replaceAll("\\r?\\n", " ");
		String aux = "";

		while (a.contains("<")) {
			aux = a.substring(a.indexOf("<"), a.indexOf(">") + 1);
			try {
				a = a.replace(aux, "");
			} catch (Exception e) {
			}
		}
		
		comentario = a.replaceAll("(\\s+)", " ").replace("{", "").trim();
		comentario = comentario.replaceAll("&lt;", "<");
		comentario = comentario.replaceAll("&gt;", ">");
		return comentario;
	}
}
