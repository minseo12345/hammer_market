//package com.hammer.hammer.notification.entity;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name = "notifications")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class Notification {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long notificationId;
//
//    @Column(nullable = false)
//    private Long userId;
//
//    @Column(nullable = false)
//    private Long itemId;
//
//    @Column(nullable = false, length = 255)
//    private String message;
//
//    @Column(nullable = false)
//    private LocalDateTime createdAt = LocalDateTime.now();
//
//    @Column(nullable = false)
//    private boolean isRead = false;
//
//    @Column(nullable = false, length = 50)
//    private String notificationType; // e.g., "낙찰", "거래완료"
//}