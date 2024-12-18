package com.hammer.hammer.transaction.entity;

import jakarta.persistence.*;
import com.hammer.hammer.user.entity.User;
import com.hammer.hammer.auction.entity.Item;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transactionId;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private final Item item;  // final 필드

    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private final User buyer;  // final 필드

    @ManyToOne
    @JoinColumn(name = "seller_id")
    private final User seller;  // final 필드

    private BigDecimal finalPrice;
    private Timestamp transactionDate;

    public void setItem(Item item) {
    }

    public void setBuyer(User user) {
    }

    public void setSeller(Object user) {
    }
}


