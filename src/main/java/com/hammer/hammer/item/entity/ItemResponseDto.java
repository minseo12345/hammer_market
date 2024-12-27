package com.hammer.hammer.item.entity;
import com.hammer.hammer.user.entity.User;
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
    private BigDecimal startingBid;
    private BigDecimal buyNowPrice;
    private Item.ItemStatus status;
    private String fileUrl;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createdAt;
    // ENUM 선언
    public enum ItemStatus {
        ONGOING,BIDDING_END,COMPLETED,CANCELLED
    }
    private User user;
}
