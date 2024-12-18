package com.hammer.hammer.chat.Entity;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "message")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    @Id
    private String id;

    @NotNull(message = "chatRoomId cannot be null")
    private String chatRoomId;

    @NotNull(message = "senderId cannot be null")
    private String senderId;

    @NotNull(message = "content cannot be null")
    @Size(min = 1, max = 100, message = "content must be between 1 and 100 characters")
    private String content;

    @NotNull
    private boolean readStatus = false;

    @CreatedDate
    private LocalDateTime createdAt; // 전송 시간
}