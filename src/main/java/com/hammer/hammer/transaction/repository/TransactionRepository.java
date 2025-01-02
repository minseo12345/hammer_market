package com.hammer.hammer.transaction.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hammer.hammer.transaction.entity.Transaction;
import com.hammer.hammer.transaction.dto.TransactionStatusDto;

public interface TransactionRepository extends JpaRepository<Transaction, Long>{

	//admin 경매 현황 조회
	@Query("SELECT new com.hammer.hammer.transaction.dto.TransactionStatusDto(" +
	           "t.seller.userId, " +
	           "t.seller.email, " +
	           "t.item.title, " +
	           "t.finalPrice, " +
	           "t.transactionDate, " +
	           "t.buyer.email, " +
	           "t.item.status) " +
	           "FROM Transaction t " +
	           "WHERE t.seller.role.roleName = 'SELLER' AND t.buyer.role.roleName = 'BUYER'")
	    List<TransactionStatusDto> findAllTransactionStatus();

	void deleteById(Long id);

	Optional<Transaction> findById(Long id);
}
