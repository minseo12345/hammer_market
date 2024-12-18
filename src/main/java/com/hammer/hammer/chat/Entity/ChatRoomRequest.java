package com.hammer.hammer.chat.Entity;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatRoomRequest {
    private String buyerId;
    private String sellerId;
}