package com.hammer.hammer.global.jwt.controller;


import com.hammer.hammer.global.jwt.dto.JwtTokenDto;
import com.hammer.hammer.global.jwt.dto.JwtTokenLoginRequest;
import com.hammer.hammer.global.jwt.dto.JwtTokenResponse;
import com.hammer.hammer.user.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
public class JwtRestController {

    private final UserService userService;

//    @PostMapping("/jwt-login")
//    public ResponseEntity<?> jwtLogin(
//            @RequestBody JwtTokenLoginRequest request,
//            HttpServletResponse response
//    ) {
//        try {
//            JwtTokenDto jwtTokenResponse = userService.login(request);
//
//            Cookie refreshTokenCookie = new Cookie(
//                    "refreshToken",
//                    jwtTokenResponse.getRefreshToken()
//            );
//
//            refreshTokenCookie.setHttpOnly(true); // 자바스크립트에서 접근할 수 없도록 설정
////            refreshTokenCookie.setSecure(true); // HTTPS에서만 전송되도록 설정 (생산 환경에서 사용)
//            refreshTokenCookie.setPath("/"); // 쿠키의 유효 경로 설정
//            refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 쿠키의 유효 기간 설정 (예: 7일)
//
//            response.addCookie(refreshTokenCookie);
//
//            return ResponseEntity.ok().body(JwtTokenResponse
//                    .builder()
//                    .accessToken(jwtTokenResponse.getAccessToken())
//                    .build());
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패: " + e.getMessage());
//        }
//    }
@PostMapping("/jwt-login")
    public ResponseEntity<?> jwtLogin(
            @RequestBody JwtTokenLoginRequest request,
            HttpServletResponse response
    ) {
        try {
            JwtTokenDto jwtTokenResponse = userService.login(request);

            // Access Token 쿠키
            Cookie accessTokenCookie = new Cookie(
                    "accessToken",
                    jwtTokenResponse.getAccessToken()
            );
            accessTokenCookie.setHttpOnly(true);
            accessTokenCookie.setPath("/");
            accessTokenCookie.setMaxAge(30 * 60); // 30분

            // Refresh Token 쿠키
            Cookie refreshTokenCookie = new Cookie(
                    "refreshToken",
                    jwtTokenResponse.getRefreshToken()
            );
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge(3 * 24 * 60 * 60); // 3일

            response.addCookie(accessTokenCookie);
            response.addCookie(refreshTokenCookie);

            // 로그인 성공 응답
            return ResponseEntity.ok().body("로그인 성공");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 실패: " + e.getMessage());
        }
    }
}