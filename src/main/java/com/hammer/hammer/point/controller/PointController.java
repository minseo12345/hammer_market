package com.hammer.hammer.point.controller;

import com.hammer.hammer.point.service.PointService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/points")
public class PointController {
    private final PointService pointService;

    @GetMapping("/select/{userId}")
    public String getAllPointsByUser(@PathVariable Long userId,
                                     Model model,
                                     @AuthenticationPrincipal UserDetails userDetails) {

        if(userId == null) {
            model.addAttribute("error", "잘못된 요청입니다.");
        }

        model.addAttribute("points",pointService.getAllPoints(userId));
        model.addAttribute("userId", userId);

        return "point/selectPoints";
    }
}
