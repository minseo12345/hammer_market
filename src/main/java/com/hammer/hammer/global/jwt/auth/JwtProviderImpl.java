package com.hammer.hammer.global.jwt.auth;


import com.hammer.hammer.user.entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

import static com.hammer.hammer.global.jwt.auth.AuthToken.AUTHORITIES_TOKEN_KEY;
import static com.hammer.hammer.global.jwt.constants.UserConstants.ACCESS_TOKEN_TYPE_VALUE;
import static com.hammer.hammer.global.jwt.constants.UserConstants.REFRESH_TOKEN_TYPE_VALUE;

@Component
@RequiredArgsConstructor
public class JwtProviderImpl implements JwtProvider<AuthTokenImpl> {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.token.access-expires}")
    private long accessExpires;

    @Value("${jwt.token.refresh-expires}")
    private long refreshExpires;

    private Key key;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public AuthTokenImpl convertAuthToken(String token) {
        return new AuthTokenImpl(token, key);
    }

    @Override
    public Authentication getAuthentication(AuthTokenImpl authToken) {
        if (authToken.validate()) {
            Claims claims = authToken.getDate();

            /*
            if (!claims.get("type").equals(ACCESS_TOKEN_TYPE_VALUE)) {
                throw new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Invalid token type"
                );
            }
            */

            Set<SimpleGrantedAuthority> authorities = Collections.singleton(
                    new SimpleGrantedAuthority(claims.get(
                            AUTHORITIES_TOKEN_KEY,
                            String.class
                    ))
            );

            User principal = new User(claims.getSubject(), "", authorities);

            return new UsernamePasswordAuthenticationToken(
                    principal,
                    authToken,
                    authorities
            );
        } else {
            throw new JwtException("token Error");
        }
    }

    @Override
    public AuthTokenImpl createAccessToken(
            String userId,
            Role role,
            Map<String, Object> claims
    ) {

        return new AuthTokenImpl(
                userId,
                role,
                key,
                claims, //c,
                new Date(System.currentTimeMillis() + accessExpires)
        );
    }

    @Override
    public AuthTokenImpl createRefreshToken(
            String userId,
            Role role,
            Map<String, Object> claims
    ) {
        claims.put("type", REFRESH_TOKEN_TYPE_VALUE);
        return new AuthTokenImpl(
                userId,
                role,
                key,
                /*(Claims)*/ claims,
                new Date(System.currentTimeMillis() + refreshExpires)
        );
    }

    public String refreshAccessToken(String refreshToken) {
        try {
            // Refresh Token 검증
            AuthTokenImpl refreshAuthToken = convertAuthToken(refreshToken);
            if (!refreshAuthToken.validate()) {
                throw new JwtException("Invalid Refresh Token");
            }

            // Refresh Token에서 사용자 정보 추출
            Claims claims = refreshAuthToken.getDate();
            String userId = claims.getSubject();
            
            // 새로운 Access Token 발급
            Map<String, Object> newClaims = new HashMap<>();
            newClaims.put("userEmail", claims.get("userEmail"));
            newClaims.put("username", claims.get("username"));

            // Role 직접 가져오기
            Role role = Role.builder()
                    .roleName("ROLE_USER")
                    .build();  // 기본값으로 USER 설정
            String roleStr = claims.get("role", String.class);
            if ("ADMIN".equals(roleStr)) {
                role = Role.builder()
                        .roleName("ROLE_ADMIN")
                        .build();
            }

            return createAccessToken(userId, role, newClaims).getToken();
        } catch (Exception e) {
            throw new JwtException("Failed to refresh token", e);
        }
    }

}
