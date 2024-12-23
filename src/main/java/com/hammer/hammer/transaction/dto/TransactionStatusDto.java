package com.hammer.hammer.transaction.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.hammer.hammer.item.entity.Item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TransactionStatusDto {

    private Long sellerId;
    private String sellerEmail;
    private String itemTitle;
    private BigDecimal finalPrice;
    private LocalDateTime transactionDate;
    private String buyerEmail;
    private Item.ItemStatus status;
}
