package com.hammer.hammer.bid;

import com.hammer.hammer.bid.Repository.BidRepository;
import com.hammer.hammer.bid.dto.ResponseBidByUserDto;
import com.hammer.hammer.bid.entity.Bid;
import com.hammer.hammer.bid.service.BidService;
import com.hammer.hammer.item.entity.Item;
import com.hammer.hammer.item.repository.ItemRepository;
import com.hammer.hammer.user.entity.User;
import com.hammer.hammer.user.repository.UserRepository;
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

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
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

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private ItemRepository itemRepository;

    @MockitoBean
    private ResponseBidByUserDto responseBidByUserDto;

    @Test
    void selectAllBidByUser() {

            // Given
            User user = new User();
            user.setUserId("1");

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

            when(bidRepository.findByUser_UserIdOrderByBidAmountDesc("1", pageable)).thenReturn(Optional.of(mockBids));
            when(bidRepository.findHighestBidByItemId(1L)).thenReturn(Optional.of(BigDecimal.valueOf(200000)));

            // When
            Page<ResponseBidByUserDto> response = bidService.getBidsByUser(user.getUserId(), pageable);

            // Then
            assertNotNull(response);
            assertEquals(1, response.getTotalElements());

            ResponseBidByUserDto dto = response.getContent().get(0);
            assertEquals(item.getItemId(), dto.getItemId());
            assertEquals(item.getTitle(), dto.getItemName());
            assertEquals(item.getFileUrl(), dto.getImg());
            assertEquals("200,000 원", dto.getMyPrice());
            assertEquals("200,000 원", dto.getCurrentPrice());
        }


    @Test
    void getBidsByUser_WhenNoBidsFound() {
        // Given
        Pageable pageable = PageRequest.of(0, 10, Sort.by("bidAmount").descending());

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            bidService.getBidsByUser("invalidUserId", pageable);
        });
    }

}


