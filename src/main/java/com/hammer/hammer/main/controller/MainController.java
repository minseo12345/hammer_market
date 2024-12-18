package com.hammer.hammer.main.controller;

import com.hammer.hammer.bid.service.BidService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/main")
public class MainController {

    private BidService bidService;

    @GetMapping
    public String mainPage() {
        // "main.html" 파일을 반환
        return "main";
    }
}
