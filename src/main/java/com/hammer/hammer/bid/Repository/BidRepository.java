package com.hammer.hammer.bid.Repository;

import com.hammer.hammer.bid.domain.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface BidRepository extends JpaRepository<Bid, Long> {
    Optional<List<Bid>> findByUserId(Long userId);
    Optional<List<Bid>> findByItemId(Long itemId);
    @Query("SELECT MAX(b.bidAmount) FROM Bid b WHERE b.item.id = :itemId")
    Optional<BigDecimal> findHighestBidByItemId(@Param("itemId") Long itemId);
}
