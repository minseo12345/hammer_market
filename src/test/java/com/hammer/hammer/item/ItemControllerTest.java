package com.hammer.hammer.item;


import com.hammer.hammer.item.entity.Item;
import com.hammer.hammer.item.service.ItemService;
import com.hammer.hammer.bid.service.BidService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @MockBean
    private BidService bidService;

    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    void testGetAuctionDetailPage() throws Exception {
        // Given
        Item mockItem = new Item();
        mockItem.setItemId(1L);
        mockItem.setTitle("Test Item");
        mockItem.setDescription("Test Description");
        mockItem.setStartingBid(BigDecimal.valueOf(100));
        mockItem.setBuyNowPrice(BigDecimal.valueOf(200));
        mockItem.setStatus(Item.ItemStatus.ONGOING);
        mockItem.setFileUrl("/uploads/test.jpg");

        BigDecimal highestBid = BigDecimal.valueOf(150);

        Mockito.when(itemService.getItemById(1L)).thenReturn(mockItem);
        Mockito.when(bidService.getHighestBidAmount(1L)).thenReturn(highestBid);

        // When & Then
        mockMvc.perform(get("/items/detail/1"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("item", mockItem))
                .andExpect(model().attribute("highestBid", highestBid))
                .andExpect(view().name("auction/detail"));
    }
}