package com.hammer.hammer.admin.service;

import com.hammer.hammer.category.entity.Category;
import com.hammer.hammer.category.repository.CategoryRepository;
import com.hammer.hammer.transaction.dto.TransactionStatusDto;
import com.hammer.hammer.transaction.repository.TransactionRepository;
import com.hammer.hammer.user.entity.User;
import com.hammer.hammer.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    
 

    //DB에서 사용자 조회
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
    //경매현황조회
    public List<TransactionStatusDto> getTransactionStatuses() {
        return transactionRepository.findAllTransactionStatus();
    }
    
    //카테고리 crud
    public List<Category> findAll() {
    	  List<Category> categories = categoryRepository.findAllCategories();
          System.out.println("Categories fetched from DB: " + categories);
          return categories;
    }

    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }
    @Transactional
    public Category save(Category category) {
        return categoryRepository.save(category);
    }
    @Transactional
    public void deleteById(Long id) {
        categoryRepository.deleteById(id);
    }
    
    public void updateCategory(Long id, Category category) {
        Optional<Category> optionalCategory = categoryRepository.findById(id);
        if (optionalCategory.isPresent()) {
            Category existingCategory = optionalCategory.get();
            existingCategory.setName(category.getName());
            existingCategory.setDescription(category.getDescription());
            categoryRepository.save(existingCategory);
        } else {
            throw new RuntimeException("Category not found for id " + id);
        }
    }
}