package com.hammer.hammer.transaction.entity;

import com.hammer.hammer.item.entity.Item;
import com.hammer.hammer.point.entity.Point;
import com.hammer.hammer.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Table(name="transactions")
@Entity
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id", nullable = false)
    private Long transactionId;

    @ManyToOne
    @JoinColumn(name = "buyer_email", referencedColumnName = "email",nullable = false)
    private final User buyer;

    @ManyToOne
    @JoinColumn(name = "seller_email", referencedColumnName = "email",nullable = false)
    private final User seller;

    @OneToMany(mappedBy = "transaction")
    private List<Point> points;

    @OneToOne
    @JoinColumn(name = "item_id", nullable = false)
    private final Item item;  // 1:1 관계

    @Column(name = "final_price", precision = 38, scale = 2)
    private BigDecimal finalPrice;

    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;
}

