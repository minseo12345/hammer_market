package com.hammer.hammer.global.jwt.auth;

public interface AuthToken <T> {
    String AUTHORITIES_TOKEN_KEY = "role";
    boolean validate();
    T getDate();
}
