package com.hammer.hammer.bid.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Bids")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Bid {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bidId;

    @Column(precision = 10, scale = 2)
    @NotNull(message = "입찰 금액은 필수입니다.")
    @Positive(message = "입찰 금액은 양수여야 합니다.")
    private BigDecimal bidAmount;

    @NotNull(message = "입찰 시간은 필수입니다.")
    @FutureOrPresent(message = "입찰 시간은 과거일 수 없습니다.")
    @Column(nullable = false)
    private LocalDateTime bidTime;

    @ManyToOne
    @JoinColumn(name = "item_id",nullable = false)
    @NotNull(message = "상품 정보는 필수입니다.")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    @NotNull(message = "사용자 정보는 필수입니다.")
    private User user;

}
