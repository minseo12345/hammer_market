package com.hammer.hammer.bid.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestBidDto {
    private Long itemId;
    private Long userId;

    @Column(precision = 10, scale = 2)
    private BigDecimal bidAmount;

    private LocalDateTime bidDate;

}
