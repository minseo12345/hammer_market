package com.hammer.hammer.chat.Entity;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "chatRoom")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoom {

    @Id
    private String id;
    private String title;
    private String sellerId; // 첫 번째 사용자 ID
    private String buyerId; // 두 번째 사용자 ID

    private int sellerUnreadCount;  // 판매자가 읽지 않은 메시지 수
    private int buyerUnreadCount;    // 구매자가 읽지 않은 메시지 수

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
}