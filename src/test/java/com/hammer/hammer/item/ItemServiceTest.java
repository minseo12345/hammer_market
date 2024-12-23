package com.hammer.hammer.item;

import com.hammer.hammer.item.entity.Item;
import com.hammer.hammer.item.repository.ItemRepository;
import com.hammer.hammer.item.service.ItemService;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@TestConfiguration
@EnableWebSecurity
@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @InjectMocks
    private ItemService itemService; 

    @Mock
    private ItemRepository itemRepository; 

    @Test
    void testCreateItemWithValidBuyNowPrice() throws Exception {
        // Given
        Item item = new Item();
        item.setTitle("Test Item");
        item.setStartingBid(BigDecimal.valueOf(100));
        item.setBuyNowPrice(BigDecimal.valueOf(200));
        item.setStartTime(LocalDateTime.now());
        item.setStatus(Item.ItemStatus.ONGOING);

        when(itemRepository.save(any(Item.class))).thenReturn(item);

        // When
        Item savedItem = itemService.createItem(item, null);

        // Then
        assertNotNull(savedItem);
        assertEquals("Test Item", savedItem.getTitle());
        assertEquals(BigDecimal.valueOf(200), savedItem.getBuyNowPrice());
        assertEquals(Item.ItemStatus.ONGOING, savedItem.getStatus());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void testCreateItemWithInvalidBuyNowPrice() {
        // Given
        Item item = new Item();
        item.setTitle("Test Item");
        item.setStartingBid(BigDecimal.valueOf(100));
        item.setBuyNowPrice(BigDecimal.valueOf(50)); 

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            itemService.createItem(item, null);
        });

        assertEquals("즉시 구매 가격은 시작 가격보다 높아야 합니다.", exception.getMessage());
        verify(itemRepository, times(0)).save(any(Item.class));
    }

    @Test
    void testGetAllItems() {
        // Given
        Item item1 = new Item();
        item1.setTitle("Item 1");
        Item item2 = new Item();
        item2.setTitle("Item 2");

        when(itemRepository.findAll()).thenReturn(List.of(item1, item2));

        // When
        var items = itemService.getAllItems();

        // Then
        assertNotNull(items);
        assertEquals(2, items.size());
        assertEquals("Item 1", items.get(0).getTitle());
        assertEquals("Item 2", items.get(1).getTitle());
        verify(itemRepository, times(1)).findAll();
    }

    @Test
    void testUpdateAuctionStatus() {
        // Given
        Item item = new Item();
        item.setStatus(Item.ItemStatus.ONGOING);

        when(itemRepository.findById(1L)).thenReturn(java.util.Optional.of(item));

        // When
        Item updatedItem = itemService.updateAuctionStatus(1L, Item.ItemStatus.COMPLETED);

        // Then
        assertNotNull(updatedItem);
        assertEquals(Item.ItemStatus.COMPLETED, updatedItem.getStatus());
        verify(itemRepository, times(1)).findById(1L);
        verify(itemRepository, times(1)).save(item);
    }

    @Test
    void testGetItemById() {
        // Given
        Item item = new Item();
        item.setTitle("Test Item");

        when(itemRepository.findById(1L)).thenReturn(java.util.Optional.of(item));

        // When
        Item foundItem = itemService.getItemById(1L);

        // Then
        assertNotNull(foundItem);
        assertEquals("Test Item", foundItem.getTitle());
        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteItem() {
        // Given
        Long itemId = 1L;

        doNothing().when(itemRepository).deleteById(itemId);

        // When
        itemService.deleteItem(itemId);

        // Then
        verify(itemRepository, times(1)).deleteById(itemId);
    }
}