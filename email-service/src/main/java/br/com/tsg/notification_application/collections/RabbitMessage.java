package br.com.tsg.notification_application.collections;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RabbitMessage {
    private CorrelationId correlationId;
    private Message payload;
}

@Getter
@Setter
class CorrelationId {
    private String id;
}

