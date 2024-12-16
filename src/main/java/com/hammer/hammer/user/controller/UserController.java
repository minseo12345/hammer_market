package com.hammer.hammer.user.controller;

import com.hammer.hammer.user.dto.UserDto;
import com.hammer.hammer.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Slf4j
@Controller
public class UserController {
    private final UserService userService;

    @GetMapping("/login")
    public String login() {
        return "/user/login";
    }

    @GetMapping("/signup")
    public String signup() {
        return "/user/signUp";
    }

    @PostMapping("/user")
    public String signup(UserDto userDto){
        System.out.println("############" + userDto.toString());
        userService.save(userDto); // 회원가입 메서드 호출
        return "redirect:/login"; // 회원가입이 완료된 이후 로그인 페이지로 이동
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response){
        new SecurityContextLogoutHandler().logout(
                request,
                response,
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );
        return "redirect:/login";
    }
}
