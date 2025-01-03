package com.hammer.hammer.item.controller;

import com.hammer.hammer.bid.dto.RequestBidDto;
import com.hammer.hammer.bid.exception.BidAmountTooLowException;
import com.hammer.hammer.bid.service.BidService;
import com.hammer.hammer.category.entity.Category;
import com.hammer.hammer.category.repository.CategoryRepository;
import com.hammer.hammer.item.entity.Item;
import com.hammer.hammer.item.entity.ItemResponseDto;
import com.hammer.hammer.item.service.ItemService;
import com.hammer.hammer.user.entity.User;
import com.hammer.hammer.user.repository.UserRepository;
import com.hammer.hammer.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Slf4j
@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final BidService bidService;

    private final CategoryRepository categoryRepository;

    @PostMapping("/create")
    public String createItem(@Valid @ModelAttribute Item item,
                             BindingResult bindingResult,
                             @RequestParam("image") MultipartFile image,
                             @RequestParam("itemPeriod") String itemPeriod,
                             RedirectAttributes redirectAttributes) throws IOException {

        // 검증 오류 처리
        if (bindingResult.hasErrors()) {
            // 모든 에러 메시지를 수집
            String errorMessage = bindingResult.getFieldErrors()
                    .stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining(", "));
            
            redirectAttributes.addFlashAttribute("error", errorMessage);
            return "redirect:/items/create";
        }

        // 이미지 타입 검증
        String contentType = image.getContentType();
        if (contentType == null || !(contentType.equals("image/jpeg") ||
                                    contentType.equals("image/png") ||
                                    contentType.equals("image/jpg"))) {
            redirectAttributes.addFlashAttribute("error", "JPG, JPEG 또는 PNG 형식의 이미지만 업로드 가능합니다.");
            return "redirect:/items/create";
        }


        // 경매 기간 검증
        if (itemPeriod == null || itemPeriod.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "경매 기간을 선택해주세요.");
            return "redirect:/items/create";
        }

        try {
            itemService.createItem(item, image, itemPeriod);
            redirectAttributes.addFlashAttribute("message", "경매가 성공적으로 생성되었습니다!");
            return "redirect:list";
        } catch (Exception e) {
            log.error("Item creation failed", e);
            redirectAttributes.addFlashAttribute("error", "경매 생성 중 오류가 발생했습니다.");
            return "redirect:/items/create";
        }
    }

    @GetMapping("/list")
    public String getAuctionListPage(@RequestParam(value = "page", defaultValue = "0") int page,
                                     @RequestParam(value = "sortBy", defaultValue = "itemId") String sortBy,
                                     @RequestParam(value = "status", defaultValue = "ONGOING") String status,
                                     @RequestParam(value = "direction", defaultValue = "asc") String direction,
                                     @RequestParam(value = "search", required = false) String search,
                                     @RequestParam(value = "categoryId", required = false) Long categoryId,
                                     Model model) {
        itemService.updateItemStatus();

        List<String> statuses = itemService.getAllStatuses();
        Page<ItemResponseDto> items;
        if (search != null && !search.isEmpty()) {
            items = itemService.searchItems(search,page,sortBy,direction,status,categoryId); // 검색이 포함된 서비스 메서드 호출
        } else {
            items = itemService.getAllItems(page,sortBy,direction,status,categoryId); // 검색 없이 모든 아이템 가져오기
        }
        List<Category> categories = categoryRepository.findAll();

        model.addAttribute("categories", categories);
        model.addAttribute("items", items.getContent());
        model.addAttribute("currentPage", items.getNumber());
        model.addAttribute("totalPages", items.getTotalPages());
        model.addAttribute("totalItems", items.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        model.addAttribute("search", search);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("status", status);
        model.addAttribute("statuses", statuses);
        return "item/list";
    }

    @GetMapping("/create")
    public String getAuctionCreatePage(Model model) {
        List<Category> categories=categoryRepository.findAll();
        model.addAttribute("categories",categories);
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