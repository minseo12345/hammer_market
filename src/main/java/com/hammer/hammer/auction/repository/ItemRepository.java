package com.hammer.hammer.auction.repository;

import com.hammer.hammer.auction.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
}