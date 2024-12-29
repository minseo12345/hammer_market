package com.hammer.hammer.point.controller;

import com.hammer.hammer.point.dto.RequestChargePointDto;
import com.hammer.hammer.point.service.PointService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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
            return "/global/error";
        }
        if(userDetails == null) {
            model.addAttribute("error", "로그인이 필요합니다.");
            return "/global/error";
        }
        model.addAttribute("points",pointService.getAllPoints(userId,userDetails));
        model.addAttribute("userId", userId);

        return "point/selectPoints";
    }

    @GetMapping("/charge/{userId}")
    public String showChargePointPage(@PathVariable Long userId,
                                      @AuthenticationPrincipal UserDetails userDetails,
                                      Model model) {
        if (userDetails == null) {
            model.addAttribute("error", "로그인이 필요합니다.");
            return "/global/error";
        }

        model.addAttribute("userId", userId);
        return "/point/chargePoint";
    }


    @PostMapping("/charge/{userId}")
    public String chargePoint(@PathVariable Long userId,
                              @Valid RequestChargePointDto requestChargePointDto,
                              Model model,
                              BindingResult bindingResult,
                              @AuthenticationPrincipal UserDetails userDetails) {

        if(userId == null) {
            model.addAttribute("error", "잘못된 요청입니다.");
            return "/global/error";
        }
        if(userDetails == null) {
            model.addAttribute("error","로그인이 필요합니다.");
            return "/global/error";
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "유효하지 않은 입력 값입니다.");
            model.addAttribute("bindingResult", bindingResult); // 에러 정보를 전달
            return "/global/error";
        }
        try{
            pointService.chargePoint(userId,requestChargePointDto,userDetails);
        }catch(Exception e){
            model.addAttribute("error", "포인트 충전 중 문제가 발생했습니다: " + e.getMessage());
            return "/global/error";
        }

        return "redirect:/point/selectPoints/"+userId;
    }
}
