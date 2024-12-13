package com.hammer.hammer.bid.repository;

import com.hammer.hammer.bid.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BidRepository extends JpaRepository<Bid, Long> {
    // 기본적인 CRUD 메서드는 JpaRepository가 제공
}