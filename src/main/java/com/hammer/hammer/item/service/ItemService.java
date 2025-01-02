package com.hammer.hammer.item.service;

import com.hammer.hammer.item.entity.Item;
import com.hammer.hammer.item.repository.ItemRepository;
import com.hammer.hammer.user.entity.User;

import com.hammer.hammer.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    public List<String> getAllStatuses() {
        return Arrays.stream(Item.ItemStatus.values())
                .map(Enum::name)  // enum을 문자열로 변환
                .collect(Collectors.toList());
    }

    public Page<Item> getAllItems(int page,int size,String sortBy,String direction,String status) {
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        if (status == null || status.isEmpty()) {
            return itemRepository.findAll(pageable);
        } else {
            for(Item.ItemStatus itemStatus : Item.ItemStatus.values()) {
                if(status.equals(itemStatus.name()))
                    return itemRepository.findByStatus(itemStatus, pageable);
            }
            return null;
        }
    }

    public Page<Item> searchItems(String keyword, int page,int size,String sortBy,String direction,String status) {
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        if (status == null || status.isEmpty()) {
            return itemRepository.findByTitleContainingIgnoreCase(keyword, pageable);
        } else {
            for(Item.ItemStatus itemStatus : Item.ItemStatus.values()) {
                if(status.equals(itemStatus.name()))
                    return itemRepository.findByTitleContainingIgnoreCaseAndStatus(keyword, pageable,itemStatus);
            }
            return null;
        }
    }

    public Item createItem(Item item, MultipartFile image, String itemPeriod) throws IOException {
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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Long currentUserId = Long.parseLong(authentication.getName());
        User currentUser = userRepository.findByUserId(currentUserId).orElse(null);

        item.setStartTime(LocalDateTime.now());
        item.setEndTime(calculateEndTime(item.getStartTime(), itemPeriod)); // 종료 시간 계산
        item.setStatus(Item.ItemStatus.ONGOING);
        item.setUser(currentUser);


        return itemRepository.save(item);
    }
    private LocalDateTime calculateEndTime(LocalDateTime startTime, String itemPeriod) {
        switch (itemPeriod) {
            case "3일":
                return startTime.plusDays(3);
            case "7일":
                return startTime.plusDays(7);
            case "1달":
                return startTime.plusMonths(1);
            case "2달":
                return startTime.plusMonths(2);
            case "3달":
                return startTime.plusMonths(3);
            default:
                throw new IllegalArgumentException("유효하지 않은 경매 기간입니다.");
        }
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
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("아이템을 찾을 수 없습니다: " + id));

        // 상태 확인
        updateItemStatus(item);

        return item;
    }
    //상태확인
    private void updateItemStatus(Item item) {
        if (item.getEndTime().isBefore(LocalDateTime.now())) {
            item.setStatus(Item.ItemStatus.COMPLETED);
            itemRepository.save(item);
        }
    }

    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }

    public List<Item> getItemsByIds(List<Long> ids) {
        return itemRepository.findAllByItemIdIn(ids);
    }

    public Page<Item> findByUserId(Long userId, Pageable pageable) {

        return itemRepository.findByUser_UserId(userId, pageable);
    }

}