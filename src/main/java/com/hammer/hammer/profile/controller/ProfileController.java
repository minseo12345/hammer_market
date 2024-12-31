package com.hammer.hammer.profile.controller;

import com.hammer.hammer.user.entity.User;
import com.hammer.hammer.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {
    private final UserService userService;

    @GetMapping("/myProfile/{userId}")
    public String profile(@PathVariable Long userId, Model model){
        User userInfo = userService.getUserById(userId);
        model.addAttribute("userInfo", userInfo);
        return "/profile/myProfile";
    }
}
