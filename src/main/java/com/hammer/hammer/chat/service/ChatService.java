package com.hammer.hammer.chat.service;
import com.hammer.hammer.chat.Entity.ChatRoom;
import com.hammer.hammer.chat.Entity.Message;
import com.hammer.hammer.chat.Entity.User;
import com.hammer.hammer.chat.repository.ChatRoomRepository;
import com.hammer.hammer.chat.repository.CustomMessageRepository;
import com.hammer.hammer.chat.repository.MessageRepository;
import com.hammer.hammer.chat.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final CustomMessageRepository customMessageRepository;

    @Autowired
    public ChatService(ChatRoomRepository chatRoomRepository,
                       MessageRepository messageRepository,
                       UserRepository userRepository,
                       CustomMessageRepository customMessageRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.customMessageRepository = customMessageRepository;
    }

    @Transactional
    public ChatRoom findOrCreateChatRoom(String sellerId, String buyerId) {
        ChatRoom chatRoom = chatRoomRepository.findBySellerIdAndBuyerId(sellerId, buyerId);
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new RuntimeException("Seller not found"));
        User buyer = userRepository.findById(buyerId)
                .orElseThrow(() -> new RuntimeException("Buyer not found"));

        if (chatRoom == null) {
            chatRoom = ChatRoom.builder()
                    .title(seller.getUsername()+"님 과"+buyer.getUsername()+"의 채팅방")
                    .sellerId(sellerId)
                    .buyerId(buyerId)
                    .build();
            chatRoom = chatRoomRepository.save(chatRoom);
        }
        return chatRoom;
    }

    @Transactional(readOnly = true)
    public List<Message> getMessagesByChatRoom(String chatRoomId) {
        return messageRepository.findByChatRoomId(chatRoomId);
    }

    @Transactional
    public Message saveMessage(String chatRoomId, String senderId, String content) {
        Message message = Message.builder()
                .chatRoomId(chatRoomId)
                .senderId(senderId)
                .content(content)
                .build();

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("ChatRoom not found"));

        chatRoom.setUpdatedAt(LocalDateTime.now());
        chatRoomRepository.save(chatRoom);

        return messageRepository.save(message);
    }

    @Transactional(readOnly = true)
    public List<ChatRoom> getChatRooms(String userId) {
        return chatRoomRepository.findByBuyerIdOrSellerId(userId, userId);
    }

    @Transactional(readOnly = true)
    public int getUnreadCount(String roomId, String userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("ChatRoom not found"));
        if(chatRoom.getBuyerId().equals(userId)) {
            return messageRepository.countUnreadMessagesByChatRoomIdAndSenderId(roomId, chatRoom.getSellerId());
        }else{
            return messageRepository.countUnreadMessagesByChatRoomIdAndSenderId(roomId,chatRoom.getBuyerId());
        }
    }

    @Transactional
    public void markMessagesAsRead(String roomId, String userId) {
        customMessageRepository.markMessagesAsRead(roomId, userId);
    }
}
