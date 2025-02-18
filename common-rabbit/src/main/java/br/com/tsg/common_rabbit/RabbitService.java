package br.com.tsg.common_rabbit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class RabbitService implements AutoCloseable {
    private final Logger logger = LoggerFactory.getLogger(RabbitService.class);

    private final Connection connection;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public RabbitService() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setRequestedHeartbeat(30);
        connection = factory.newConnection();
    }

    public <T> void registerConsumer(String queueName, Class<T> messageType, MessageHandler<T> handler) throws IOException {
        try {
            Channel channel = connection.createChannel();
            channel.basicConsume(queueName, true, (consumerTag, message) -> {
                try {
                    String body = new String(message.getBody(), StandardCharsets.UTF_8);
                    T deserializedMessage = new ObjectMapper().readValue(body, messageType); // Deserialize the message
                    handler.handle(deserializedMessage); // Process the message
                } catch (Exception e) {
                    logger.error("Error while handling message: {}", e.getMessage(), e);
                }
            }, consumerTag -> logger.info("Consumer {} cancelled", consumerTag));
        } catch (Exception e) {
            logger.error("Error while registering new consumer: ", e);
        }
    }

    @Override
    public void close() throws IOException, TimeoutException {
        if (connection != null && connection.isOpen()) {
            connection.close();
        }
    }

    public <T> void publishMessage(String queueName, T message) throws IOException {
        Channel channel = connection.createChannel();
        channel.queueDeclare(queueName, true, false, false, null);
        try {
            String messageJson = objectMapper.writeValueAsString(message);

            channel.basicPublish("", queueName, null, messageJson.getBytes());
            logger.info("Message published to queue [{}]: {}", queueName, messageJson);
        } catch (JsonProcessingException e) {
            throw  new IOException("Error serializing message to json.", e);
        }
    }

    @FunctionalInterface
    public interface MessageHandler<T> {
        void handle(T message) throws Exception;
    }
}
