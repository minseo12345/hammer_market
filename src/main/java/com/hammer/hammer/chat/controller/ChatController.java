package com.hammer.hammer.chat.controller;

import com.hammer.hammer.category.entity.Category;
import com.hammer.hammer.category.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Controller
public class ChatController {

    private final CategoryRepository categoryRepository;

    @GetMapping("/chat")
    public String chat() {
        return "chat/chat";
    }
    @GetMapping("/cart")
    public String cart(Model model) {
        List<Category> categories = categoryRepository.findAll();

        model.addAttribute("categories", categories);
        return "cart/cart";
    }

}
