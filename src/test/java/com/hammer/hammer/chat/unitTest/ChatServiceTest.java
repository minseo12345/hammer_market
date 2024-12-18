package com.hammer.hammer.chat.unitTest;

import com.hammer.hammer.chat.Entity.ChatRoom;
import com.hammer.hammer.chat.Entity.Message;
import com.hammer.hammer.chat.Entity.User;
import com.hammer.hammer.chat.repository.ChatRoomRepository;
import com.hammer.hammer.chat.repository.MessageRepository;
import com.hammer.hammer.chat.repository.UserRepository;
import com.hammer.hammer.chat.service.ChatService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ChatServiceTest {

    @Autowired
    private ChatService chatService;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    private User seller;
    private User buyer;

    @BeforeEach
    void setUp() {
        seller = User.builder()
                .username("seller1")
                .password("1234")
                .build();
        buyer = User.builder()
                .username("buyer1")
                .password("1234")
                .build();

        userRepository.save(seller);
        userRepository.save(buyer);


    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        chatRoomRepository.deleteAll();
        messageRepository.deleteAll();
    }

    @Test
    void testFindOrCreateChatRoom() {
        // Given
        String sellerId = seller.getId();
        String buyerId = buyer.getId();

        // When
        ChatRoom createChatRoom = chatService.findOrCreateChatRoom(sellerId, buyerId);//만들어진 채팅방
        ChatRoom findChatRoom = chatService.findOrCreateChatRoom(sellerId, buyerId);//이미 만들어진 채팅방을 찾음

        // Then
        assertNotNull(createChatRoom);
        assertEquals(sellerId, createChatRoom.getSellerId());
        assertEquals(buyerId, createChatRoom.getBuyerId());

        assertNotNull(findChatRoom);
        assertEquals(sellerId, findChatRoom.getSellerId());
        assertEquals(buyerId, findChatRoom.getBuyerId());

        assertEquals(createChatRoom.getId(), findChatRoom.getId()); // 같은 채팅방인지 비교

        ChatRoom savedChatRoom = chatRoomRepository.findBySellerIdAndBuyerId(sellerId, buyerId);
        assertNotNull(savedChatRoom);
    }

    @Test
    void testGetMessagesByChatRoom() {
        // Given
        ChatRoom chatRoom = chatService.findOrCreateChatRoom(seller.getId(), buyer.getId());
        Message message1 = Message.builder()
                .chatRoomId(chatRoom.getId())
                .senderId(buyer.getId())
                .content("hello")
                .build();
        Message message2 = Message.builder()
                .chatRoomId(chatRoom.getId())
                .senderId(seller.getId())
                .content("nice to meet you")
                .build();
        messageRepository.save(message1);
        messageRepository.save(message2);

        // When
        List<Message> messages = chatService.getMessagesByChatRoom(chatRoom.getId());

        // Then
        assertNotNull(messages);
        assertFalse(messages.isEmpty());
        assertEquals(2, messages.size());
        assertEquals("hello", messages.get(0).getContent());
        assertEquals("nice to meet you", messages.get(1).getContent());
    }

    @Test
    void testSaveMessage() {
        // Given
        ChatRoom chatRoom = chatService.findOrCreateChatRoom(seller.getId(), buyer.getId());
        String content1 = "New Message1";
        String content2 = "New Message2";

        // When
        Message savedMessage1 = chatService.saveMessage(chatRoom.getId(), seller.getId(), content1);
        Message savedMessage2 = chatService.saveMessage(chatRoom.getId(), buyer.getId(), content2);

        // Then
        assertNotNull(savedMessage1);
        assertEquals(content1, savedMessage1.getContent());
        assertEquals(chatRoom.getId(), savedMessage1.getChatRoomId());

        assertNotNull(savedMessage2);
        assertEquals(content2, savedMessage2.getContent());
        assertEquals(chatRoom.getId(), savedMessage2.getChatRoomId());

        List<Message> messages = messageRepository.findByChatRoomId(chatRoom.getId());
        assertFalse(messages.isEmpty());
        assertEquals(2, messages.size());
        assertEquals(content1, messages.get(0).getContent());
        assertEquals(content2, messages.get(1).getContent());
    }

    @Test
    void testGetChatRooms() {
        // Given
        chatService.findOrCreateChatRoom(seller.getId(), buyer.getId());
        chatService.findOrCreateChatRoom(buyer.getId(), seller.getId());
        // When
        List<ChatRoom> chatRooms = chatService.getChatRooms(seller.getId());

        // Then
        assertNotNull(chatRooms);
        assertFalse(chatRooms.isEmpty());
        assertEquals(2, chatRooms.size());
        assertEquals(seller.getId(), chatRooms.get(0).getSellerId());
        assertEquals(seller.getId(), chatRooms.get(1).getBuyerId());
    }

    @Test
    void testGetUnreadCount() {
        // Given
        ChatRoom chatRoom = chatService.findOrCreateChatRoom(seller.getId(), buyer.getId());
        Message unreadMessage1 = Message.builder()
                .chatRoomId(chatRoom.getId())
                .senderId(seller.getId())
                .content("hello")
                .build();
        Message unreadMessage2 = Message.builder()
                .chatRoomId(chatRoom.getId())
                .senderId(seller.getId())
                .content("hello2")
                .build();
        messageRepository.save(unreadMessage1);
        messageRepository.save(unreadMessage2);
        // When
        int unreadCount = chatService.getUnreadCount(chatRoom.getId(), buyer.getId());

        // Then
        assertEquals(2, unreadCount);
    }

    @Test
    void testMarkMessagesAsRead() {
        // Given
        ChatRoom chatRoom = chatService.findOrCreateChatRoom(seller.getId(), buyer.getId());
        Message unreadMessage1 = Message.builder()
                .chatRoomId(chatRoom.getId())
                .senderId(buyer.getId())
                .content("hello")
                .build();
        Message unreadMessage2 = Message.builder()
                .chatRoomId(chatRoom.getId())
                .senderId(buyer.getId())
                .content("hello2")
                .build();

        messageRepository.save(unreadMessage1);
        messageRepository.save(unreadMessage2);

        // When
        chatService.markMessagesAsRead(chatRoom.getId(), seller.getId());

        // Then
        List<Message> messages = messageRepository.findByChatRoomId(chatRoom.getId());
        assertTrue(messages.stream().allMatch(Message::isReadStatus));
    }
}
