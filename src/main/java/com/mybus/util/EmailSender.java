package com.mybus.util;

import com.mybus.SystemProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

;

/**
 * Created by srinikandula on 3/28/17.
 */
@Service
public class EmailSender {

    @Autowired
    private SystemProperties systemProperties;

    public void sendEmail(String to, String content, String subject) {
        final String username = systemProperties.getProperty("mail.username");
        final String password = systemProperties.getProperty("mail.password");
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.starttls.enable", true);

        // Get the Session object.
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
                    InternetAddress.parse(to));
            message.setSubject(subject);
            message.setContent(content, "text/html; charset=utf-8");
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void sendExpiringNotifications(String content, String toEmail){
        String subject ="Important! Vehicle documents exipring";
        if(content.length() > 0) {
            sendEmail(toEmail, content, subject);
        }
    }
    public void sendServiceReportsToBeReviewed(String content, String toEmail){
        String subject ="Important! Service reports need to be reviewed";
        if(content.length() > 0) {
            sendEmail(toEmail, content, subject);
        }
    }
}
