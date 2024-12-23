package com.hammer.hammer.admin.service;

import com.hammer.hammer.category.entity.Category;
import com.hammer.hammer.category.repository.CategoryRepository;
import com.hammer.hammer.transaction.dto.TransactionStatusDto;
import com.hammer.hammer.transaction.repository.TransactionRepository;
import com.hammer.hammer.user.entity.User;
import com.hammer.hammer.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    //private final CategoryRepository categoryRepository;
    
 

    //DB에서 사용자 조회
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }
    //경매현황조회
    public List<TransactionStatusDto> getTransactionStatuses() {
        return transactionRepository.findAllTransactionStatus();
    }
    /*
    //카테고리 crud
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    public Optional<Category> findById(Long categoryId) {
        return categoryRepository.findById(categoryId);
    }

    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    public void deleteById(Long categoryId) {
        categoryRepository.deleteById(categoryId);
    }*/
}