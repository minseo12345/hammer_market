package com.hammer.hammer.admin.controller;


import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation. *;

import com.hammer.hammer.admin.service.AdminService;
import com.hammer.hammer.category.entity.Category;


import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin")
public class AdminApiController {

    private final AdminService adminService;

    //카테고리 저장
    @PostMapping("/categories")
    public ResponseEntity<Void> saveCategory(@RequestBody Category category) {
        adminService.save(category);
        return ResponseEntity.status(HttpStatus.CREATED).build(); // 201 Created
    }
    
    @GetMapping("/categories/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable("id") Long id) {
        Optional<Category> optionalCategory = adminService.findById(id);
        if (optionalCategory.isPresent()) {
            return ResponseEntity.ok(optionalCategory.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/categories/edit/{id}")
    public String updateCategory(@PathVariable("id") Long id, @ModelAttribute("category") Category category) {
    	 System.out.println("Category ID: " + id);
    	adminService.updateCategory(id, category);
        return "redirect:/admin/categories";
    }
    // 카테고리 삭제
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("id") Long id) {
        Optional<Category> optionalCategory = adminService.findById(id);
        if (optionalCategory.isPresent()) {
            adminService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
