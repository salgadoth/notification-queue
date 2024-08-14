package br.com.tsg.notification_application.repositories;

import br.com.tsg.notification_application.collections.Message;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MessageRepository extends MongoRepository<Message, ObjectId> {
}
