package com.hammer.hammer.chat.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/chat")
public class ChatController {

    @GetMapping
    public String chat() {
        return "/chat/chat";
    }
    @GetMapping("/cart")
    public String cart() {
        return "/cart/cart";
    }
}
