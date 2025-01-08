package com.hammer.hammer.profile.controller;

import com.hammer.hammer.profile.dto.ProfileUpdateRequestDto;
import com.hammer.hammer.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/profile")
public class ProfileApiController {
    private final UserService userService;

    @PostMapping("/edit")
    public ResponseEntity<?> updateProfile(@RequestBody ProfileUpdateRequestDto request,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        try {
            Long userId = Long.valueOf(userDetails.getUsername());
            userService.updateProfile(userId, request);
            return ResponseEntity.ok().body(Map.of("message", "회원정보가 성공적으로 수정되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "회원정보 수정 중 오류가 발생했습니다."));
        }
    }
}