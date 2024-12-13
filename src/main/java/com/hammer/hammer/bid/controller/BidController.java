package com.hammer.hammer.bid.controller;

import ch.qos.logback.core.model.Model;
import com.hammer.hammer.bid.dto.RequestBidDto;
import com.hammer.hammer.bid.sevice.BidService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
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
        try {
            bidService.saveBid(requestBidDto);
            return "redirect:/";
        } catch (IllegalArgumentException e) {
            return "error";
        }
    }
}
