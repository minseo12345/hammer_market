package com.hammer.hammer.chat.controller;

import com.hammer.hammer.user.entity.User;
import com.hammer.hammer.user.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ApiController {

    private final UserRepository userRepository;

    public ApiController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/api/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    @GetMapping("/api/session-user")
    public ResponseEntity<User> getSessionUser() {
        return null;
        /*User currentUser =
        return ResponseEntity.ok(currentUser);*/
    }
}
