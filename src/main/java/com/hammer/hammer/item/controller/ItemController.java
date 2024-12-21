package com.hammer.hammer.item.controller;

import com.hammer.hammer.bid.service.BidService;
import com.hammer.hammer.item.entity.Item;
import com.hammer.hammer.item.service.ItemService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final BidService bidService;

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
    public String getAuctionDetailPage(@PathVariable Long id, Model model) {
        Item item = itemService.getItemById(id);
        BigDecimal highestBid = bidService.getHighestBidAmount(id); 

        model.addAttribute("item", item);
        model.addAttribute("highestBid", highestBid);

        return "auction/detail"; 
    }

    @PostMapping("/detail/{id}/bid")
    public String placeBid(@PathVariable Long id, @RequestParam("bidAmount") String bidAmount, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("message", "입찰이 성공적으로 완료되었습니다!");
        return "redirect:/items/detail/" + id;
    }
}