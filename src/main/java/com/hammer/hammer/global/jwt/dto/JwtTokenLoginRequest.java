package com.hammer.hammer.global.jwt.dto;

import lombok.*;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class JwtTokenLoginRequest {
    private final String email;
    private final String password;
}
