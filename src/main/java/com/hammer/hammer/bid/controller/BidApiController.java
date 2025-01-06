package com.hammer.hammer.bid.controller;


import com.hammer.hammer.bid.dto.RequestBidDto;
import com.hammer.hammer.bid.dto.ResponseBidByItemDto;
import com.hammer.hammer.bid.dto.ResponseBidByUserDto;
import com.hammer.hammer.bid.exception.BidAmountTooLowException;
import com.hammer.hammer.bid.service.BidService;
import com.hammer.hammer.transaction.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bid")
@Slf4j
public class BidApiController {
    private final BidService bidService;
    private final TransactionService transactionService;

    /**
     * 입찰 등록
     */
    @PostMapping
    public ResponseEntity<String> createBid(@ModelAttribute @Valid RequestBidDto requestBidDto,
                                            Model model,
                                            BindingResult bindingResult,
                                            @AuthenticationPrincipal UserDetails userDetails) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("validFail", "입력값이 잘못되었습니다.");
            return new ResponseEntity<>("입력값이 잘못되었습니다.", HttpStatus.BAD_REQUEST);
        }

        if (userDetails == null) {
            return new ResponseEntity<>("로그인 정보가 필요합니다.", HttpStatus.UNAUTHORIZED);
        }

        try {
            bidService.saveBid(requestBidDto, userDetails);
            model.addAttribute("highestBid", requestBidDto.getBidAmount());
            model.addAttribute("success", true);
            return new ResponseEntity<>("입찰 성공! 현재가가 갱신되었습니다.", HttpStatus.OK);
        } catch (BidAmountTooLowException | IllegalStateException | IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("/buy/now")
    public ResponseEntity<String> buyNow(@ModelAttribute @Valid RequestBidDto requestBidDto,
                                         @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            return new ResponseEntity<>("로그인 정보가 필요합니다.", HttpStatus.UNAUTHORIZED);
        }

        try {

            bidService.saveBid(requestBidDto, userDetails,"buyNow");
            transactionService.createTransactionForImmediatePurchase(requestBidDto.getItemId());
            return new ResponseEntity<>("즉시 구매가 성공적으로 처리되었습니다.", HttpStatus.OK);
        } catch (BidAmountTooLowException | IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
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

        List<ResponseBidByItemDto> bidsByItem = bidService.getBidsByItem(itemId);

        model.addAttribute("bids",bidsByItem);

        return "bid/bidsByItem";
    }

}
