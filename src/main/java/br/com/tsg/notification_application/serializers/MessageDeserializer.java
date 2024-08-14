package br.com.tsg.notification_application.serializers;

import br.com.tsg.notification_application.collections.Message;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;

public class MessageDeserializer implements Deserializer<Message> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Message deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(data, Message.class);
        } catch (IOException e) {
            throw new RuntimeException("Error deserializing string to Message object, error: " + e.getMessage(), e);
        }
    }
}
