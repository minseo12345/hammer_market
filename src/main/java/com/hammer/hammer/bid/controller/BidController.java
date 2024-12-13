package com.hammer.hammer.bid.controller;

import com.hammer.hammer.bid.entity.Bid;
import com.hammer.hammer.bid.service.BidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/bids")
public class BidController {

    @Autowired
    private BidService bidService;

    @GetMapping
    public String bidsPage() {
        // "main.html" 파일을 반환
        return "bids";
    }

    @GetMapping("/{id}")
    public ResponseEntity<Bid> getBidById(@PathVariable Long id) {
        Optional<Bid> bid = bidService.findBidById(id);
        return bid.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build()); // ID로 조회
    }

    @PostMapping
    public ResponseEntity<Bid> createBid(@RequestBody Bid bid) {
        Bid savedBid = bidService.saveBid(bid);
        return ResponseEntity.ok(savedBid); // 새 입찰 생성
    }
}
