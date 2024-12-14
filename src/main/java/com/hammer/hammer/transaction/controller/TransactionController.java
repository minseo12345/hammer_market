package com.hammer.hammer.transaction.controller;

import com.hammer.hammer.transaction.entity.Transaction;
import com.hammer.hammer.transaction.service.TransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    //경매 종료 후 트랜젝션 생성
    @PostMapping("/create/{itemId}")
    public ResponseEntity<String> createTransaction(@PathVariable Long itemId) {
        try {
            transactionService.createTransactionForAuctionEnd(itemId);
            return ResponseEntity.status(HttpStatus.CREATED).body("거래가 성공적으로 생성되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    //모든 트랜젝션 조회
    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = transactionService.findAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    //ID로 트랜젝션 조회
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable Long id) {
        return transactionService.findTransactionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //트랜젝션 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {
        transactionService.deleteTransaction(id);
        return ResponseEntity.noContent().build();
    }

}