package com.hammer.hammer.user.controller;

import com.hammer.hammer.user.entity.User;
import com.hammer.hammer.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Slf4j
@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) { this.userService = userService; }

    @GetMapping("/login")
    public String login() {
//        if (request.getUserPrincipal() != null) {
//            return "redirect:/board/list";
//        }
        return "user/login";
    }

    @GetMapping("/sign-up")
    public String signUp() {
        return "user/signUp";
    }

//    @PostMapping("/sign-up")
//    public String signUpProcess(@Valid SignUpDTO dto, RedirectAttributes redirectAttributes) {
//
//        userService.signUpProcess(dto);
//
//        redirectAttributes
//                .addFlashAttribute("message", "환영합니다, " + dto.getUserName() + "님!");
//
//        return "redirect:/user/login";
//    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public Optional<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @PostMapping("/sign-up")
    public String save(@ModelAttribute User user) {
        System.out.println("########## user #########" +  user.toString());
        userService.save(user);
        return "redirect:/login";
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
