package br.com.tsg.notification_application.collections;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.FieldType;
import org.springframework.data.mongodb.core.mapping.MongoId;

@Document("Message")
@Getter @Setter
public class Message {
    @MongoId(FieldType.OBJECT_ID)
    private String id;
    private String name;
    private String email;
    private String phone;
    private String message;
    private Boolean notified;
}
