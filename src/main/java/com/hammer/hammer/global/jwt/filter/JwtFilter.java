package com.hammer.hammer.global.jwt.filter;

import com.hammer.hammer.global.jwt.auth.AuthTokenImpl;
import com.hammer.hammer.global.jwt.auth.JwtProviderImpl;
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
                AuthTokenImpl jwtToken =
                    tokenProvider.convertAuthToken(token.get());

                if (jwtToken.validate()) {
                    Authentication authentication =
                        tokenProvider.getAuthentication(jwtToken);

                    SecurityContextHolder
                            .getContext()
                            .setAuthentication(authentication);
                }
            }
        } catch (JwtException ex) {
            SecurityContextHolder.clearContext(); // 유효하지 않은 토큰 시 보안 컨텍스트 초기화
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않거나 만료된 토큰");
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
                if ("refreshToken".equals(cookie.getName())) {
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
