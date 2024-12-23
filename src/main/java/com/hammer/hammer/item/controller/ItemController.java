package com.hammer.hammer.item.controller;

import com.hammer.hammer.bid.dto.RequestBidDto;
import com.hammer.hammer.bid.exception.BidAmountTooLowException;
import com.hammer.hammer.bid.service.BidService;
import com.hammer.hammer.item.entity.Item;
import com.hammer.hammer.item.service.ItemService;
import com.hammer.hammer.user.entity.User;
import com.hammer.hammer.user.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
@Slf4j
@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final BidService bidService;
    private final UserService userService;
    @PostMapping("/create")
    public String createItem(@ModelAttribute Item item,
                             @RequestParam("image") MultipartFile image,
                             @RequestParam("itemPeriod") String itemPeriod,
                             RedirectAttributes redirectAttributes) throws IOException {
        // 사용자 ID를 1로 가정하고 조회 (테스트용)
        User user = userService.getUserById(1L);
        itemService.createItem(item, image, user, itemPeriod);

        redirectAttributes.addFlashAttribute("message", "경매가 성공적으로 생성되었습니다!");
        return "redirect:/items/list";
    }

    @GetMapping("/list")
    public String getAuctionListPage(Model model) {
        List<Item> items = itemService.getAllItems();
        model.addAttribute("items", items);
        return "item/list";
    }
    @GetMapping("/create")
    public String getAuctionCreatePage() {
        return "item/create";
    }

    @GetMapping("/detail/{id}")
    public String getAuctionDetailPage(@PathVariable Long id, Model model) {
        // 상품 및 최고 입찰가 조회
        Item item = itemService.getItemById(id);
        BigDecimal highestBid = bidService.getHighestBidAmount(id);

        // 최고 입찰가가 없으면 시작가로 설정
        if (highestBid.compareTo(BigDecimal.ZERO) == 0) {
            highestBid = item.getStartingBid();
        }

        model.addAttribute("item", item);
        model.addAttribute("highestBid", highestBid);

        if (item.getStatus() == Item.ItemStatus.COMPLETED) {
            return "item/soldout"; // 판매 완료 화면으로 이동
        }

        return "item/detail";
    }

    @PostMapping("/detail/{id}/bid")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> placeBid(@PathVariable Long id,
                                                        @RequestBody RequestBidDto requestBidDto) {
        try {
            // 입찰 저장
            bidService.saveBid(requestBidDto);

            // 새로운 최고 입찰가 반환
            BigDecimal highestBid = bidService.getHighestBidAmount(id);
            Map<String, Object> response = Map.of(
                    "success", true,
                    "highestBid", highestBid
            );
            return ResponseEntity.ok(response);
        } catch (BidAmountTooLowException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "예상치 못한 오류가 발생했습니다."));
        }
    }
    @GetMapping("/detail/{id}/highest-bid")
    @ResponseBody
    public BigDecimal getHighestBid(@PathVariable Long id) {
        return bidService.getHighestBidAmount(id);
    }

}