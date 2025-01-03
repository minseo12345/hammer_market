package com.hammer.hammer.item.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hammer.hammer.notification.entity.Notification;
import com.hammer.hammer.transaction.entity.Transaction;
//import com.hammer.hammer.transaction.entity.Transaction;
import com.hammer.hammer.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "items")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // AUTO_INCREMENT 대응
    @Column(name = "item_id")
    private Long itemId;


    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    @NotBlank(message = "제목은 필수 입력 항목입니다.")
    @Size(max = 100, message = "제목은 100자 이하여야 합니다.")
    @Column(nullable = false)
    private String title;

    @NotBlank(message = "상품 설명은 필수 입력 항목입니다.")
    @Size(max = 1000, message = "상품 설명은 1000자 이하여야 합니다.")
    @Column(nullable = false)
    private String description;

    @NotNull(message = "시작 가격은 필수 입력 항목입니다.")
    @Column(nullable = false)
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
        ONGOING,BIDDING_END,COMPLETED,CANCELLED,WAITING_FOR_MY_APPROVAL,WAITING_FOR_OTHER_APPROVAL
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
