package com.hammer.hammer.admin.service;

import com.hammer.hammer.transaction.repository.TransactionRepository;
import com.hammer.hammer.transaction.dto.TransactionStatusDto;
import com.hammer.hammer.user.entity.User;
import com.hammer.hammer.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    //2개의 레파지토리를 주입받음

    //DB에서 사용자 조회
    public List<User> getAllUsers(){
        return userRepository.findAll();
    }

    public List<TransactionStatusDto> getTransactionStatuses() {
        return transactionRepository.findAllTransactionStatus();
    }


}