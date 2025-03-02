package com.hammer.hammer.bid.repository;

import com.hammer.hammer.bid.entity.Bid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface BidRepository extends JpaRepository<Bid, Long> {
    Optional<Page<Bid>> findByUser_UserIdOrderByBidAmountDesc(Long userId, Pageable pageable);
    Optional<List<Bid>> findByItem_ItemIdOrderByBidAmountDesc(Long itemId);
    @Query("SELECT MAX(b.bidAmount) FROM Bid b WHERE b.item.itemId = :itemId")
    Optional<BigDecimal> findHighestBidByItemId(@Param("itemId") Long itemId);
    Optional<Object> findTopByItem_ItemIdOrderByBidAmountDesc(Long itemId);
    Optional<Page<Bid>> findByUser_UserId(Long userId, Pageable pageable);
    Optional<Page<Bid>> findByItem_TitleContainingIgnoreCase(String itemName, Pageable pageable);
    Optional<List<Bid>> findBidByItem_ItemId(Long itemId);
}
