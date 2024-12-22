package com.hammer.hammer.bid;

import com.hammer.hammer.bid.repository.BidRepository;
import com.hammer.hammer.bid.dto.ResponseBidByUserDto;
import com.hammer.hammer.bid.entity.Bid;
import com.hammer.hammer.bid.service.BidService;
import com.hammer.hammer.item.entity.Item;
import com.hammer.hammer.user.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class SelectBidByUserTest {

    @Autowired
    @InjectMocks
    private BidService bidService;

    @MockitoBean
    private BidRepository bidRepository;

    /**
     * 사용자별 모든 입찰 목록 조회 테스트
     */
    @Test
    void selectAllBidByUser() {
        // Given
        User user = new User();
        user.setUserId(1L);

        Item item = new Item();
        item.setItemId(1L);
        item.setTitle("신발");
        item.setFileUrl("http://example.com/image.jpg");

        Bid bid = Bid.builder()
                .bidId(1L)
                .user(user)
                .item(item)
                .bidAmount(BigDecimal.valueOf(200000))
                .bidTime(LocalDateTime.now())
                .build();

        Page<Bid> mockBids = new PageImpl<>(List.of(bid));
        Pageable pageable = PageRequest.of(0, 10, Sort.by("bidAmount").descending());

        when(bidRepository.findByUser_UserId(1L, pageable)).thenReturn(Optional.of(mockBids));
        when(bidRepository.findHighestBidByItemId(1L)).thenReturn(Optional.of(BigDecimal.valueOf(200000)));

        // When
        Page<ResponseBidByUserDto> response = bidService.getBidsByUser(user.getUserId(), pageable, "", "");

        // Then
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());

        ResponseBidByUserDto dto = response.getContent().get(0);
        assertEquals(item.getItemId(), dto.getItemId());
        assertEquals(item.getTitle(), dto.getItemName());
        assertEquals(item.getFileUrl(), dto.getImg());
        assertEquals("200,000원", dto.getMyPrice());
        assertEquals("200,000원", dto.getCurrentPrice());
    }

    /**
     * 입찰 데이터가 없는 사용자의 조회 테스트
     */
    @Test
    void getBidsByUser_WhenNoBidsFound() {
        // Given
        Pageable pageable = PageRequest.of(0, 10, Sort.by("bidAmount").descending());

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            bidService.getBidsByUser(999L, pageable, "", "");
        });
    }

    /**
     * 페이지네이션 조건이 포함된 조회 테스트
     */
    @Test
    void selectAllBidByUser_WithPagination() {
        // Given
        User user = new User();
        user.setUserId(1L);

        Item item1 = new Item();
        item1.setItemId(1L);
        item1.setTitle("신발");
        item1.setFileUrl("http://example.com/image1.jpg");

        Item item2 = new Item();
        item2.setItemId(2L);
        item2.setTitle("가방");
        item2.setFileUrl("http://example.com/image2.jpg");

        Bid bid1 = Bid.builder()
                .bidId(1L)
                .user(user)
                .item(item1)
                .bidAmount(BigDecimal.valueOf(200000))
                .bidTime(LocalDateTime.now())
                .build();

        Bid bid2 = Bid.builder()
                .bidId(2L)
                .user(user)
                .item(item2)
                .bidAmount(BigDecimal.valueOf(150000))
                .bidTime(LocalDateTime.now())
                .build();

        List<Bid> bidListPage1 = List.of(bid1);
        List<Bid> bidListPage2 = List.of(bid2);

        Page<Bid> page1 = new PageImpl<>(bidListPage1, PageRequest.of(0, 1, Sort.by("bidAmount").descending()), 2);
        Page<Bid> page2 = new PageImpl<>(bidListPage2, PageRequest.of(1, 1, Sort.by("bidAmount").descending()), 2);

        when(bidRepository.findByUser_UserId(1L, PageRequest.of(0, 1, Sort.by("bidAmount").descending()))).thenReturn(Optional.of(page1));
        when(bidRepository.findByUser_UserId(1L, PageRequest.of(1, 1, Sort.by("bidAmount").descending()))).thenReturn(Optional.of(page2));

        // When
        when(bidRepository.findHighestBidByItemId(1L)).thenReturn(Optional.of(BigDecimal.valueOf(200000)));
        when(bidRepository.findHighestBidByItemId(2L)).thenReturn(Optional.of(BigDecimal.valueOf(150000)));

        // Then
        // 페이지1 테스트
        Page<ResponseBidByUserDto> responsePage1 = bidService.getBidsByUser(user.getUserId(), PageRequest.of(0, 1, Sort.by("bidAmount").descending()), "", "");
        assertNotNull(responsePage1);
        assertEquals(1, responsePage1.getTotalElements());
        ResponseBidByUserDto dtoPage1 = responsePage1.getContent().get(0);
        assertEquals(item1.getItemId(), dtoPage1.getItemId());
        assertEquals("200,000원", dtoPage1.getMyPrice());

        // 페이지2 테스트
        Page<ResponseBidByUserDto> responsePage2 = bidService.getBidsByUser(user.getUserId(), PageRequest.of(1, 1, Sort.by("bidAmount").descending()), "", "");
        assertNotNull(responsePage2);
        assertEquals(1, responsePage2.getTotalElements());
        ResponseBidByUserDto dtoPage2 = responsePage2.getContent().get(0);
        assertEquals(item2.getItemId(), dtoPage2.getItemId());
        assertEquals("150,000원", dtoPage2.getMyPrice());
    }


    /**
     * 검색 조건이 포함된 사용자 입찰 목록 조회 테스트
     */
    @Test
    void getBidsByUser_WithSearchCondition() {
        // Given
        User user = new User();
        user.setUserId(1L);

        Item item = new Item();
        item.setItemId(1L);
        item.setTitle("신발");
        item.setFileUrl("http://example.com/image.jpg");

        Bid bid = Bid.builder()
                .bidId(1L)
                .user(user)
                .item(item)
                .bidAmount(BigDecimal.valueOf(300000))
                .bidTime(LocalDateTime.now())
                .build();

        Page<Bid> mockBids = new PageImpl<>(List.of(bid));
        Pageable pageable = PageRequest.of(0, 10, Sort.by("bidAmount").descending());

        when(bidRepository.findByItem_TitleContainingIgnoreCase("신발", pageable)).thenReturn(Optional.of(mockBids));
        when(bidRepository.findHighestBidByItemId(1L)).thenReturn(Optional.of(BigDecimal.valueOf(300000)));

        // When
        Page<ResponseBidByUserDto> response = bidService.getBidsByUser(user.getUserId(), pageable, "bidAmount", "신발");

        // Then
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());

        ResponseBidByUserDto dto = response.getContent().get(0);
        assertEquals(item.getItemId(), dto.getItemId());
        assertEquals(item.getTitle(), dto.getItemName());
        assertEquals(item.getFileUrl(), dto.getImg());
        assertEquals("300,000원", dto.getMyPrice());
        assertEquals("300,000원", dto.getCurrentPrice());
    }
}
