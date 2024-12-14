package com.hammer.hammer.auction.controller;

import com.hammer.hammer.auction.entity.Item;
import com.hammer.hammer.auction.service.ItemService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ResponseEntity<Item> createItem(@ModelAttribute Item item, @RequestParam("image") MultipartFile image) throws IOException {
        Item createdItem = itemService.createItem(item, image);
        return ResponseEntity.ok(createdItem);
    }

    @GetMapping
    public ResponseEntity<List<Item>> getAllItems() {
        return ResponseEntity.ok(itemService.getAllItems());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.getItemById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Item> updateAuctionStatus(@PathVariable Long id, @RequestParam("status") String status) {
        Item.AuctionStatus newStatus = Item.AuctionStatus.valueOf(status.toUpperCase());
        Item updatedItem = itemService.updateAuctionStatus(id, newStatus);
        return ResponseEntity.ok(updatedItem);
    }
}