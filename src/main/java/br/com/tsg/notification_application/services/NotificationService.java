package br.com.tsg.notification_application.services;

import br.com.tsg.notification_application.collections.Message;
import br.com.tsg.notification_application.exceptions.EmailSendingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    private final MessageService messageService;

    public NotificationService(final MessageService messageService){
        this.messageService = messageService;
    }

    @KafkaListener(topics = "QUEUE_NEW_MESSAGE", groupId = "message-process-group")
    public void listen(Message message) throws EmailSendingException {
        logger.info("Recieved new message: {}", message);
        messageService.processMessage(message);
    }
}
