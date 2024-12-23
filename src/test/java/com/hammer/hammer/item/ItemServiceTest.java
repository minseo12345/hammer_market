package com.hammer.hammer.item;

import com.hammer.hammer.item.entity.Item;
import com.hammer.hammer.item.repository.ItemRepository;
import com.hammer.hammer.item.service.ItemService;
import com.hammer.hammer.bid.service.BidService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ItemServiceTest {

    @InjectMocks
    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BidService bidService;

    private Item mockItem;

    @BeforeEach
    public void setup() {
        mockItem = new Item();
        mockItem.setItemId(1L);
        mockItem.setTitle("Test Item");
        mockItem.setStartingBid(BigDecimal.valueOf(5000));
        mockItem.setBuyNowPrice(BigDecimal.valueOf(10000));
        mockItem.setStartTime(LocalDateTime.now());
        mockItem.setEndTime(LocalDateTime.now().plusDays(3));
        mockItem.setStatus(Item.ItemStatus.ONGOING);
    }
    /**
     * 테스트: 즉시 구매 가격이 시작 가격보다 높은지 확인
     */
    @Test
    public void testBuyNowPriceHigherThanStartingBid() throws IOException {
        // Given
        mockItem.setBuyNowPrice(BigDecimal.valueOf(10000));

        // When
        when(itemRepository.save(any(Item.class))).thenReturn(mockItem);
        Item savedItem = itemService.createItem(mockItem, null, null, "3일");

        // Then
        assertTrue(savedItem.getBuyNowPrice().compareTo(savedItem.getStartingBid()) > 0, "즉시 구매 가격이 시작 가격보다 낮습니다.");
    }
    /**
     * 테스트: 즉시 구매 가격이 null일 때 정상적으로 처리되는지 확인
     */
    @Test
    public void testBuyNowPriceCanBeNull() throws IOException {
        // Given
        mockItem.setBuyNowPrice(null);

        // When
        when(itemRepository.save(any(Item.class))).thenReturn(mockItem);
        Item savedItem = itemService.createItem(mockItem, null, null, "3일");

        // Then
        assertNull(savedItem.getBuyNowPrice(), "즉시 구매 가격이 null이어야 합니다.");
    }
    /**
     * 테스트: 파일 URL이 정확히 매핑되는지 확인
     */
    @Test
    public void testFileUrlMapping() throws IOException {
        // Given
        mockItem.setFileUrl("/uploads/test.jpg");
        MultipartFile mockFile = new MockMultipartFile("test.jpg", "test.jpg", "image/jpeg", "test data".getBytes());

        // When
        when(itemRepository.save(any(Item.class))).thenReturn(mockItem);
        Item savedItem = itemService.createItem(mockItem, mockFile, null, "3일");

        // Then
        assertEquals("/uploads/test.jpg", savedItem.getFileUrl(), "파일 URL이 정확히 매핑되지 않았습니다.");
    }
    /**
     * 테스트: 모든 경매 아이템이 반환되는지 확인
     */
    @Test
    public void testGetAllItems() {
        // Given
        List<Item> items = List.of(mockItem);
        when(itemRepository.findAll()).thenReturn(items);

        // When
        List<Item> result = itemService.getAllItems();

        // Then
        assertEquals(1, result.size(), "아이템 개수가 일치하지 않습니다.");
        assertEquals("Test Item", result.get(0).getTitle(), "아이템 제목이 일치하지 않습니다.");
    }
    /**
     * 테스트: 입찰 테이블에서 가장 높은 입찰 금액을 올바르게 조회하는지 확인
     */
    @Test
    public void testHighestBidAmount() {
        // Given
        BigDecimal highestBid = BigDecimal.valueOf(10000);
        when(bidService.getHighestBidAmount(anyLong())).thenReturn(highestBid);

        // When
        BigDecimal result = bidService.getHighestBidAmount(1L);

        // Then
        assertEquals(BigDecimal.valueOf(10000), result, "가장 높은 입찰 가격이 일치하지 않습니다.");
    }
    /**
     * 테스트: 경매 종료 시 상태가 COMPLETED로 변경되는지 확인
     */
    @Test
    public void testCurrentBidAndAuctionCompletion() {
        // Given
        mockItem.setEndTime(LocalDateTime.now().minusDays(1)); // 경매 종료
        mockItem.setStatus(Item.ItemStatus.ONGOING);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(mockItem));

        // When
        Item result = itemService.getItemById(1L);

        // Then
        assertEquals(Item.ItemStatus.COMPLETED, result.getStatus(), "경매 상태가 COMPLETED로 변경되지 않았습니다.");
    }
    /**
     * 테스트: 경매 진행 중일 때 상태가 ONGOING으로 유지되는지 확인
     */
    @Test
    public void testAuctionOngoingStatus() {
        // Given
        mockItem.setEndTime(LocalDateTime.now().plusDays(3)); // 경매 진행 중
        mockItem.setStatus(Item.ItemStatus.ONGOING);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(mockItem));

        // When
        Item result = itemService.getItemById(1L);

        // Then
        assertEquals(Item.ItemStatus.ONGOING, result.getStatus(), "경매 상태가 ONGOING으로 유지되지 않았습니다.");
    }
}