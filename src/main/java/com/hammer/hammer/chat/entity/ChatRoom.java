package com.hammer.hammer.chat.entity;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "chatRoom")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoom {

    @Id
    private String id;

    @NotNull(message = "Title cannot be null")
    private String sellerTitle;

    @NotNull(message = "Title cannot be null")
    private String buyerTitle;

    @NotNull(message = "SellerId cannot be null")
    private Long sellerId;

    @NotNull(message = "BuyerId cannot be null")
    private Long buyerId;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime updatedAt;
}