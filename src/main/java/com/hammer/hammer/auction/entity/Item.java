package com.hammer.hammer.auction.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long itemId;

    @Column(name = "title")
    private String title;

    @Column(name = "description") 
    private String description;

    @Column(name = "starting_bid")
    private BigDecimal startingBid;

    @Column(name = "buyNowPrice")
    private BigDecimal buyNowPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AuctionStatus status;

    @Column(name = "fileUrl")
    private String fileUrl;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public enum AuctionStatus {
        ONGOING, BIDDING_END, COMPLETED
    }
}