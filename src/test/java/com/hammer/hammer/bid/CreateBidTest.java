//package com.hammer.hammer.bid;
//
//import com.hammer.hammer.bid.repository.BidRepository;
//import com.hammer.hammer.bid.dto.RequestBidDto;
//import com.hammer.hammer.bid.entity.Bid;
//import com.hammer.hammer.bid.exception.BidAmountTooLowException;
//import com.hammer.hammer.bid.service.BidService;
//import com.hammer.hammer.item.entity.Item;
//import com.hammer.hammer.item.repository.ItemRepository;
//import com.hammer.hammer.user.entity.User;
//import com.hammer.hammer.user.repository.UserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.*;
//
//@SpringBootTest
//public class CreateBidTest {
//
//    @Autowired
//    private BidService bidService; // 실제 서비스 클래스
//
//    @MockitoBean
//    private UserRepository userRepository;
//    @MockitoBean
//    private ItemRepository itemRepository;
//
//    @MockitoBean
//    private BidRepository bidRepository;
//    @MockitoBean
//    private RequestBidDto requestBidDto; // 테스트에 필요한 DTO
//
//    @BeforeEach
//    void setUp() {
//        // 테스트에 필요한 DTO 초기화
//        requestBidDto = new RequestBidDto();
//        requestBidDto.setUserId(1L);
//        requestBidDto.setItemId(1L);
//        requestBidDto.setBidAmount(new BigDecimal("100"));
//    }
//
//    @Test
//    void saveBid_ShouldSaveBidSuccessfully() {
//        // Given
//        User user = User.builder()
//                .userId(1L)
//                .build();
//
//        Item item = new Item();
//        item.setItemId(1L);
//
//        // 최고 입찰가
//        BigDecimal highestBid = new BigDecimal("50");
//
//        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
//        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
//        when(bidRepository.findHighestBidByItemId(1L)).thenReturn(Optional.of(highestBid));
//
//        // When
//        bidService.saveBid(requestBidDto);
//
//        // Then
//        // bidRepository.save 호출 여부 확인
//        verify(bidRepository, times(1)).save(any(Bid.class));
//    }
//
//    @Test
//    void saveBidWhenBidAmountIsLow() {
//        // Given
//        User user = User.builder()
//                .userId(1L)
//                .build();
//
//        Item item = new Item();
//        item.setItemId(1L);
//
//        // 최고 입찰가
//        BigDecimal highestBid = new BigDecimal("200");
//
//        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
//        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
//        when(bidRepository.findHighestBidByItemId(1L)).thenReturn(Optional.of(highestBid));
//
//        // When and Then
//        BidAmountTooLowException exception = assertThrows(BidAmountTooLowException.class, () -> {
//            bidService.saveBid(requestBidDto);
//        });
//
//        assertEquals("입찰 금액이 현재 최고 입찰가보다 작습니다.", exception.getMessage());
//    }
//
//    @Test
//    void saveBidWhenUserNotFound() {
//        // Given
//        RequestBidDto invalidRequestBidDto = new RequestBidDto();
//        invalidRequestBidDto.setUserId(999L); // 존재하지 않는 사용자 ID
//        invalidRequestBidDto.setItemId(1L);
//        invalidRequestBidDto.setBidAmount(new BigDecimal("100"));
//
//        when(userRepository.findById(999L)).thenReturn(Optional.empty());
//
//        // When and Then
//        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
//            bidService.saveBid(invalidRequestBidDto);
//        });
//
//        assertEquals("사용자를 찾을 수 없습니다.", exception.getMessage());
//    }
//
//    @Test
//    void saveBidWhenItemNotFound() {
//        // Given
//        RequestBidDto invalidRequestBidDto = new RequestBidDto();
//        invalidRequestBidDto.setUserId(1L);
//        invalidRequestBidDto.setItemId(999L); // 존재하지 않는 상품 ID
//        invalidRequestBidDto.setBidAmount(new BigDecimal("100.00"));
//
//        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
//        when(itemRepository.findById(999L)).thenReturn(Optional.empty());
//
//        // When and Then
//        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
//            bidService.saveBid(invalidRequestBidDto);
//        });
//
//        assertEquals("상품을 찾을 수 없습니다.", exception.getMessage());
//    }
//}
