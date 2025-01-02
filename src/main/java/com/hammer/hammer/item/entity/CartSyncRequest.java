package com.hammer.hammer.item.entity;

import lombok.Data;

import java.util.List;

@Data
public class CartSyncRequest {
    private Long userId;
    private List<Long> itemIds;
}
