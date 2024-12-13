package com.hammer.hammer.chat.repository;
import com.hammer.hammer.chat.Entity.ChatRoom;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
    ChatRoom findBySellerIdAndBuyerId(String sellerId, String buyerId);
    List<ChatRoom> findByBuyerIdOrSellerId(String buyerId, String sellerId);
}