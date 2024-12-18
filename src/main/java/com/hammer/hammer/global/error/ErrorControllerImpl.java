package com.hammer.hammer.global.error;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Slf4j
@Controller
public class ErrorControllerImpl implements ErrorController {

    @GetMapping("/error")
    public String error(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");

        if (statusCode != null) {
            log.error("Error occurred with status code: {}", statusCode);
        } else {
            log.error("An unknown error occurred");
        }

        return "global/error";
    }
}
