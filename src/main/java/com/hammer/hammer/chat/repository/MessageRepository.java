package com.hammer.hammer.chat.repository;
import com.hammer.hammer.chat.Entity.Message;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findByChatRoomId(String chatRoomId);

    @Query(value = "{ 'chatRoomId': ?0, 'senderId': ?1, 'readStatus': false }", count = true)
    int countUnreadMessagesByChatRoomIdAndSenderId(String chatRoomId, String senderId);
}