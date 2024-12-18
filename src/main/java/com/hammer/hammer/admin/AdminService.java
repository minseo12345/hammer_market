package com.hammer.hammer.admin;

import java.util.List;

import org.springframework.stereotype.Service;

import com.hammer.hammer.domain.User;
import com.hammer.hammer.dto.TransactionStatusDto;
import com.hammer.hammer.transaction.TransactionRepository;
import com.hammer.hammer.user.UserRepository;

import lombok.RequiredArgsConstructor;


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
