package com.hammer.hammer.admin.controller;

import com.hammer.hammer.global.jwt.dto.JwtTokenResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {

    @GetMapping("/admin")
    public ResponseEntity<String> adminPage() {
        // 간단한 JSON 응답 생성
        return ResponseEntity.ok("message");
    }
//
//	@GetMapping("/users")
//	public List<User> getAllUsers(){
//		return adminService.getAllUser();
//	}

}
