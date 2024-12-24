package com.hammer.hammer.bid.dto;

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
public class ResponseBidByItemDto {
    private Long userId;
    private String bidAmount;
    private String description;
    private String itemName;
    private String title;
    private LocalDateTime createAt;
    private String username;
    private String imageUrl;
}
