package com.hammer.hammer.item.controller;

import com.hammer.hammer.item.entity.CartSyncRequest;
import com.hammer.hammer.item.entity.Item;
import com.hammer.hammer.item.entity.ItemResponseDto;
import com.hammer.hammer.item.repository.ItemRepository;

import com.hammer.hammer.item.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ItemApiController {

    private final ItemRepository itemRepository;
    private final ItemService itemService;

    @PostMapping("/item/{itemId}")
    public ResponseEntity<ItemResponseDto> getItem(@PathVariable Long itemId) {
        Item findItem = itemRepository.findById(itemId).orElse(null);
        if (findItem == null) {
            return ResponseEntity.notFound().build();
        }
        ItemResponseDto response = findItem.toItemResponseDto();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/sync-cart")
    public ResponseEntity<List<Item>> syncCart(@RequestBody CartSyncRequest request) {
        List<Long> itemIds = request.getItemIds();
        log.info("get size:{}",itemIds.size());
        List<Item> updatedItems = itemService.getItemsByIds(itemIds); // Fetch latest data from DB
        log.info("get return:{}",updatedItems.size());
        return ResponseEntity.ok(updatedItems);
    }
}
