package com.hammer.hammer.chat.controller;
import com.hammer.hammer.chat.entity.ChatRoom;
import com.hammer.hammer.chat.entity.ChatRoomDto;
import com.hammer.hammer.chat.entity.Message;

import com.hammer.hammer.chat.service.ChatService;
import com.hammer.hammer.user.entity.User;
import com.hammer.hammer.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@RequestMapping("/api")
@RestController
@RequiredArgsConstructor
public class ChatApiController {

    private final UserRepository userRepository;
    private final ChatService chatService;

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
    public ResponseEntity<List<ChatRoom>> getUserChatRooms() {
        // 세션에서 현재 로그인된 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long currentUserId = Long.parseLong(authentication.getName());
        User currentUser = userRepository.findByUserId(currentUserId).orElse(null);


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
