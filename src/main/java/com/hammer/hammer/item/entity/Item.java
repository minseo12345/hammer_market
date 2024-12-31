package com.hammer.hammer.item.entity;

import com.hammer.hammer.transaction.entity.Transaction;
//import com.hammer.hammer.transaction.entity.Transaction;
import com.hammer.hammer.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "items")
@Getter
@Setter
public class Item{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT 대응
    @Column(name = "item_id")
    private Long itemId;


    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "starting_bid", precision = 12)
    private BigDecimal startingBid;

    @Column(name = "buyNowPrice", precision = 12)
    private BigDecimal buyNowPrice;

    @Enumerated(EnumType.STRING) // ENUM을 문자열로 저장
    @Column(name = "status", nullable = false)
    private ItemStatus status;

    @Column(name = "fileUrl", length = 100, nullable= false)
    private String fileUrl;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // ENUM 선언
    public enum ItemStatus {
        ONGOING,BIDDING_END,COMPLETED,CANCELLED
    }

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(mappedBy = "item", cascade = CascadeType.ALL)
    private Transaction transaction;

    public ItemResponseDto toItemResponseDto() {
        return ItemResponseDto.builder()
                .itemId(itemId)
                .categoryId(categoryId)
                .title(title)
                .description(description)
                .startingBid(startingBid)
                .buyNowPrice(buyNowPrice)
                .status(status)
                .fileUrl(fileUrl)
                .build();
    }
}
