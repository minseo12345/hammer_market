package com.hammer.hammer.chat.repository;
import com.hammer.hammer.chat.entity.Message;
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

    public void markMessagesAsRead(String chatRoomId, Long userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("chatRoomId").is(chatRoomId)
                .and("senderId").ne(userId)
                .and("readStatus").is(false));

        Update update = new Update();
        update.set("readStatus", true);

        mongoTemplate.updateMulti(query, update, Message.class);
    }
}
