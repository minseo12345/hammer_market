package com.hammer.hammer.user.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {
    USER("ROLE_USER"),
    ADMIN("ROLE_ADMIN"),
    SELLER("ROLE_ADMIN"),
    BUYER("ROLE_ADMIN");
    private final String value;
}