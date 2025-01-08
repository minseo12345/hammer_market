package com.hammer.hammer.item.entity;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ItemResponseDto {
    private Long itemId;
    private Long categoryId;
    private String title;
    private String description;
    private BigDecimal currentPrice;
    private BigDecimal startingBid;
    private BigDecimal buyNowPrice;
    private Item.ItemStatus status;
    private String fileUrl;
    private LocalDateTime startTime ;
    private LocalDateTime endTime;

    // ENUM 선언
    public enum ItemStatus {
        ONGOING,BIDDING_END,COMPLETED,CANCELLED, PARTIALLY_APPROVE
    }
}
