package com.hammer.hammer.point.controller;

import com.hammer.hammer.point.dto.RequestChargePointDto;
import com.hammer.hammer.point.dto.ResponseCurrentPointDto;
import com.hammer.hammer.point.dto.ResponseSelectPointDto;
import com.hammer.hammer.point.entity.PointStatus;
import com.hammer.hammer.point.service.PointService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/points")
public class PointController {
    private final PointService pointService;

    @GetMapping("/select/{userId}")
    public String getAllPointsByUser(@PathVariable Long userId,
                                     Model model,
                                     @RequestParam(required = false ,defaultValue = "ALL") String type,
                                     @AuthenticationPrincipal UserDetails userDetails,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "10") int size) {

        if(userId == null) {
            model.addAttribute("error", "잘못된 요청입니다.");
            return "global/error";
        }
        if(userDetails == null) {
            model.addAttribute("error", "로그인이 필요합니다.");
            return "global/error";
        }

        PointStatus pointType = (type != null) ? PointStatus.valueOf(type) : null;

        Page<ResponseSelectPointDto> responseSelectPointDtoList = pointService.getAllPoints(userId,pointType,userDetails,page,size);

        ResponseCurrentPointDto responseCurrentPointDto = pointService.currentPointByUser(userId,userDetails);
        model.addAttribute("type" ,type);
        model.addAttribute("currentPoint",responseCurrentPointDto.getCurrentPoint());
        model.addAttribute("points",responseSelectPointDtoList);
        model.addAttribute("userId", userId);

        return "point/selectPoints";
    }

    @GetMapping("/charge/{userId}")
    public String showChargePointPage(@PathVariable Long userId,
                                      @AuthenticationPrincipal UserDetails userDetails,
                                      Model model) {
        if (userDetails == null) {
            model.addAttribute("error", "로그인이 필요합니다.");
            return "global/error";
        }

        ResponseCurrentPointDto responseCurrentPointDto = pointService.currentPointByUser(userId,userDetails);

        model.addAttribute("userId", userId);
        model.addAttribute("currentPoint", responseCurrentPointDto.getCurrentPoint());
        return "point/chargePoint";
    }


    @PostMapping("/charge/{userId}")
    public String chargePoint(@PathVariable Long userId,
                              @Valid @ModelAttribute RequestChargePointDto requestChargePointDto,
                              Model model,
                              BindingResult bindingResult,
                              @AuthenticationPrincipal UserDetails userDetails) {
        if(userId == null) {
            model.addAttribute("error", "잘못된 요청입니다.");
//            return "global/error";
        }
        if(userDetails == null) {
            model.addAttribute("error","로그인이 필요합니다.");
//            return "global/error";
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "유효하지 않은 입력 값입니다.");
            model.addAttribute("bindingResult", bindingResult);
//            return "global/error";
        }
        try{
            pointService.chargePoint(userId,requestChargePointDto,userDetails);
        }catch(Exception e){
            model.addAttribute("error", "포인트 충전 중 문제가 발생했습니다: " + e.getMessage());
//            return "global/error";
        }

        return "redirect:/points/select/"+userId;
    }

    @GetMapping("/currency/{userId}")
    public String showCurrencyPointPage(@PathVariable Long userId,
                                      @AuthenticationPrincipal UserDetails userDetails,
                                      Model model) {
        if (userDetails == null) {
            model.addAttribute("error", "로그인이 필요합니다.");
            return "global/error";
        }

        ResponseCurrentPointDto responseCurrentPointDto = pointService.currentPointByUser(userId,userDetails);

        model.addAttribute("userId", userId);
        model.addAttribute("currentPoint", responseCurrentPointDto.getCurrentPoint());
        return "point/currencyPoint";
    }

    @PostMapping("/currency/{userId}")
    public String currencyPoint(@PathVariable Long userId,
                              @Valid @ModelAttribute RequestChargePointDto requestChargePointDto,
                              Model model,
                              BindingResult bindingResult,
                              @AuthenticationPrincipal UserDetails userDetails) {

        if(userId == null) {
            model.addAttribute("error", "잘못된 요청입니다.");
            return "global/error";
        }
        if(userDetails == null) {
            model.addAttribute("error","로그인이 필요합니다.");
            return "global/error";
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("error", "유효하지 않은 입력 값입니다.");
            model.addAttribute("bindingResult", bindingResult);
            return "global/error";
        }
        try{
            pointService.currencyPoint(userId,requestChargePointDto,userDetails);
        }catch(Exception e){
            model.addAttribute("error", "포인트 환전 중 문제가 발생했습니다: " + e.getMessage());
            return "global/error";
        }

        return "redirect:/points/select/"+userId;
    }
}
