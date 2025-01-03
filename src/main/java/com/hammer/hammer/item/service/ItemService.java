package com.hammer.hammer.item.service;

import com.hammer.hammer.bid.service.BidService;
import com.hammer.hammer.item.entity.Item;
import com.hammer.hammer.item.entity.ItemResponseDto;
import com.hammer.hammer.item.repository.ItemRepository;
import com.hammer.hammer.user.entity.User;

import com.hammer.hammer.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
    private final BidService bidService;

    public List<String> getAllStatuses() {
        return Arrays.stream(Item.ItemStatus.values())
                .map(Enum::name)  // enum을 문자열로 변환
                .collect(Collectors.toList());
    }

    public Page<ItemResponseDto> getAllItems(int page,String sortBy,String direction,String status,Long categoryId) {
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, 12, sort);
        Page<Item> itemPage;
        if (categoryId == null) {
            for(Item.ItemStatus itemStatus : Item.ItemStatus.values()) {
                if(status.equals(itemStatus.name())) {
                    itemPage = itemRepository.findByStatus(itemStatus, pageable);
                    return convertToItemResponseDtoPage(itemPage);
                }
            }
        } else {
            for(Item.ItemStatus itemStatus : Item.ItemStatus.values()) {
                if(status.equals(itemStatus.name())){
                    itemPage =itemRepository.findByStatusAndCategoryId(itemStatus, categoryId,pageable);
                    return convertToItemResponseDtoPage(itemPage);
                }
            }
        }
        return null;
    }

    public Page<ItemResponseDto> searchItems(String keyword, int page,String sortBy,String direction,String status,Long categoryId) {
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, 12, sort);
        Page<Item> itemPage;
        if (categoryId == null) {
            for(Item.ItemStatus itemStatus : Item.ItemStatus.values()) {
                if(status.equals(itemStatus.name())){
                    itemPage=itemRepository.findByTitleContainingIgnoreCaseAndStatus(keyword, pageable,itemStatus);
                    return convertToItemResponseDtoPage(itemPage);
                }
            }
        } else {
            for(Item.ItemStatus itemStatus : Item.ItemStatus.values()) {
                if(status.equals(itemStatus.name())){
                    itemPage=itemRepository.findByTitleContainingIgnoreCaseAndStatusAndCategoryId(keyword, pageable,itemStatus,categoryId);
                    return convertToItemResponseDtoPage(itemPage);
                }
            }
        }
        return null;
    }

    private Page<ItemResponseDto> convertToItemResponseDtoPage(Page<Item> itemPage) {
        List<ItemResponseDto> itemDtos = itemPage.getContent().stream()
                .map(item -> ItemResponseDto.builder()
                        .itemId(item.getItemId())
                        .categoryId(item.getCategoryId())
                        .title(item.getTitle())
                        .description(item.getDescription())
                        .currentPrice(bidService.getHighestBidAmount(item.getItemId()))
                        .startingBid(item.getStartingBid())
                        .buyNowPrice(item.getBuyNowPrice())
                        .status(item.getStatus())
                        .fileUrl(item.getFileUrl())
                        .startTime(item.getStartTime())
                        .endTime(item.getEndTime())
                        .build())
                .collect(Collectors.toList());

        return new PageImpl<>(itemDtos, itemPage.getPageable(), itemPage.getTotalElements());
    }


    @Transactional
    public Item createItem(Item item, MultipartFile image, String itemPeriod) throws IOException {
        if (!image.isEmpty()) {
            String uploadPath;
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
                uploadPath = "C:/uploads/";
            } else {
                uploadPath = "/var/uploads/";
            }
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


    @Transactional
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
        updateItemStatus();

        return item;
    }

    //상태확인

    @Transactional
    public void updateItemStatus() {
        LocalDateTime currentTime = LocalDateTime.now();

        // 'ONGOING' 상태의 아이템들을 가져옵니다.
        List<Item> ongoingItems = itemRepository.findByStatus(Item.ItemStatus.ONGOING);

        for (Item it : ongoingItems) {
            // 아이템의 endTime이 현재 시간보다 이전이면 상태를 'BIDDING_END'로 업데이트
            if (it.getEndTime().isBefore(currentTime)) {
                it.setStatus(Item.ItemStatus.BIDDING_END);
                itemRepository.save(it); // 상태 업데이트
            }
        }
    }

    @Transactional
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