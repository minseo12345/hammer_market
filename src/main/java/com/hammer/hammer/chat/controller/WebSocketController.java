package com.hammer.hammer.chat.controller;

import com.hammer.hammer.chat.entity.Message;
import com.hammer.hammer.chat.service.ChatService;
import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class WebSocketController {
    private final ChatService chatService;

    @MessageMapping("/chat")
    @SendTo("/topic/messages")
    public Message sendMessage(@Valid Message message, BindingResult result) {
        if (result.hasErrors()) {
            log.info("valid error occured: {}", result.getAllErrors());
            throw new ValidationException(result.getAllErrors().toString());
        }
        return chatService.saveMessage(message.getChatRoomId(), message.getSenderId(), message.getContent());
    }
}
