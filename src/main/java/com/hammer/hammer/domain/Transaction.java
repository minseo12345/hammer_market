package com.hammer.hammer.domain;



import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Id;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "transaction")
@Getter
@Setter
public class Transaction {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY) 
	@Column(name="transaction_id",nullable = false)
	private Integer transactionId; //pk

    @Column(name = "final_price", precision = 38, scale = 2, nullable = true)
    private BigDecimal finalPrice;

    @Column(name = "transaction_date", nullable = true)
    private LocalDateTime transactionDate;
    
    @ManyToOne
    @JoinColumn(name = "buyer_id", referencedColumnName = "user_id")
    private User buyer; // 구매자 (BUYER 역할)

    @ManyToOne
    @JoinColumn(name = "seller_id", referencedColumnName = "user_id")
    private User seller; 
    
    //item_id 1:1 관계
    @OneToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;
}
