package com.hammer.hammer.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    @GetMapping("/")
    public String loginPage() {
        return "chat/login"; // login.html 렌더링
    }

    @GetMapping("/chat")
    public String chatPage() {
        return "chat/chat"; // chat.html 렌더링
    }
}
