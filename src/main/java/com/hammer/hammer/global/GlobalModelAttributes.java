package com.hammer.hammer.global;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute("userId")
    public Long addUserIdToModel() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getName() != null) {
            try {
                return Long.parseLong(authentication.getName());
            } catch (NumberFormatException e) {
                System.err.println("Invalid userId format in authentication: " + authentication.getName());
            }
        }
        return null; // 비로그인 사용자 처리
    }
}

