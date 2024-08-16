package br.com.tsg.notification_application.services;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.log4j.Log4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.apache.commons.codec.binary.Base64;

import java.io.*;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

@Service
public class EmailService {

    @Value("${google.gmail.credentials-file-path}")
    private String CREDENTIALS_FILE_PATH;

    @Value("${google.gmail.sender-email}")
    private String SENDER_EMAIL_ADDRESS;

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    private static final String APPLICATION_NAME = "mp-email-sender";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(GmailScopes.MAIL_GOOGLE_COM);

    public Credential getCredentials() throws IOException, GeneralSecurityException {
        logger.info("Obtaining Google Credentials for user: {}", SENDER_EMAIL_ADDRESS);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new FileReader(CREDENTIALS_FILE_PATH));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new File("tokens")))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder()
                .setPort(8888).build();

        logger.info("Credentials obtained successfully...");
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    private Gmail getGmailService() throws GeneralSecurityException, IOException {
        logger.info("Building Gmail...");
        Credential credential = getCredentials();
        return new Gmail.Builder(GoogleNetHttpTransport.newTrustedTransport(), JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private MimeMessage createEmail(String to, String from, String subject, String bodyText) throws MessagingException {
        logger.info("Building email to be sent...");

        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        MimeMessage email = new MimeMessage(session);
        email.setFrom(new InternetAddress(from));
        email.addRecipients(jakarta.mail.Message.RecipientType.TO, String.valueOf(new InternetAddress(to)));
        email.setSubject(subject);
        email.setText(bodyText);
        return email;
    }

    public void sendEmail(String to, String subject, String bodyText) throws MessagingException, IOException, GeneralSecurityException {
        logger.info("Starting to send email to: {}, from: {}", to, SENDER_EMAIL_ADDRESS);
        MimeMessage email = createEmail(to, SENDER_EMAIL_ADDRESS, subject, bodyText);
        sendMessage(getGmailService(), SENDER_EMAIL_ADDRESS, email);
    }

    private void sendMessage(Gmail gmailService, String senderEmailAddress, MimeMessage email) throws MessagingException, IOException {
        logger.info("Sending email...");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        email.writeTo(buffer);
        byte[] rawMessageBytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(rawMessageBytes);
        Message message = new Message();
        message.setRaw(encodedEmail);

        gmailService.users().messages().send(senderEmailAddress, message).execute();
        logger.info("Email sent successfully");
    }
}
