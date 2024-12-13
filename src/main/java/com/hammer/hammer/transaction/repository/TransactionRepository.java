package com.hammer.hammer.transaction.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.hammer.hammer.transaction.entity.Transaction;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // 기본적인 CRUD 메서드는 JpaRepository에서 제공
    // 필요시 추가적인 쿼리 메서드를 선언할 수 있음
    // 예: Optional<Transaction> findBySomeField(String value);

}
