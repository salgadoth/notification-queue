package br.com.tsg.notification_application;

import br.com.tsg.notification_application.collections.Message;
import br.com.tsg.notification_application.exceptions.EmailSendingException;
import br.com.tsg.notification_application.services.EmailService;
import br.com.tsg.notification_application.services.MessageService;
import br.com.tsg.notification_service.KafkaService;
import br.com.tsg.notification_service.KafkaMessage;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@SpringBootApplication
public class EmailServiceApplication {
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceApplication.class);

    private final MessageService messageService;

    public EmailServiceApplication(MessageService messageService) {
        this.messageService = messageService;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        ConfigurableApplicationContext ctx = SpringApplication.run(EmailServiceApplication.class);
        EmailServiceApplication emailServiceApplication = ctx.getBean(EmailServiceApplication.class);

        try(var service = new KafkaService<>(EmailServiceApplication.class.getSimpleName(), "QUEUE_NEW_MESSAGE", emailServiceApplication::parse, Map.of())) {
            service.run();
        }
    }

    private void parse(ConsumerRecord<String, KafkaMessage<Message>> record){
        Message message = record.value().getPayload();
        try {
            messageService.processMessage(message);
        } catch (EmailSendingException e) {
            // ignoring
            e.printStackTrace();
        }
    }
}