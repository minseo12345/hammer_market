package com.hammer.hammer.global.jwt.auth;

import com.hammer.hammer.user.entity.Role;
import org.springframework.security.core.Authentication;

import java.util.Map;

public interface JwtProvider<T> {
    T convertAuthToken(String token);

    Authentication getAuthentication(T authToken);

    T createAccessToken(String userId, Role role, Map<String, Object> claims);

    T createRefreshToken(String userId, Role role, Map<String, Object> claims);
}
