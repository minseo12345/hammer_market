package com.hammer.hammer.chat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String loginPage() {
        return "login"; // login.html 렌더링
    }

    @GetMapping("/chat")
    public String chatPage(Model model) {
        return "chat"; // chat.html 렌더링
    }
}
