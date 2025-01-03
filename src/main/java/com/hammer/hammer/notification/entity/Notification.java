package com.hammer.hammer.notification.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.hammer.hammer.item.entity.Item;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("notificationId")
    private Long notificationId;

    @Column(nullable = false)
    @JsonProperty("userId")
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    @JsonIgnore // Item 전체 데이터는 포함하지 않음
    private Item item;

    @Column(nullable = false, length = 255)
    @JsonProperty("message")
    private String message;

    @Column(nullable = false)
    @JsonProperty("isRead")
    private boolean isRead = false;

    @Column(nullable = false, updatable = false)
    @JsonProperty("createdAt")
    private LocalDateTime createdAt;

    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL) // JSON 응답에 NULL 값은 포함되지 않음
    @JsonProperty("itemId")
    private Long itemId;

    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL) // JSON 응답에 NULL 값은 포함되지 않음
    @JsonProperty("itemStatus")
    private String itemStatus;

    @PostLoad
    public void loadTransientFields() {
        this.itemId = item != null ? item.getItemId() : null;
        this.itemStatus = item != null && item.getStatus() != null ? item.getStatus().name() : "UNKNOWN";
    }

    public String getDynamicStatus(Long currentUserId) {
        if (item != null && item.getStatus() != null) {
            switch (item.getStatus()) {
                case WAITING_FOR_OTHER_APPROVAL:
                    return userId.equals(currentUserId) ? "내 수락 대기 중" : "상대방 수락 대기 중";
                case WAITING_FOR_MY_APPROVAL:
                    return userId.equals(currentUserId) ? "상대방 수락 대기 중" : "내 수락 대기 중";
                case COMPLETED:
                    return "거래 완료";
                case CANCELLED:
                    return "취소된 거래";
                default:
                    return item.getStatus().name(); // 상태 그대로 반환
            }
        }
        return "UNKNOWN";
    }

    // 생성자: Notification 생성 시 기본 데이터 설정
    public Notification(Long userId, Item item, String message) {
        this.userId = userId;
        this.item = item;
        this.message = message;
        this.isRead = false;
        this.createdAt = LocalDateTime.now();
    }
}

