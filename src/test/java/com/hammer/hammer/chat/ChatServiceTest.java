package com.hammer.hammer.chat;

import com.hammer.hammer.chat.entity.ChatRoom;
import com.hammer.hammer.chat.entity.Message;
import com.hammer.hammer.chat.repository.ChatRoomRepository;
import com.hammer.hammer.chat.repository.MessageRepository;
import com.hammer.hammer.chat.service.ChatService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;



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

    private Long sellerId;
    private Long buyerId;

    @BeforeEach
    void setUp() {
        sellerId = 1L;
        buyerId = 2L;

    }

    @AfterEach
    void tearDown() {
        chatRoomRepository.deleteAll();
        messageRepository.deleteAll();
    }

    @Test
    void testFindOrCreateChatRoom() {
        // Given


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
        ChatRoom chatRoom = chatService.findOrCreateChatRoom(sellerId, buyerId);
        Message message1 = Message.builder()
                .chatRoomId(chatRoom.getId())
                .senderId(buyerId)
                .content("hello")
                .build();
        Message message2 = Message.builder()
                .chatRoomId(chatRoom.getId())
                .senderId(sellerId)
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
        ChatRoom chatRoom = chatService.findOrCreateChatRoom(sellerId, buyerId);
        String content1 = "New Message1";
        String content2 = "New Message2";

        // When
        Message savedMessage1 = chatService.saveMessage(chatRoom.getId(), sellerId, content1);
        Message savedMessage2 = chatService.saveMessage(chatRoom.getId(), buyerId, content2);

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
        chatService.findOrCreateChatRoom(sellerId, buyerId);
        chatService.findOrCreateChatRoom(buyerId, sellerId);
        // When
        List<ChatRoom> chatRooms = chatService.getChatRooms(sellerId);

        // Then
        assertNotNull(chatRooms);
        assertFalse(chatRooms.isEmpty());
        assertEquals(2, chatRooms.size());
        assertEquals(sellerId, chatRooms.get(0).getSellerId());
        assertEquals(sellerId, chatRooms.get(1).getBuyerId());
    }

    @Test
    void testGetUnreadCount() {
        // Given
        ChatRoom chatRoom = chatService.findOrCreateChatRoom(sellerId, buyerId);
        Message unreadMessage1 = Message.builder()
                .chatRoomId(chatRoom.getId())
                .senderId(sellerId)
                .content("hello")
                .build();
        Message unreadMessage2 = Message.builder()
                .chatRoomId(chatRoom.getId())
                .senderId(sellerId)
                .content("hello2")
                .build();
        messageRepository.save(unreadMessage1);
        messageRepository.save(unreadMessage2);
        // When
        int unreadCount = chatService.getUnreadCount(chatRoom.getId(), buyerId);

        // Then
        assertEquals(2, unreadCount);
    }

    @Test
    void testMarkMessagesAsRead() {
        // Given
        ChatRoom chatRoom = chatService.findOrCreateChatRoom(sellerId, buyerId);
        Message unreadMessage1 = Message.builder()
                .chatRoomId(chatRoom.getId())
                .senderId(buyerId)
                .content("hello")
                .build();
        Message unreadMessage2 = Message.builder()
                .chatRoomId(chatRoom.getId())
                .senderId(buyerId)
                .content("hello2")
                .build();

        messageRepository.save(unreadMessage1);
        messageRepository.save(unreadMessage2);

        // When
        chatService.markMessagesAsRead(chatRoom.getId(), sellerId);

        // Then
        List<Message> messages = messageRepository.findByChatRoomId(chatRoom.getId());
        assertTrue(messages.stream().allMatch(Message::isReadStatus));
    }
    /*@Test//test를 위해 10초로 설정 이후에 30일로 변경할 예정
    void testAutoDeletionAfter10Seconds() throws InterruptedException {
        // Given
        ChatRoom chatRoom = chatService.findOrCreateChatRoom(sellerId, buyerId);
        Message message = Message.builder()
                .chatRoomId(chatRoom.getId())
                .senderId(sellerId)
                .content("Test message")
                .build();
        messageRepository.save(message);

        // Ensure initial persistence
        assertNotNull(chatRoomRepository.findById(chatRoom.getId()));
        assertNotNull(messageRepository.findById(message.getId()));

        try {
            Thread.sleep(30000); // 30초 동안 멈춤
        } catch (InterruptedException e) {
            e.printStackTrace(); // 인터럽트 예외 처리
        }

        // When
        ChatRoom deletedChatRoom = chatRoomRepository.findById(chatRoom.getId()).orElse(null);
        Message deletedMessage = messageRepository.findById(message.getId()).orElse(null);

        // Then
        assertNull(deletedChatRoom, "ChatRoom should be deleted after 10 seconds");
        assertNull(deletedMessage, "Message should be deleted after 10 seconds");
    }*/

    @Test
    void testChatRoomUpdatedAtOnMessageSend() throws InterruptedException {
        // Given
        ChatRoom chatRoom = chatService.findOrCreateChatRoom(sellerId, buyerId);
        assertNotNull(chatRoom);
        long initialUpdatedAt = chatRoom.getUpdatedAt().getSecond(); // 초기 updatedAt 시간 저장

        // 잠시 대기하여 updatedAt의 시간 차이를 확인할 수 있도록 함
        Thread.sleep(1000); // 1초 대기

        // When: 새로운 메시지 전송
        Message message = chatService.saveMessage(chatRoom.getId(), sellerId, "Test Message");
        assertNotNull(message);

        // Then: ChatRoom의 updatedAt 필드가 최신화되었는지 확인
        ChatRoom updatedChatRoom = chatRoomRepository.findById(chatRoom.getId()).orElse(null);
        assertNotNull(updatedChatRoom);

        long updatedAtAfterMessage = updatedChatRoom.getUpdatedAt().getSecond();
        assertTrue(updatedAtAfterMessage > initialUpdatedAt, "updatedAt이 최신화되지 않았습니다.");

    }
}
