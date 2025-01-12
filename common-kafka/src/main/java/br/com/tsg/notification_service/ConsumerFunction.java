package br.com.tsg.notification_service;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface ConsumerFunction<T> {
    void consume(ConsumerRecord<String, KafkaMessage<T>> record) throws Exception;
}
