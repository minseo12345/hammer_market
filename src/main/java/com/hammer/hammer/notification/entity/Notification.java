package com.hammer.hammer.notification.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private Long notificationId;

    @Column(nullable = false)
    private Long userId;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    @JsonIgnore
    private Item item;

    @Column(nullable = false, length = 255)
    private String message;

    @Column(nullable = false)
    private boolean isRead = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Transient
    private Long itemId;

    @PostLoad
    public void loadItemId() {
        this.itemId = item != null ? item.getItemId() : null;
    }

    public Notification(Long userId, Item item, String message) {
        this.userId = userId;
        this.item = item; // Item 객체 직접 설정
        this.message = message;
        this.isRead = false;
        this.createdAt = LocalDateTime.now(); // 현재 시간 자동 설정
    }

    // itemStatus를 반환하는 메서드 추가
    public String getItemStatus() {
        return item != null ? item.getStatus().name() : "UNKNOWN";
    }
}
