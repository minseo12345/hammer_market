package com.hammer.hammer.transaction.entity;

import com.hammer.hammer.item.entity.Item;
import com.hammer.hammer.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id", nullable = false)  // `nullable` 설정을 통해 필드 강제 설정
    private Long transactionId; // pk

    @ManyToOne
    @JoinColumn(name = "buyer_id", referencedColumnName = "user_id")
    private final User buyer;

    @ManyToOne
    @JoinColumn(name = "seller_id", referencedColumnName = "user_id")
    private final User seller;

    @OneToOne
    @JoinColumn(name = "item_id", nullable = false)
    private final Item item; //1:1 관계로 수정

    @Column(name = "final_price", precision = 38, scale = 2, nullable = true)  // 금액 설정
    private BigDecimal finalPrice;

    @Column(name = "transaction_date", nullable = true)  // 거래 날짜
    private LocalDateTime transactionDate;


    public void setBuyer(User user) {

    }

    public void setSeller(User user) {

    }

    public void setTransactionDate(LocalDateTime localDateTime) {
    }

    public void setItem(Item item) {
    }
}

