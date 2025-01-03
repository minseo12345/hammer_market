package com.hammer.hammer.fragments.layout;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/*@RequiredArgsConstructor
@Controller
@RequestMapping("/topbar")
public class FragmentsController {

//    private final UserService userService;

    // 특정 요청에서만 userId를 모델에 추가
    @RequestMapping
    public String topbar(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getName() != null) {
            try {
                Long userId = Long.parseLong(authentication.getName());
                model.addAttribute("userId", userId);
            } catch (NumberFormatException e) {
                System.err.println("Invalid userId format in authentication: " + authentication.getName());
            }
        }

        return "fragments/topbar"; // topbar.html 템플릿
    }
}*/

@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute("userId")
    public Long addUserIdToModel() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getName() != null) {
            try {
                return Long.parseLong(authentication.getName());
            } catch (NumberFormatException e) {
                System.err.println("Invalid userId format in authentication: " + authentication.getName());
            }
        }
        return null; // 비로그인 사용자 처리
    }
}

