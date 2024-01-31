package com.ite5year.services;

import org.springframework.web.multipart.MultipartFile;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;

/**
 * @author doraemon
 */
public class GoogleGmailService {
    private GoogleGmailService() {
    }

    public static void Send(final String username, final String password, String recipientEmail, String subjectTitle, String content, Multipart file) throws Exception {

        Properties props = new Properties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {




            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(recipientEmail));
            message.setSubject(subjectTitle);
            message.setText(content);
            if(file != null) {
                // Create the message part
                BodyPart messageBodyPart = new MimeBodyPart();
                // Fill the message
                messageBodyPart.setText("<p>" + content + "</p>");
                messageBodyPart.setContent(content, "text/html");
                file.addBodyPart(messageBodyPart);

                // Second part
                message.setContent(file);
            }
            Transport.send(message);

            System.out.println("Done");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }


}