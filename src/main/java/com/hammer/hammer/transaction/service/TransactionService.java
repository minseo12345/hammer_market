package com.hammer.hammer.transaction.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.hammer.hammer.transaction.entity.Transaction;
import com.hammer.hammer.transaction.repository.TransactionRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * 새로운 트랜잭션을 저장
     */
    public Transaction saveTransaction(Transaction transaction) {
        return transactionRepository.save(transaction);
    }

    /**
     * 모든 트랜잭션 조회
     */
    @Transactional(readOnly = true)
    public List<Transaction> findAllTransactions() {
        return transactionRepository.findAll();
    }

    /**
     * ID로 트랜잭션 조회
     */
    @Transactional(readOnly = true)
    public Optional<Transaction> findTransactionById(Long id) {
        return transactionRepository.findById(id);
    }

    /**
     * 트랜잭션 삭제
     */
    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }
}
