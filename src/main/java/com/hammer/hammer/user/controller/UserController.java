package com.hammer.hammer.user.controller;

import com.hammer.hammer.user.dto.UserDto;
import com.hammer.hammer.user.entity.User;
import com.hammer.hammer.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Slf4j
@Controller
public class UserController {
    private final UserService userService;

    // 로그인 화면
    @GetMapping("/login")
    public String login(@AuthenticationPrincipal UserDetails userDetails) {
        if(userDetails == null) {
            return "user/login";
        }
        return "item/list";
    }

    // 회원가입 화면
    @GetMapping("/signup")
    public String signup() {
        return "user/signUp";
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
        return "user/findId";
    }

    // 비밀번호 찾기
    @GetMapping("/login/find-pw")
    public String findPw() {
        return "user/findPw";
    }

    @GetMapping("/login/changePw")
    public String changePw(@RequestParam(value = "email", required = false)String email, Model model
    , @AuthenticationPrincipal UserDetails userDetails) {

        if(email == null) {
            Long userId = Long.valueOf(userDetails.getUsername());
            User userInfo = userService.getUserById(userId);
            model.addAttribute("userEmail", userInfo.getEmail());
            return "user/changePw";
        }
        model.addAttribute("userEmail", email);
        return "user/changePw";

    }
}
