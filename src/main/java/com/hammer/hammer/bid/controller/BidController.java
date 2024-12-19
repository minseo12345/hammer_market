package com.hammer.hammer.bid.controller;


import com.hammer.hammer.bid.dto.RequestBidDto;
import com.hammer.hammer.bid.dto.ResponseBidByItemDto;
import com.hammer.hammer.bid.dto.ResponseBidByUserDto;
import com.hammer.hammer.bid.exception.BidAmountTooLowException;
import com.hammer.hammer.bid.service.BidService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
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
    public String createBid(@ModelAttribute @Valid RequestBidDto requestBidDto,
                             Model model,
                             BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("validFail", "입력값이 잘못되었습니다.");
            return "item/detail";
        }

        try{
            bidService.saveBid(requestBidDto);
            return "redirect:/item/detail";
        }catch (BidAmountTooLowException | IllegalStateException e){
            model.addAttribute("bidPrice", e.getMessage());
        }
        return "item/detail";

    }

    /**
     * 사용자 별 입찰 내역 조회
     */
    @GetMapping("/user/{userId}")
    public String getBidsByUser(@PathVariable Long userId,
                                Model model,
                                @PageableDefault(page = 0, size = 10) Pageable pageable) {
        if (userId == null) {
            model.addAttribute("userError", "사용자가 없습니다.");
        }

        Page<ResponseBidByUserDto> bidsByUser = bidService.getBidsByUser(userId,pageable);
        model.addAttribute("bids",bidsByUser);
        model.addAttribute("currentPage", pageable.getPageNumber());
        model.addAttribute("totalPages", bidsByUser.getTotalPages());

        return "bid/bidsByUser";

    }

    /**
     *  상품 별 입찰 내역 조회
     */
    @GetMapping("/item/{itemId}")
    public String getBidsByItem(@PathVariable Long itemId,
                                Model model ,
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
