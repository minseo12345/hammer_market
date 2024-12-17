package com.hammer.hammer.admin;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hammer.hammer.domain.User;
import com.hammer.hammer.dto.TransactionStatusDto;
import com.hammer.hammer.transaction.TransactionRepository;
import com.hammer.hammer.user.UserRepository;


@Service

public class AdminService {
	private final UserRepository userRepository; 
	private final TransactionRepository transactionRepository;
	//2개의 레파지토리를 주입받음
	public AdminService(UserRepository userRepository, TransactionRepository transactionRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
    }
	
	//DB에서 사용자 조회
	public List<User> getAllUsers(){
		return userRepository.findAll();
	}
	
	public List<TransactionStatusDto> getTransactionStatuses() {
        return transactionRepository.findAllTransactionStatus();
    } 

	
}
