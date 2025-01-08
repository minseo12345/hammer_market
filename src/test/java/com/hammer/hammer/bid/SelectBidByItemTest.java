//package com.hammer.hammer.bid;
//
//import com.hammer.hammer.bid.dto.ResponseBidByItemDto;
//import com.hammer.hammer.bid.entity.Bid;
//import com.hammer.hammer.bid.repository.BidRepository;
//import com.hammer.hammer.bid.service.BidService;
//import com.hammer.hammer.item.entity.Item;
//import com.hammer.hammer.item.repository.ItemRepository;
//import com.hammer.hammer.user.entity.User;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.domain.*;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.Mockito.when;
//
//@SpringBootTest
//@ExtendWith(MockitoExtension.class)
//public class SelectBidByItemTest {
//
//    @Autowired
//    @InjectMocks
//    private BidService bidService;
//
//    @MockitoBean
//    private BidRepository bidRepository;
//
//    @MockitoBean
//    private ItemRepository itemRepository;
//
//    @Test
//    void selectAllBidByItem() {
//        // Given
//        User user = User.builder()
//                .userId(1L)
//                .build();
//
//        Item item = new Item();
//        item.setItemId(1L);
//        item.setTitle("Sample Item");
//        item.setDescription("This is a test item description.");
//        item.setCreatedAt(LocalDateTime.now());
//        item.setFileUrl("http://sample-image-url.com");
//        item.setUser(user);
//
//        Bid bid = Bid.builder()
//                .bidId(1L)
//                .user(user)
//                .item(item)
//                .bidAmount(BigDecimal.valueOf(200000))
//                .bidTime(LocalDateTime.now())
//                .build();
//
//        Page<Bid> mockBids = new PageImpl<>(List.of(bid));
//        Pageable pageable = PageRequest.of(0, 10, Sort.by("bidAmount").descending());
//
//        when(bidRepository.findByItem_ItemIdOrderByBidAmountDesc(anyLong(), any(Pageable.class)))
//                .thenReturn(Optional.of(mockBids));
//
//        // When
//        when(itemRepository.findById(item.getItemId())).thenReturn(Optional.of(item));
//
//        Page<ResponseBidByItemDto> response;
//        response = bidService.getBidsByItem(item.getItemId(), pageable);
//
//        // Then
//        assertNotNull(response);
//        assertEquals(1, response.getTotalElements());
//
//        ResponseBidByItemDto dto = response.getContent().get(0);
//
//        assertEquals(user.getUserId(), dto.getUserId());
//        assertEquals("200,000원", dto.getBidAmount());
//        assertEquals(item.getTitle(), dto.getItemName());
//        assertEquals(item.getTitle(), dto.getTitle());
//        assertEquals(item.getDescription(), dto.getDescription());
//        assertEquals(item.getCreatedAt(), dto.getCreateAt());
//        assertEquals(item.getUser().getUsername(), dto.getUsername());
//        assertEquals(item.getFileUrl(), dto.getImageUrl());
//    }
//
//    @Test
//    void getBidsByItem_WhenNoBidsFound() {
//        // Given
//        Pageable pageable = PageRequest.of(0, 10, Sort.by("bidAmount").descending());
//
//        // When & Then
//        assertThrows(IllegalStateException.class, () -> {
//            bidService.getBidsByItem(999L, pageable);  // 존재하지 않는 상품 ID
//        });
//    }
//}
