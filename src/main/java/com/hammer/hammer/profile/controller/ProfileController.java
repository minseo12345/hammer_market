package com.hammer.hammer.profile.controller;

import com.hammer.hammer.item.entity.Item;
import com.hammer.hammer.item.service.ItemService;
import com.hammer.hammer.profile.dto.ProfileUpdateRequestDto;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
        return "profile/myProfile";
    }

    @GetMapping("/mySell")
    public String mySell(@AuthenticationPrincipal UserDetails userDetails,
                         Model model,
                         @PageableDefault(page = 0, size = 10) Pageable pageable) {
        Long userId = Long.valueOf(userDetails.getUsername());
        Page<Item> myItems = itemService.findByUserId(userId ,pageable);

        model.addAttribute("myItems", myItems);
        model.addAttribute("currentPage", pageable.getPageNumber());
        model.addAttribute("totalPages", myItems.getTotalPages());
        model.addAttribute("data-user-id",userId);
        return "profile/mySell";
    }

    @GetMapping("/edit")
    public String showEditForm(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = Long.valueOf(userDetails.getUsername());
        User userInfo = userService.getUserById(userId);
        model.addAttribute("userInfo", userInfo);
        return "profile/editProfile";
    }

    @PostMapping("/edit")
    public String updateProfile(@ModelAttribute ProfileUpdateRequestDto request,
                                @AuthenticationPrincipal UserDetails userDetails,
                                RedirectAttributes redirectAttributes) {
        try {
            Long userId = Long.valueOf(userDetails.getUsername());
            userService.updateProfile(userId, request);
            redirectAttributes.addFlashAttribute("message", "회원정보가 성공적으로 수정되었습니다.");
            return "redirect:/profile/myProfile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "회원정보 수정 중 오류가 발생했습니다.");
            return "redirect:/profile/edit";
        }
    }
}
