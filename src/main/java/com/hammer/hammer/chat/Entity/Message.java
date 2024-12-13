package com.hammer.hammer.chat.Entity;
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
    private String chatRoomId; // 메시지가 속한 채팅방 ID
    private String senderId; // 보낸 사용자 ID
    private String content; // 메시지 내용

    private boolean readStatus;

    @CreatedDate
    private LocalDateTime timestamp; // 전송 시간
}