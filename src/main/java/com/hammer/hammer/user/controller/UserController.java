package com.hammer.hammer.user.controller;

import com.hammer.hammer.user.dto.UserDto;
import com.hammer.hammer.user.service.UserService;
import jakarta.servlet.http.Cookie;
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

    // 로그인 화면
    @GetMapping("/login")
    public String login() {
        return "/user/login";
    }

    // 회원가입 화면
    @GetMapping("/signup")
    public String signup() {
        return "/user/signUp";
    }

    // 회원가입
    @PostMapping("/user")
    public String signup(UserDto userDto){
        userService.save(userDto); // 회원가입 메서드 호출
        return "redirect:/login"; // 회원가입이 완료된 이후 로그인 페이지로 이동
    }


    // 로그아웃
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

    // 아이디 찾기
    @GetMapping("/login/find-id")
    public String findId() {
        return "/user/findId";
    }

    // 비밀번호 찾기
    @GetMapping("/login/find-pw")
    public String findPw() {
        return "/user/findPw";
    }
}
