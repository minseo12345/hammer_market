package com.hammer.hammer.item.repository;

import com.hammer.hammer.item.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByItemIdIn(List<Long> itemIds);
    List<Item> findByStatus(Item.ItemStatus itemStatus);
    Page<Item> findByStatus(Item.ItemStatus itemStatus, Pageable pageable);
    Page<Item> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);
    Page<Item> findByTitleContainingIgnoreCaseAndStatus(String keyword, Pageable pageable,Item.ItemStatus itemStatus);
//    List<Item> findByUserIdAndStatus(Long userId, String status);
    Page<Item> findByUser_UserId(Long userId, Pageable pageable);

}