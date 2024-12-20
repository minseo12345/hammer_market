package com.hammer.hammer.chat.controller;
import com.hammer.hammer.chat.entity.ChatRoom;
import com.hammer.hammer.chat.entity.ChatRoomDto;
import com.hammer.hammer.chat.entity.Message;

import com.hammer.hammer.chat.service.ChatService;
import com.hammer.hammer.user.entity.User;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public Message sendMessage(Message message) {
        return chatService.saveMessage(message.getChatRoomId(), message.getSenderId(), message.getContent());
    }

    @PostMapping("/chat/createChatRoom")//객체로 받기
    public ResponseEntity<ChatRoom> getChatRoom(@RequestBody ChatRoomDto request) {
        ChatRoom chatRoom = chatService.findOrCreateChatRoom(request.getSellerId(),request.getBuyerId());
        log.info("chatRoomId : {}", chatRoom.getId());
        return ResponseEntity.ok(chatRoom);
    }

    @GetMapping("/chat/{roomId}")
    public ResponseEntity<List<Message>> getMessagesByChatRoom(@PathVariable String roomId) {
        List<Message> messages= chatService.getMessagesByChatRoom(roomId);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/chat/chatrooms")
    public ResponseEntity<List<ChatRoom>> getUserChatRooms(HttpSession session) {
        // 세션에서 현재 로그인된 사용자 정보 가져오기
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not logged in");
        }
        List<ChatRoom> chatRooms = chatService.getChatRooms(currentUser.getUserId());
        return ResponseEntity.ok(chatRooms);
    }

    @GetMapping("/chat/chatrooms/{roomId}/unreadCount")
    public ResponseEntity<Integer> getUnreadCount(@PathVariable String roomId, @RequestParam Long userId) {
        return ResponseEntity.ok(chatService.getUnreadCount(roomId, userId));
    }

    @PostMapping("/chat/{roomId}/read")
    public ResponseEntity<Void> markMessagesAsRead(
            @PathVariable String roomId,
            @RequestParam Long userId) {
        // readStatus를 true로 업데이트
        chatService.markMessagesAsRead(roomId, userId);
        return ResponseEntity.ok().build();
    }
}
