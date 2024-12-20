package com.hammer.hammer.global.jwt.auth;


import com.hammer.hammer.user.entity.Role;
import io.jsonwebtoken.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import io.jsonwebtoken.impl.DefaultClaims;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Getter
@AllArgsConstructor
@Slf4j
@ToString
public class AuthTokenImpl implements AuthToken<Claims> {

    private final String token;
    private final Key key;

    public AuthTokenImpl (
            String userId,
            Role role,
            Key key,
            Map<String, Object> claims, //Claims claims,
            Date expiredDate
    ) {
        this.key = key;
        this.token = createJwtToken(userId, role, claims, expiredDate).get();
    }

    private Optional<String> createJwtToken(
            String userId,
            Role role,
            Map<String, Object> claimsMap,
            Date expiredDate
    ) {
        claimsMap.put(AUTHORITIES_TOKEN_KEY, role);
        DefaultClaims claims = new DefaultClaims(claimsMap);
        //claims.put(AUTHORITIES_TOKEN_KEY, role);

        return Optional.ofNullable(Jwts.builder()
                .setSubject(userId)
                .addClaims(claims)
                .signWith(key, SignatureAlgorithm.HS256)
                .setExpiration(expiredDate)
                .compact()
        );
    }

    @Override
    public boolean validate() {
        return getDate() != null;
    }

    @Override
    public Claims getDate() {
        try {
            return Jwts
                    //.parserBuilder()
                    .parser()
                    .setSigningKey(key.getEncoded())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SecurityException e) {
            log.warn("Invalid JWT signature");
        } catch (MalformedJwtException e) {
            log.warn("Invalid JWT token");
        } catch (ExpiredJwtException e) {
            log.warn("Expired JWT token");
        } catch (UnsupportedJwtException e) {
            log.warn("Unsupported JWT Token");
        } catch (IllegalArgumentException e) {
            log.warn("JWT token compact of handler are invalid");
        }
        return null;
    }

}