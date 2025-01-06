package com.hammer.hammer.bid.controller;

import com.hammer.hammer.bid.dto.ResponseBidByUserDto;
import com.hammer.hammer.bid.service.BidService;
import com.hammer.hammer.transaction.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/bid")
public class BidController {

    private final BidService bidService;


    @GetMapping("/user/{userId}")
    public String getBidsByUser(@PathVariable Long userId,
                                Model model,
                                @PageableDefault(page = 0, size = 10) Pageable pageable,
                                @RequestParam(defaultValue = "") String sort,
                                @RequestParam(defaultValue = "") String itemName,
                                @AuthenticationPrincipal UserDetails authenticatedPrincipal) {

        if (userId == null) {
            model.addAttribute("userError", "사용자가 없습니다.");
        }

        Page<ResponseBidByUserDto> bidsByUser = bidService.getBidsByUser(userId,pageable,sort,itemName,authenticatedPrincipal);
        model.addAttribute("bids",bidsByUser);
        model.addAttribute("currentPage", pageable.getPageNumber());
        model.addAttribute("totalPages", bidsByUser.getTotalPages());
        model.addAttribute("data-user-id",userId);
        model.addAttribute("sortParam", sort);
        model.addAttribute("itemName", itemName);

        return "bid/bidsByUser";
    }
}
