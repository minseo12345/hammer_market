package com.hammer.hammer.chat.controller;

import com.hammer.hammer.chat.entity.Message;
import com.hammer.hammer.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class WebSocketController {
    private final ChatService chatService;

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public Message sendMessage(Message message) {
        return chatService.saveMessage(message.getChatRoomId(), message.getSenderId(), message.getContent());
    }
}
