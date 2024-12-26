package com.hammer.hammer.admin;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.hammer.hammer.transaction.dto.TransactionStatusDto;
import com.hammer.hammer.transaction.repository.TransactionRepository;

@SpringBootTest
public class TransactionRepositoryTest {

    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    void testFindAllTransactionStatus() {
        // Act: JPQL 쿼리 실행
        List<TransactionStatusDto> transactions = transactionRepository.findAllTransactionStatus();

        // Assert: 결과 확인
        System.out.println(transactions); // 데이터를 로그로 출력
    }
}
