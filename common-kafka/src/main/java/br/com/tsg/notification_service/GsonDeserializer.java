package br.com.tsg.notification_service;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.kafka.common.serialization.Deserializer;

import java.lang.reflect.Type;

public class GsonDeserializer<T> implements Deserializer<KafkaMessage<T>> {
    private final Gson gson = new GsonBuilder().registerTypeAdapter(KafkaMessage.class, new MessageAdapter<T>()).create();

    @Override
    public KafkaMessage<T> deserialize(String topic, byte[] data) {
        Type type = new TypeToken<KafkaMessage<T>>() {}.getType();
        return gson.fromJson(new String(data), type);
    }
}
