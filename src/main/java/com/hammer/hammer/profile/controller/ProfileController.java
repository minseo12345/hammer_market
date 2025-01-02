package com.hammer.hammer.profile.controller;

import com.hammer.hammer.item.entity.Item;
import com.hammer.hammer.item.service.ItemService;
import com.hammer.hammer.user.entity.User;
import com.hammer.hammer.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.util.List;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/profile")
public class ProfileController {
    private final UserService userService;
    private final ItemService itemService;

    @GetMapping("/myProfile")
    public String profile(@AuthenticationPrincipal UserDetails userDetails, Model model){
        Long userId = Long.valueOf(userDetails.getUsername());
        User userInfo = userService.getUserById(userId);
        model.addAttribute("userInfo", userInfo);
        return "/profile/myProfile";
    }

    @GetMapping("/mySell/{userId}")
    public String mySell(@PathVariable Long userId,
                         Model model,
                         @PageableDefault(page = 0, size = 10) Pageable pageable) {
        Page<Item> myItems = itemService.findByUserId(userId ,pageable);

        model.addAttribute("myItems", myItems);
        model.addAttribute("currentPage", pageable.getPageNumber());
        model.addAttribute("totalPages", myItems.getTotalPages());
        model.addAttribute("data-user-id",userId);
        return "/profile/mySell";
    }
}
