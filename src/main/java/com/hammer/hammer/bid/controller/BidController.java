package com.hammer.hammer.bid.controller;


import com.hammer.hammer.bid.domain.Bid;
import com.hammer.hammer.bid.dto.RequestBidDto;
import com.hammer.hammer.bid.sevice.BidService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/bid")
@Slf4j
public class BidController {
    private final BidService bidService;

    /**
     * 입찰 등록
     */
    @PostMapping
    public String createBid(@RequestBody RequestBidDto requestBidDto) {
        if (requestBidDto == null) {
            throw new IllegalArgumentException("입찰 정보가 누락되었습니다.");
        }

        bidService.saveBid(requestBidDto);
        return "redirect:/";
    }

    /**
     * 입찰 내역 조회
     */
    @GetMapping("/{userId}")
    public String getBidsByUserId(@PathVariable Long userId, Model model) {
        if (userId == null) {
            model.addAttribute("error", "사용자가 없습니다.");
        }

        List<Bid> bids = bidService.getBidsByItemId(userId);
        model.addAttribute("bids",bids);
        return "/mypage/";

    }
}
