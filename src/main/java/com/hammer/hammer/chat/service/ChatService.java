package com.hammer.hammer.chat.service;
import com.hammer.hammer.chat.Entity.ChatRoom;
import com.hammer.hammer.chat.Entity.Message;
import com.hammer.hammer.chat.Entity.User;
import com.hammer.hammer.chat.repository.ChatRoomRepository;
import com.hammer.hammer.chat.repository.MessageRepository;
import com.hammer.hammer.chat.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Autowired
    public ChatService(ChatRoomRepository chatRoomRepository, MessageRepository messageRepository, UserRepository userRepository) {
        this.chatRoomRepository = chatRoomRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }

    public ChatRoom findOrCreateChatRoom(String sellerId, String buyerId) {
        ChatRoom chatRoom = chatRoomRepository.findBySellerIdAndBuyerId(sellerId, buyerId);
        User seller=userRepository.findById(sellerId).orElse(null);
        User buyer=userRepository.findById(buyerId).orElse(null);
        if (chatRoom == null) {
            chatRoom = ChatRoom.builder()
                    .title(seller.getUsername()+"님 과"+buyer.getUsername()+"의 채팅방")
                    .sellerId(sellerId)
                    .buyerId(buyerId)
                    .buyerUnreadCount(0)
                    .sellerUnreadCount(0)
                    .build();
            chatRoom = chatRoomRepository.save(chatRoom);
        }
        return chatRoom;
    }

    public List<Message> getMessagesByChatRoom(String chatRoomId,String userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("ChatRoom not found"));
        if (userId.equals(chatRoom.getSellerId())) {
            chatRoom.setSellerUnreadCount(0);
        } else if (userId.equals(chatRoom.getBuyerId())) {
            chatRoom.setBuyerUnreadCount(0);
        }
        chatRoomRepository.save(chatRoom);
        return messageRepository.findByChatRoomId(chatRoomId);
    }

    public Message saveMessage(String chatRoomId, String senderId, String content) {
        Message message = Message.builder()
                .chatRoomId(chatRoomId)
                .senderId(senderId)
                .content(content)
                .readStatus(false)
                .timestamp(LocalDateTime.now())
                .build();

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new RuntimeException("ChatRoom not found"));

        // 발신자에 따라 상대방의 UnreadCount를 증가
        if (message.getSenderId().equals(chatRoom.getSellerId())) {
            chatRoom.setBuyerUnreadCount(chatRoom.getBuyerUnreadCount() + 1);
        } else if (message.getSenderId().equals(chatRoom.getBuyerId())) {
            chatRoom.setSellerUnreadCount(chatRoom.getSellerUnreadCount() + 1);
        }

        chatRoomRepository.save(chatRoom);
        return messageRepository.save(message);
    }

    public List<ChatRoom> getChatRooms(String userId) {
        return chatRoomRepository.findByBuyerIdOrSellerId(userId, userId);
    }

    public int getUnreadCount(String roomId, String userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("ChatRoom not found"));

        if (userId.equals(chatRoom.getSellerId())) {
            return chatRoom.getSellerUnreadCount();
        } else if (userId.equals(chatRoom.getBuyerId())) {
            return chatRoom.getBuyerUnreadCount();
        }
        return 0; // 사용자 ID가 일치하지 않으면 0 반환
    }

    public void markMessagesAsRead(String roomId,String userId){
        List<Message> messages = messageRepository.findById(roomId).stream().toList();
        for(Message message:messages){
            if(!message.getSenderId().equals(userId)){
                message.setReadStatus(true);
            }
        }
    }
}
