package com.hammer.hammer.item.service;

import com.hammer.hammer.item.entity.Item;
import com.hammer.hammer.item.repository.ItemRepository;
import com.hammer.hammer.user.entity.User;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;


    public Item createItem(Item item, MultipartFile image, User user) throws IOException {
        if (!image.isEmpty()) {
            String uploadPath = "C:/uploads/";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            String originalFileName = image.getOriginalFilename();
            String sanitizedFileName = originalFileName.replaceAll("[^a-zA-Z0-9.]", "_");
            String fileName = UUID.randomUUID() + "_" + sanitizedFileName;
            File uploadFile = new File(uploadPath + fileName);
            image.transferTo(uploadFile);

            item.setFileUrl("/uploads/" + fileName);
        }

        item.setStartTime(LocalDateTime.now());
        item.setStatus(Item.ItemStatus.ONGOING);


        item.setUser(user);

        return itemRepository.save(item);
    }


    public Item updateAuctionStatus(Long itemId, Item.ItemStatus newStatus) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item not found"));
        item.setStatus(newStatus);
        return itemRepository.save(item);
    }

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Item getItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("아이템을 찾을 수 없습니다: " + id));
    }

    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }
}