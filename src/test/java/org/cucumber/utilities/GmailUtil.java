package org.cucumber.utilities;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.util.Properties;


public class GmailUtil {

    //this is responsible to send the message with attachment
    public static void sendAttach(String message, String subject, String to, String from, String filepath) {

        String host="smtp.gmail.com";

        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port","465");
        properties.put("mail.smtp.ssl.enable","true");
        properties.put("mail.smtp.auth","true");

        Session session=Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("qa@mail.ai", ConfigurationReader.get("appPassword"));
            }

        });

//        session.setDebug(true);

        MimeMessage m = new MimeMessage(session);

        try {

            m.setFrom(from);
            m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            m.setSubject(subject);

            MimeMultipart mimeMultipart = new MimeMultipart();

            MimeBodyPart textMime = new MimeBodyPart();
            MimeBodyPart fileMime = new MimeBodyPart();

            try {
                textMime.setText(message);

                File file=new File(filepath);
                fileMime.attachFile(file);

                mimeMultipart.addBodyPart(textMime);
                mimeMultipart.addBodyPart(fileMime);

            } catch (Exception e) {
                e.printStackTrace();
            }
            m.setContent(mimeMultipart);

            Transport.send(m);

        }catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("********************** Mail Sent **********************");
    }


    public static void sendEmail(String message, String subject, String to, String from) {

        String host="smtp.gmail.com";

        Properties properties = System.getProperties();
//        System.out.println("PROPERTIES "+properties);

        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port","465");
        properties.put("mail.smtp.ssl.enable","true");
        properties.put("mail.smtp.auth","true");

        Session session=Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("suleymansarikurk@gmail.com",  ConfigurationReader.get("appPassword"));
            }

        });
//        session.setDebug(true);

        MimeMessage m = new MimeMessage(session);
        try {

            m.setFrom(from);
            m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            m.setSubject(subject);
            m.setText(message);

            Transport.send(m);

            System.out.println("********************** Mail Sent **********************");

        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void sendHTMLEmail(String message, String subject, String to, String from) {

        String host="smtp.gmail.com";

        Properties properties = System.getProperties();
//        System.out.println("PROPERTIES "+properties);

        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port","465");
        properties.put("mail.smtp.ssl.enable","true");
        properties.put("mail.smtp.auth","true");

        Session session=Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("suleymansarikurk@gmail.com",  ConfigurationReader.get("appPassword"));
            }

        });
//        session.setDebug(true);

        MimeMessage m = new MimeMessage(session);
        try {

            m.setFrom(from);
            m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            m.setSubject(subject);

            if (message != null && (message.trim().startsWith("<") || message.contains("<table") || message.contains("<html"))) {
                m.setContent(message, "text/html; charset=utf-8");
            } else {
                m.setText(message);
            }

            Transport.send(m);

            System.out.println("********************** Mail Sent **********************");

        }catch (Exception e) {
            e.printStackTrace();
        }

    }

}





