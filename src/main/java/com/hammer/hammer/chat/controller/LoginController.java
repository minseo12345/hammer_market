package com.hammer.hammer.chat.controller;
import com.hammer.hammer.chat.Entity.User;
import com.hammer.hammer.chat.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Slf4j
@RestController
@RequestMapping("/api")
public class LoginController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @PostMapping("/login")
    public User login(@RequestBody User loginUser, HttpSession session) {
        User user = userRepository.findByUsername(loginUser.getUsername());
        log.info("login user: " + loginUser.getUsername()+", "+loginUser.getPassword());
        log.info("find user:"+user.getUsername());
        if (user != null && user.getPassword().equals(loginUser.getPassword())) {
            session.setAttribute("user", user);
            return user;
        }
        throw new RuntimeException("Invalid username or password");
    }
    @GetMapping("/session-user")
    public ResponseEntity<?> getSessionUser(HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("No active session or user not logged in");
        }
        return ResponseEntity.ok(currentUser);
    }
}