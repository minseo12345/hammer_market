package com.hammer.hammer.item.controller;

import com.hammer.hammer.item.entity.Item;
import com.hammer.hammer.item.entity.ItemResponseDto;
import com.hammer.hammer.item.repository.ItemRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ItemApiController {

    private final ItemRepository itemRepository;

    @PostMapping("/item/{itemId}")
    public ResponseEntity<ItemResponseDto> getItem(@PathVariable Long itemId) {
        Item findItem = itemRepository.findById(itemId).orElse(null);
        if (findItem == null) {
            return ResponseEntity.notFound().build();
        }
        ItemResponseDto response = findItem.toItemResponseDto();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/item-list")
    public ResponseEntity<List<ItemResponseDto>> getItemList() {
        List<Item> findItemList = itemRepository.findAll();
        List<ItemResponseDto> responses = new ArrayList<>();
        for (Item item : findItemList) {
            responses.add(item.toItemResponseDto());
        }
        return ResponseEntity.ok(responses);
    }
}
