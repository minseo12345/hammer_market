package com.hammer.hammer.chat.repository;
import com.hammer.hammer.chat.entity.ChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
    ChatRoom findBySellerIdAndBuyerId(Long sellerId, Long buyerId);
    List<ChatRoom> findByBuyerIdOrSellerId(Long buyerId, Long sellerId);
}