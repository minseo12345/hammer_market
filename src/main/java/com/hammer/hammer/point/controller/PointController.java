package com.hammer.hammer.point.controller;

import com.hammer.hammer.point.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class PointController {
    private final PointService pointService;
}
