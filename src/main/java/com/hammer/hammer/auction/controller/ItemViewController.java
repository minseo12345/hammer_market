package com.hammer.hammer.auction.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.hammer.hammer.auction.entity.Item;
import com.hammer.hammer.auction.service.ItemService;

@Controller
@RequestMapping("/items")
public class ItemViewController {

    private final ItemService itemService;

    public ItemViewController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping("/create")
    public String createItem(@ModelAttribute Item item, @RequestParam("image") MultipartFile image, RedirectAttributes redirectAttributes) throws IOException {
      
        itemService.createItem(item, image);
 
       redirectAttributes.addFlashAttribute("message", "경매가 성공적으로 생성되었습니다!");

        return "redirect:/items/list";
    }

    @GetMapping("/list")
    public String getAuctionListPage(Model model) {
       
        List<Item> items = itemService.getAllItems();
        model.addAttribute("items", items); 
        return "auction-list"; 
    }

    @GetMapping("/create")
    public String getAuctionCreatePage() {
        return "auction-create"; 
    }
    @GetMapping("/detail/{id}")
    public String getAuctionDetailPage(@PathVariable("id") Long id, Model model) {
        Item item = itemService.getItemById(id); 
        model.addAttribute("item", item); 
        return "auction-detail";
    }
}