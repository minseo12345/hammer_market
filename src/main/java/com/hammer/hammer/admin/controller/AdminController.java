package com.hammer.hammer.admin.controller;

import com.hammer.hammer.admin.service.AdminService;
import com.hammer.hammer.category.entity.Category;
import com.hammer.hammer.transaction.dto.TransactionStatusDto;
import com.hammer.hammer.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    // 관리자 대시보드
    @GetMapping
    public String adminPage(Model model) {
        model.addAttribute("welcomeMessage", "Welcome to the admin page!");
        return "/admin/dashboard";
    }
    
    // 사용자 조회 화면
    @GetMapping("/users")
    public String getAllUsers(Model model) {
        List<User> userList = adminService.getAllUsers();
        model.addAttribute("users", userList);
        return "/admin/users";
    }

    // 경매 현황 조회 화면
    @GetMapping("/transactions")
    public String getAllTransactions(Model model) {
        List<TransactionStatusDto> transactions = adminService.getTransactionStatuses();
        System.out.println("transactions: " + transactions);
        model.addAttribute("transactions", transactions);
        return "admin/transactions";
    }

    // 카테고리 관리 화면
    @GetMapping("/categories")
    public String getCategory(Model model) {
    	  List<Category> categories = adminService.findAll();
    	    model.addAttribute("categories", categories);
    	    return "admin/categories";
    }

    
    @PostMapping("/categories")
    public String saveCategory(@ModelAttribute Category category) {
        adminService.save(category); // 서비스 계층에서 카테고리 저장
        return "redirect:/admin/categories"; // 저장 후 카테고리 목록으로 리다이렉트
    }
    // 카테고리 추가 폼
    @GetMapping("/categories/new")
    public String createCategoryForm(Model model) {
        model.addAttribute("category", new Category());
        return "admin/categories.new";
    }

    
    // 카테고리 수정 폼
    @GetMapping("/categories/edit/{id}")
    public String editCategoryForm(@PathVariable("id") Long id, Model model) {
        Optional<Category> optionalCategory = adminService.findById(id);
        if (optionalCategory.isPresent()) {
            model.addAttribute("category", optionalCategory.get());
            return "admin/categories.form";
        }
        return "redirect:/admin/categories";
    }

  
}
