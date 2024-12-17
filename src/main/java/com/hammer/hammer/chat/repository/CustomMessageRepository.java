package com.hammer.hammer.chat.repository;
import com.hammer.hammer.chat.Entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
public class CustomMessageRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void markMessagesAsRead(String chatRoomId, String userId) {
        // Define the query
        Query query = new Query();
        query.addCriteria(Criteria.where("chatRoomId").is(chatRoomId)
                .and("senderId").ne(userId)
                .and("readStatus").is(false));

        // Define the update operation
        Update update = new Update();
        update.set("readStatus", true);

        // Perform the update
        mongoTemplate.updateMulti(query, update, Message.class);
    }
}
