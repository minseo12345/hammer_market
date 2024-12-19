package com.hammer.hammer.chat.entity;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatRoomDto {
    private Long buyerId;
    private Long sellerId;
}