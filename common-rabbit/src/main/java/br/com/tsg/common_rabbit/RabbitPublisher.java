package br.com.tsg.common_rabbit;

import java.io.IOException;

public class RabbitPublisher<T> {
    private final RabbitService rabbitService;

    public RabbitPublisher(RabbitService rabbitService) {
        this.rabbitService = rabbitService;
    }

    public void sendMessage(String queueName, T message) throws IOException {
        rabbitService.publishMessage(queueName, message);
    }
}
