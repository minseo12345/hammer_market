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


    // 특정 카테고리 조회
    @GetMapping("/categories/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        Optional<Category> optionalCategory = adminService.findById(id);
        if (optionalCategory.isPresent()) {
            return ResponseEntity.ok(optionalCategory.get());
        }
        return ResponseEntity.notFound().build(); // 404 Not Found
    }

    // 카테고리 생성
    @PostMapping("/categories")
    public ResponseEntity<Category> createCategory(@RequestBody Category category) {
        Category savedCategory = adminService.save(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCategory); // 201 Created
    }

    // 카테고리 수정
    @PutMapping("/categories/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody Category categoryDetails) {
        Optional<Category> optionalCategory = adminService.findById(id);
        if (optionalCategory.isPresent()) {
            Category category = optionalCategory.get();
            category.setName(categoryDetails.getName());
            category.setDescription(categoryDetails.getDescription());
            Category updatedCategory = adminService.save(category);
            return ResponseEntity.ok(updatedCategory); // 200 OK
        }
        return ResponseEntity.notFound().build(); // 404 Not Found
    }

    // 카테고리 삭제
    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        Optional<Category> optionalCategory = adminService.findById(id);
        if (optionalCategory.isPresent()) {
            adminService.deleteById(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        }
        return ResponseEntity.notFound().build(); // 404 Not Found
    }
}
