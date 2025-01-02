package com.hammer.hammer.global.jwt.filter;

import com.hammer.hammer.global.jwt.auth.AuthTokenImpl;
import com.hammer.hammer.global.jwt.auth.JwtProviderImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

import static com.hammer.hammer.global.jwt.constants.UserConstants.AUTHORIZATION_TOKEN_KEY;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProviderImpl tokenProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        Optional<String> token = resolveToken(request);
        
        try {
            if (token.isPresent()) {
                AuthTokenImpl jwtToken = tokenProvider.convertAuthToken(token.get());

                if (jwtToken.validate()) {
                    Authentication authentication = tokenProvider.getAuthentication(jwtToken);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (ExpiredJwtException ex) {
            // Access Token이 만료된 경우
            Cookie[] cookies = request.getCookies();
            String refreshToken = null;
            
            // Refresh Token 추출
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("refreshToken".equals(cookie.getName())) {
                        refreshToken = cookie.getValue();
                        break;
                    }
                }
            }

            // Refresh Token이 있는 경우 토큰 갱신 시도
            if (refreshToken != null) {
                try {
                    // Refresh Token 검증 및 새로운 Access Token 발급
                    String newAccessToken = tokenProvider.refreshAccessToken(refreshToken);
                    
                    // 새로운 Access Token을 쿠키에 설정
                    Cookie newAccessTokenCookie = new Cookie("accessToken", newAccessToken);
                    newAccessTokenCookie.setHttpOnly(true);
                    newAccessTokenCookie.setPath("/");
                    newAccessTokenCookie.setMaxAge(30 * 60); // 30분
                    response.addCookie(newAccessTokenCookie);

                    // 새로운 토큰으로 인증 처리
                    AuthTokenImpl newJwtToken = tokenProvider.convertAuthToken(newAccessToken);
                    Authentication authentication = tokenProvider.getAuthentication(newJwtToken);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } catch (Exception e) {
                    SecurityContextHolder.clearContext();
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "토큰 갱신 실패");
                    return;
                }
            } else {
                SecurityContextHolder.clearContext();
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Refresh Token이 없습니다");
                return;
            }
        } catch (JwtException ex) {
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 토큰");
            return;
        }
        
        filterChain.doFilter(request, response);
    }

    private Optional<String> resolveToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String authToken = null;
        // 토큰을 꺼내오는 로직
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    authToken = cookie.getValue();
                    break;
                }
            }
        }


        if (StringUtils.hasText(authToken)) {
            return Optional.of(authToken);
        } else {
            return Optional.empty();
        }
    }
}
