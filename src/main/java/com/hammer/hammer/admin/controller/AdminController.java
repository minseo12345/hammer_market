package com.hammer.hammer.admin.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class AdminController {

//	private final AdminService adminService;

    @GetMapping
    public String adminPage() {
        return "Welcome to Admin Page!";
    }
//
//	@GetMapping("/users")
//	public List<User> getAllUsers(){
//		return adminService.getAllUser();
//	}

}
