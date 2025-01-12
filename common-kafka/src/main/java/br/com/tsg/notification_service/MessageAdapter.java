package br.com.tsg.notification_service;

import com.google.gson.*;

import java.lang.reflect.Type;

public class MessageAdapter<T> implements JsonSerializer<KafkaMessage<T>>, JsonDeserializer<KafkaMessage<T>> {
    @Override
    public KafkaMessage<T> deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        var obj = jsonElement.getAsJsonObject();
        var payloadType =  obj.get("type").getAsString();
        var correlationId = (CorrelationId) context.deserialize(obj.get("correlationId"), CorrelationId.class);
        try {
            // maybe you want to use an "accept list"
            var payload = context.deserialize(obj.get("payload"), Class.forName(payloadType));
            return new KafkaMessage(payloadType, correlationId, payload);
        } catch (ClassNotFoundException e) {
            // you might want to deal with this exception
            throw new JsonParseException(e);
        }
    }

    @Override
    public JsonElement serialize(KafkaMessage message, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        obj.addProperty("type", message.getPayload().getClass().getName());
        obj.add("payload", context.serialize(message.getPayload()));
        obj.add("correlationId", context.serialize(message.getId()));
        return obj;
    }
}
