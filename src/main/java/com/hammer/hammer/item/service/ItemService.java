package com.hammer.hammer.item.service;

import com.hammer.hammer.item.entity.Item;
import com.hammer.hammer.item.repository.ItemRepository;

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


    public Item createItem(Item item, MultipartFile image) throws IOException {
        if (!image.isEmpty()) {
            // 파일 저장 경로 설정
            String uploadPath = "C:/uploads/";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs(); // 없으면 디렉토리 생성
            }

            // 파일 이름 처리 (한글 및 특수문자 제거)
            String originalFileName = image.getOriginalFilename();
            String sanitizedFileName = originalFileName.replaceAll("[^a-zA-Z0-9.]", "_");
            String fileName = UUID.randomUUID() + "_" + sanitizedFileName;

            // 파일 저장
            File uploadFile = new File(uploadPath + fileName);
            image.transferTo(uploadFile);

            // 이미지 URL 설정
            item.setFileUrl("/uploads/" + fileName);
        }

        item.setStartTime(LocalDateTime.now());
        item.setStatus(Item.ItemStatus.ONGOING);

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
                .orElseThrow(() -> new IllegalArgumentException("해당 경매 상품을 찾을 수 없습니다. ID: " + id));
    }

    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }
}