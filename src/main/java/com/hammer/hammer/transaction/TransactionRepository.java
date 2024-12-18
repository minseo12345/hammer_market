package com.hammer.hammer.transaction;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.hammer.hammer.domain.Transaction;
import com.hammer.hammer.dto.TransactionStatusDto;

public interface TransactionRepository extends JpaRepository<Transaction, Integer>{
	
	//admin 경매 현황 조회
	@Query("SELECT new com.hammer.hammer.dto.TransactionStatusDto(" +
		       "CAST(t.seller.userId AS string), " +       
		       "t.seller.username, " +
		       "t.item.title, " +
		       "t.finalPrice, " +
		       "t.transactionDate, " +
		       "t.buyer.userId, " +        
		       "CAST(t.item.status AS string)) " +
		       "FROM Transaction t ")

	    List<TransactionStatusDto> findAllTransactionStatus();

	void deleteById(Long id);

	Optional<Transaction> findById(Long id);
}
