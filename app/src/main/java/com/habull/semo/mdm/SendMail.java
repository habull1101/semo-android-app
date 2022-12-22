package com.habull.semo.mdm;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class SendMail extends AsyncTask<Void, Void, Void> {
    private Context context;
    private Session session;
    private String email, subject, message;

    private String usernameSender = "project.ptit@gmail.com";
    private String passwordSender = "dovanhaptit";

    public SendMail(Context context, String email, String subject, String message) {
        this.context = context;
        this.email = email;
        this.subject = subject;
        this.message = message;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.socketFactory.port", "465");
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.port", "465");

        session = Session.getDefaultInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(usernameSender, passwordSender);
            }
        });

        MimeMessage mimeMessage = new MimeMessage(session);
        try {
            //add basic info to email
            mimeMessage.setFrom(new InternetAddress(usernameSender));
            mimeMessage.addRecipients(Message.RecipientType.TO, String.valueOf(new InternetAddress(email)));
            mimeMessage.setSubject(subject);

            Multipart multipart = new MimeMultipart();

            // body part 1
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(message);
            multipart.addBodyPart(messageBodyPart);

            // body part2
            // add image to email
            DateFormat df = new SimpleDateFormat("yyyyMMdd");
            String stringDate = df.format(Calendar.getInstance().getTime());
            String nameImage = "IMG_rear_" + stringDate + ".jpg";

            BodyPart messageBodyPart2 = new MimeBodyPart();
            DataSource source1 = new FileDataSource("/storage/emulated/0/Pictures/" + nameImage);
            messageBodyPart2.setDataHandler(new DataHandler(source1));
            messageBodyPart2.setFileName("Camera rear.jpg");
            multipart.addBodyPart(messageBodyPart2);

            BodyPart messageBodyPart3 = new MimeBodyPart();
            nameImage = "IMG_front_" + stringDate + ".jpg";
            DataSource source2 = new FileDataSource("/storage/emulated/0/Pictures/" + nameImage);
            messageBodyPart3.setDataHandler(new DataHandler(source2));
            messageBodyPart3.setFileName("Camera front.jpg");
            multipart.addBodyPart(messageBodyPart3);

            mimeMessage.setContent(multipart);
            Transport.send(mimeMessage);

            Log.i("hehe", "da gui email");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
