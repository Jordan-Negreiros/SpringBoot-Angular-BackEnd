package com.api.rest.service;

import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class ServiceEnviaEmail {

    private String userName = "testesjordan@gmail.com";
    private String senha = "*Jordan123456";

    public void enviarEmail (String assunto, String emailDestino, String mensagem) throws MessagingException {

        Properties properties = new Properties();
        properties.put("mail.smtp.ssl.trust", "*");
        properties.put("mail.smtp.auth", "true"); // Autorização
        properties.put("mail.smtp.starttls", "true"); // Autenticação
        properties.put("mail.smtp.host", "smtp.gmail.com"); // Servidor Google
        properties.put("mail.smtp.port", "465"); // porta do servidor
        properties.put("mail.smtp.socketFactory.port", "465"); // Especifica porta socket
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); // Classe de conexão socket

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, senha);
            }
        });

        Address[] toUser = InternetAddress.parse(emailDestino);

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(userName)); /* Quem está enviando */
        message.setRecipients(Message.RecipientType.TO, toUser); /* Quem irá receber */
        message.setSubject(assunto); /* Assunto */
        message.setText(mensagem); /* corpo */

        Transport.send(message); /* envia */
    }
}
