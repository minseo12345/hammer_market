package com.hammer.hammer.transaction.controller;

import com.hammer.hammer.transaction.entity.Transaction;
import com.hammer.hammer.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    // 모든 트랜잭션 조회 (관리자)
    @GetMapping
    public String getAllTransactions(Model model) {
        List<Transaction> transactions = transactionService.findAllTransactions();
        model.addAttribute("transactions", transactions);
        return "transaction/list";
    }

    // ID로 트랜잭션 조회
    @GetMapping("/transactions/{transactionId}")
    public String getTransactionDetail(@PathVariable Long transactionId, Model model) {
        // Transaction 데이터를 가져옴
        Transaction transaction = transactionService.findTransactionById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("해당 거래를 찾을 수 없습니다. ID: " + transactionId));

        // 모델에 데이터 추가
        model.addAttribute("transaction", transaction);

        // detail.html 렌더링
        return "transaction/detail";
    }

    // 즉시구매 버튼을 눌러서 경매 종료 처리
    @PostMapping("/immediate-purchase/{itemId}")
    public String createTransactionForImmediatePurchase(@PathVariable Long itemId) {
        try {
            transactionService.createTransactionForImmediatePurchase(itemId);
            return "redirect:/transactions";  // 즉시구매 후 트랜잭션 목록 페이지로 리디렉션
        } catch (IllegalArgumentException e) {
            return "error";  // 오류가 발생하면 에러 페이지로 이동
        }
    }

    /*// 경매 종료 후 트랜잭션 생성
    @PostMapping("/auction-end/{itemId}")
    public String createTransactionForAuctionEnd(@PathVariable Long itemId) {
        try {
            transactionService.createTransactionForAuctionEnd(itemId);
            return "redirect:/transactions";
        } catch (IllegalArgumentException e) {
            return "error";
        }
    }*/
}


