package com.hammer.hammer.bid.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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

    @NotNull(message = "상품 ID는 필수입니다.")
    private Long itemId;

    @NotNull(message = "사용자 ID는 필수입니다.")
    private String userId;

    @Column(precision = 10, scale = 2)
    @NotNull(message = "입찰 금액은 필수입니다.")
    @Positive(message = "입찰 금액은 양수여야 합니다.")
    private BigDecimal bidAmount;

    @NotNull(message = "입찰 날짜는 필수입니다.")
    @FutureOrPresent(message = "입찰 날짜는 과거일 수 없습니다.")
    private LocalDateTime bidDate;

}
