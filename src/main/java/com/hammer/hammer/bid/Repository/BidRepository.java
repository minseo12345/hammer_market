package com.hammer.hammer.bid.Repository;

import com.hammer.hammer.bid.domain.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BidRepository extends JpaRepository<Bid, Long> {
    Optional<List<Bid>> findByUserId(Long userId);
    Optional<List<Bid>> findByItemId(Long itemId);
}
