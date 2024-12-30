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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @OneToOne
    @JoinColumn(name = "item_id", nullable = false)
    private final Item item;  // 1:1 관계

    @Column(name = "final_price", precision = 38, scale = 2)
    private BigDecimal finalPrice;

    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;

    @ElementCollection
    private Set<Long> modifiedBy = new HashSet<>(); // 거래 상태를 변경한 사용자 ID 저장

    public void addModifiedBy(Long userId) {
        this.modifiedBy.add(userId);
    }

    // 거래 상태 반환 메서드
    public String getTransactionStatus(Long userId) {
        if (modifiedBy.contains(userId)) {
            return "WAITING_FOR_OTHER_APPROVAL"; // 내가 수락함, 상대방 대기 중
        } else {
            return "WAITING_FOR_MY_APPROVAL"; // 상대방이 수락함, 내가 대기 중
        }
    }
}

