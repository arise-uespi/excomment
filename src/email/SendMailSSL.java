package email;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
 
public class SendMailSSL {
	public void sendMail(String text) throws AddressException, MessagingException {
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
 
		Session session = Session.getDefaultInstance(props,
			new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication("commentmining@gmail.com","dividatecnica");
				}
			});
 
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("commentmining@gmail.com"));
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse("commentmining@gmail.com"));
			message.setSubject("Erro no projeto");
			message.setText(text);
 
			Transport.send(message);
	}
}