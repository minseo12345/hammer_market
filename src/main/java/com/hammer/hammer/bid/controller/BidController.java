package com.hammer.hammer.bid.controller;


import com.hammer.hammer.bid.domain.Bid;
import com.hammer.hammer.bid.dto.RequestBidDto;
import com.hammer.hammer.bid.dto.ResponseBidByItemDto;
import com.hammer.hammer.bid.dto.ResponseBidByUserDto;
import com.hammer.hammer.bid.sevice.BidService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

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
     * 사용자 별 입찰 내역 조회
     */
    @GetMapping("/user/{userId}")
    public String getBidsByUser(@PathVariable String userId, Model model,
                                @PageableDefault(page = 0, size = 10) Pageable pageable) {
        if (userId == null) {
            model.addAttribute("userError", "사용자가 없습니다.");
        }

        Page<ResponseBidByUserDto> bidsByUser = bidService.getBidsByUser(userId,pageable);
        model.addAttribute("bids",bidsByUser);
        model.addAttribute("currentPage", pageable.getPageNumber());
        model.addAttribute("totalPages", bidsByUser.getTotalPages());
        return "mypage";

    }

    /**
     *  상품 별 입찰 내역 조회
     */
    @GetMapping("/item/{itemId}")
    public String getBidsByItem(@PathVariable Long itemId, Model model ,
                                @PageableDefault(page = 0, size = 10) Pageable pageable) {
        if (itemId == null) {
            model.addAttribute("itemError", "상품이 없습니다.");
        }
        Page<ResponseBidByItemDto> bidsByItem = bidService.getBidsByItem(itemId,pageable);
        model.addAttribute("bids",bidsByItem);
        model.addAttribute("currentPage", pageable.getPageNumber());
        model.addAttribute("totalPages", bidsByItem.getTotalPages());
        return "item";
    }

}
