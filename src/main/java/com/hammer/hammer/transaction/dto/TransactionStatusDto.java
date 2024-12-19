package com.hammer.hammer.transaction.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TransactionStatusDto {

    private String sellerId;
    private String sellerUsername;
    private String itemTitle;
    private BigDecimal finalPrice;
    private LocalDateTime transactionDate;
    private String buyerId;
    private String status;
}
