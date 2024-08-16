package br.com.tsg.notification_application.services;

import br.com.tsg.notification_application.collections.Message;
import br.com.tsg.notification_application.exceptions.EmailSendingException;
import br.com.tsg.notification_application.repositories.MessageRepository;
import jakarta.mail.MessagingException;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.MongoId;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Optional;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final EmailService emailService;

    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);

    public MessageService(final MessageRepository repository, EmailService emailService) {
        this.messageRepository = repository;
        this.emailService = emailService;
    }

    public void processMessage(Message message) throws EmailSendingException {
        logger.info("Processing new message...");
        Optional<Message> optMessage = messageRepository.findById(new ObjectId(message.getId()));
        if(optMessage.isPresent()){
            Message messageDocument = optMessage.get();

            try {
                emailService.sendEmail(message.getEmail(), "New contact form entry", message.getMessage());
            } catch (MessagingException | IOException | GeneralSecurityException e) {
                throw new EmailSendingException("Error during the process of sending the email: " + e.getMessage(), e);
            }
            logger.info("Message sent, updating its status and persisting...");
            messageDocument.setNotified(true);
            messageRepository.save(messageDocument);
        } else {
            logger.error("Message with id: {} not found.", message.getId());
        }
    }

}