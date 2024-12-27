package com.hammer.hammer.point.entity;

import com.hammer.hammer.transaction.entity.Transaction;
import com.hammer.hammer.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "points")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Point {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pointId;

    @Column(precision = 10, scale = 2, nullable = false)
    @Positive(message = "입찰 금액은 양수여야 합니다.")
    private BigDecimal pointAmount;

    @Column
    private LocalDateTime createDate;

    private String description;

    @Enumerated(EnumType.STRING)
    private PointStatus pointType;

    @Column(precision = 10, scale = 2, nullable = false)
    private BigDecimal balanceAmount;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
