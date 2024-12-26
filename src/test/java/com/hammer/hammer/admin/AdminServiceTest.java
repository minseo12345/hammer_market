package com.hammer.hammer.admin;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.hammer.hammer.admin.service.AdminService;
import com.hammer.hammer.item.entity.Item;
import com.hammer.hammer.item.entity.Item.ItemStatus;
import com.hammer.hammer.transaction.dto.TransactionStatusDto;
import com.hammer.hammer.transaction.repository.TransactionRepository;
import com.hammer.hammer.user.entity.Role;
import com.hammer.hammer.user.entity.User;
import com.hammer.hammer.user.repository.UserRepository;

import jakarta.validation.constraints.AssertTrue;

class AdminServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private AdminService adminService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Mock 객체 초기화
    }
    
    @Test
    @DisplayName("user 조회 TEST")
    void testGetAllUsers() {
        // Arrange: Role 및 User 객체 생성
        Role role = Role.builder()
            .roleId(1L)
            .roleName("Admin")
            .build();

        User user = User.builder()
            .userId(1L)
            .username("JohnDoe")
            .email("john.doe@example.com")
            .password("securepassword")
            .phoneNumber("1234567890")
            .role(role)
            .active(true)
            .build();

        when(userRepository.findAll()).thenReturn(Collections.singletonList(user));

        // Act: 사용자 리스트 조회
        List<User> result = adminService.getAllUsers();

        // Assert: 결과 검증
        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.size(), "Result size should be 1");

        User actualUser = result.get(0);
        assertEquals(1L, actualUser.getUserId(), "User ID should match");
        assertEquals("JohnDoe", actualUser.getName(), "Username should match");
        assertEquals("john.doe@example.com", actualUser.getUsername(), "getUsername() should return email");
        assertEquals("john.doe@example.com", actualUser.getEmail(), "Email should match");
        assertEquals("Admin", actualUser.getRole().getRoleName(), "Role name should match");
    }


    
    @Test
    @DisplayName("경매현황 조회 TEST")
    void testGetTransactionStatus() {
        LocalDateTime fixedDate = LocalDateTime.of(2024, 1, 1, 10, 0);

        // Arrange - Mock 데이터 생성
        TransactionStatusDto status = new TransactionStatusDto(
            1L, // sellerId as Long
            "seller1@example.com", // sellerEmail
            "Item1", // itemTitle
            new BigDecimal("100.00"), // finalPrice
            fixedDate, // transactionDate
            "buyer1@example.com", // buyerEmail
            ItemStatus.BIDDING_END // status as ENUM
        );

        // Mock repository response
        when(transactionRepository.findAllTransactionStatus()).thenReturn(Collections.singletonList(status));

    
        List<TransactionStatusDto> result = adminService.getTransactionStatuses();

        // Assert - 검증
        assertNotNull(result);
        assertEquals(1, result.size());

        TransactionStatusDto actual = result.get(0);
        assertEquals(1L, actual.getSellerId());
        assertEquals("seller1@example.com", actual.getSellerEmail());
        assertEquals("Item1", actual.getItemTitle());
        assertEquals(new BigDecimal("100.00"), actual.getFinalPrice());
        assertEquals(fixedDate, actual.getTransactionDate());
        assertEquals("buyer1@example.com", actual.getBuyerEmail());
        assertEquals(ItemStatus.BIDDING_END, actual.getStatus());
    }

}
