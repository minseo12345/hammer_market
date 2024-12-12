package com.hammer.hammer.bid.Repository;

import com.hammer.hammer.bid.domain.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BidRepository extends JpaRepository<Bid, Long> {
}
