package br.com.tsg.notification_application;

import br.com.tsg.common_rabbit.RabbitConsumerWorker;
import br.com.tsg.common_rabbit.RabbitService;
import br.com.tsg.notification_application.collections.Message;
import br.com.tsg.notification_application.collections.RabbitMessage;
import br.com.tsg.notification_application.exceptions.EmailSendingException;
import br.com.tsg.notification_application.services.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

@SpringBootApplication
public class EmailServiceApplication {
    private static final Logger logger = LoggerFactory.getLogger(EmailServiceApplication.class);
    private final MessageService messageService;

    public EmailServiceApplication(MessageService messageService) {
        this.messageService = messageService;
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(EmailServiceApplication.class);
        EmailServiceApplication emailServiceApplication = ctx.getBean(EmailServiceApplication.class);

        try {
            RabbitService rabbitService = new RabbitService();

            RabbitConsumerWorker<RabbitMessage> rabbitConsumerWorker = new RabbitConsumerWorker<>(
                    rabbitService,
                    "QUEUE_NEW_MESSAGE",
                    RabbitMessage.class,
                    emailServiceApplication::parse
            );

            rabbitConsumerWorker.start();
            logger.info("RabbitMQ consumer started, waiting for messages...");

            Runtime.getRuntime().addShutdownHook(
                    new Thread(() -> {
                        try {
                            rabbitConsumerWorker.close();
                            logger.info("Application shutdown gracefully.");
                        } catch (Exception e) {
                            logger.error("Error during shutdown: {}", e.getMessage());
                        }
                    })
            );
        } catch (Exception e) {
            logger.error("Failed to start RabbitMQ consumer", e);
        }
    }

    private void parse(RabbitMessage message) throws EmailSendingException {
        logger.info("Processing new message: {}", message.getCorrelationId());
        messageService.processMessage(message.getPayload());
    }
}