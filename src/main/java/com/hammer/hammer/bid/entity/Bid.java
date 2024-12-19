package com.hammer.hammer.bid.entity;

import com.hammer.hammer.item.entity.Item;
import com.hammer.hammer.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bids")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bidId;

    @Column(precision = 10, scale = 2, nullable = false)
    @Positive(message = "입찰 금액은 양수여야 합니다.")
    private BigDecimal bidAmount;

    @FutureOrPresent(message = "입찰 시간은 과거일 수 없습니다.")
    @Column(nullable = false)
    private LocalDateTime bidTime;

    @ManyToOne
    @JoinColumn(name = "item_id",nullable = false)
    private Item item;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

}
