package com.hammer.hammer.global.jwt.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@ToString
@Getter
public class JwtTokenResponse {
    private String accessToken;
}
