package com.hammer.hammer.bid.service;

import com.hammer.hammer.bid.entity.Bid;
import com.hammer.hammer.bid.repository.BidRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BidService {

    @Autowired
    private BidRepository bidRepository;

    public Optional<Bid> findBidById(Long bidId) {
        return bidRepository.findById(bidId); // 입찰 ID로 조회
    }

    public Bid saveBid(Bid bid) {
        return bidRepository.save(bid); // 새로운 입찰 저장
    }
}