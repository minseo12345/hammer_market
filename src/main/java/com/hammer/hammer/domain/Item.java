package com.hammer.hammer.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Id;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "items")
@Getter
@Setter
public class Item {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT 대응
    @Column(name = "item_id")
    private Integer itemId;


    @Column(name = "category_id", nullable = false)
    private Integer categoryId;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "starting_bid", precision = 10, scale = 2)
    private BigDecimal startingBid;

    @Column(name = "buyNowPrice", precision = 10, scale = 2)
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
    	ONGOING,BIDDING_END,COMPLETED
    }
    
   
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; 
    
    @OneToOne(mappedBy = "item", cascade = CascadeType.ALL)
    private Transaction transaction;
    
    

  
}
