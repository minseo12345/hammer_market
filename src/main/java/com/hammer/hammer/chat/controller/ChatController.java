package com.hammer.hammer.chat.controller;
import com.hammer.hammer.chat.Entity.ChatRoom;
import com.hammer.hammer.chat.Entity.Message;
import com.hammer.hammer.chat.Entity.User;
import com.hammer.hammer.chat.service.ChatService;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@RestController
public class ChatController {

    private final ChatService chatService;

    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public Message sendMessage(Message message) {
        return chatService.saveMessage(message.getChatRoomId(), message.getSenderId(), message.getContent());
    }

    @GetMapping("/chat/{user1Id}/{user2Id}")
    public ResponseEntity<ChatRoom> getChatRoom(@PathVariable String user1Id, @PathVariable String user2Id) {
        List<String> sortedIds = Arrays.asList(user1Id, user2Id);
        Collections.sort(sortedIds);
        String normalizedUser1Id = sortedIds.get(0);
        String normalizedUser2Id = sortedIds.get(1);

        ChatRoom chatRoom = chatService.findOrCreateChatRoom(normalizedUser1Id, normalizedUser2Id);

        return ResponseEntity.ok(chatRoom);
    }
    @GetMapping("/chat/{chatRoomId}")
    public ResponseEntity<List<Message>> getMessagesByChatRoom(@PathVariable String chatRoomId) {
        List<Message> messages= chatService.getMessagesByChatRoom(chatRoomId);
        if(messages==null) {
            log.info("messages is null");
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(messages);
    }
    @GetMapping("/api/chatrooms")
    public ResponseEntity<List<ChatRoom>> getUserChatRooms(HttpSession session) {
        // 세션에서 현재 로그인된 사용자 정보 가져오기
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }

        List<ChatRoom> chatRooms = chatService.getChatRooms(currentUser.getId());

        return ResponseEntity.ok(chatRooms);
    }
    @GetMapping("/api/chatrooms/{roomId}/unreadCount")
    public ResponseEntity<Integer> getUnreadCount(@PathVariable String roomId, @RequestParam String userId) {
        return ResponseEntity.ok(chatService.getUnreadCount(roomId, userId));
    }

    @PostMapping("/chat/{roomId}/read")
    public ResponseEntity<Void> markMessagesAsRead(
            @PathVariable String roomId,
            @RequestParam String userId) {
        // 읽지 않은 메시지 상태를 업데이트
        chatService.markMessagesAsRead(roomId, userId);

        return ResponseEntity.ok().build();
    }
}
