package com.hammer.hammer.main.controller;

import com.hammer.hammer.user.entity.User;
import com.hammer.hammer.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("/main")
public class MainController {

    private final UserService userService;

    @GetMapping
    public String mainPage(Model model) {
        // "main.html" 파일을 반환
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.parseLong(authentication.getName());

        User user = userService.getUserById(userId);

        System.out.println("########user id########" +user.getUserId());
        System.out.println("########user name########" +user.getUsername());
        System.out.println("########user email########" +user.getEmail());

        model.addAttribute("user", user);

        return "/main/main";
    }
}
