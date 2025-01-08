package com.hammer.hammer.chat.service;
import com.hammer.hammer.chat.entity.ChatRoom;
import com.hammer.hammer.chat.entity.Message;

import com.hammer.hammer.chat.repository.ChatRoomRepository;
import com.hammer.hammer.chat.repository.CustomMessageRepository;
import com.hammer.hammer.chat.repository.MessageRepository;

import com.hammer.hammer.user.entity.User;
import com.hammer.hammer.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final CustomMessageRepository customMessageRepository;

    @Transactional
    public ChatRoom findOrCreateChatRoom(Long sellerId, Long buyerId) {
        ChatRoom chatRoom = chatRoomRepository.findBySellerIdAndBuyerId(sellerId, buyerId);
        if (chatRoom == null) {
            User seller = userRepository.findByUserId(sellerId)
                    .orElseThrow(() -> new EntityNotFoundException("Seller not found"));
            User buyer = userRepository.findByUserId(buyerId)
                    .orElseThrow(() -> new EntityNotFoundException("Buyer not found"));
            chatRoom = ChatRoom.builder()
                    .sellerTitle(buyer.getUsername())
                    .buyerTitle(seller.getUsername() )
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
    public Message saveMessage(String chatRoomId, Long senderId, String content) {
        Message message = Message.builder()
                .chatRoomId(chatRoomId)
                .senderId(senderId)
                .content(content)
                .build();

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new EntityNotFoundException("ChatRoom not found"));

        chatRoom.setUpdatedAt(message.getCreatedAt());
        chatRoomRepository.save(chatRoom);

        return messageRepository.save(message);
    }

    @Transactional(readOnly = true)
    public List<ChatRoom> getChatRooms(Long userId) {
        return chatRoomRepository.findByBuyerIdOrSellerId(userId, userId);
    }

    @Transactional(readOnly = true)
    public int getUnreadCount(String roomId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("ChatRoom not found"));
        if(chatRoom.getBuyerId().equals(userId)) {
            return messageRepository.countUnreadMessagesByChatRoomIdAndSenderId(roomId, chatRoom.getSellerId());
        }else{
            return messageRepository.countUnreadMessagesByChatRoomIdAndSenderId(roomId, chatRoom.getBuyerId());
        }
    }

    @Transactional
    public void markMessagesAsRead(String roomId, Long userId) {
        customMessageRepository.markMessagesAsRead(roomId, userId);
    }

    @Transactional
    public void deleteEveryThings(){
        messageRepository.deleteAll();
        chatRoomRepository.deleteAll();
    }
}
