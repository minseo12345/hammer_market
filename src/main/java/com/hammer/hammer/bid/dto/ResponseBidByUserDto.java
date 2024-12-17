package com.hammer.hammer.bid.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseBidByUserDto {
    private Long itemId;
    private String itemName;
    private String img;
    private String myPrice;
    private String currentPrice;
}
