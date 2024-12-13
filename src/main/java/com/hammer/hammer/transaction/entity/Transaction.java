package com.hammer.hammer.transaction.entity;

import jakarta.persistence.*;
import org.hibernate.cache.spi.support.AbstractReadWriteAccess; // ?
import org.hibernate.resource.transaction.spi.TransactionStatus;
//import org.springframework.security.core.userdetails.User;
import com.hammer.hammer.user.entity.User;
import com.hammer.hammer.item.entity.Item;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private User buyer;

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private User seller;

    private BigDecimal finalPrice;
    private Timestamp transactionDate;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    // Getters and Setters
}