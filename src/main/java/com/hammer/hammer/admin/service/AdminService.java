package com.hammer.hammer.admin.service;

import org.springframework.stereotype.Service;

@Service
public class AdminService {
    public String getWelcomeMessage() {
        return "Welcome to the admin page";
    }
}
