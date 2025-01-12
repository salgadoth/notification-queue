package br.com.tsg.notification_service;

import lombok.Getter;

@Getter
public class KafkaMessage<T> {
    private String type;
    private final CorrelationId id;
    private final T payload;

    KafkaMessage(String type, CorrelationId id, T payload) {
        this.type = type;
        this.id = id;
        this.payload = payload;
    }

    @Override
    public String toString() {
        return "Message{" +
                "type=" + type +
                "id=" + id +
                ", payload=" + payload +
                '}';
    }
}
