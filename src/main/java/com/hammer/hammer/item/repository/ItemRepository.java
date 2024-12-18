package com.hammer.hammer.item.repository;

import com.hammer.hammer.item.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<com.hammer.hammer.domain.Item> findByStatus(com.hammer.hammer.domain.Item.ItemStatus itemStatus);
}