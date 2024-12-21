package com.hammer.hammer.transaction;

import com.hammer.hammer.global.jwt.auth.JwtProviderImpl;
import com.hammer.hammer.transaction.controller.TransactionController;
import com.hammer.hammer.transaction.service.TransactionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.filter.OncePerRequestFilter;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransactionController.class)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtProviderImpl jwtProvider; // JwtProviderImpl을 MockitoBean으로 등록

    @MockitoBean
    private TransactionService transactionService;

    @MockitoBean
    private OncePerRequestFilter jwtFilter; // JwtFilter를 MockitoBean으로 등록

    @Test
    @DisplayName("즉시 구매 트랜잭션 생성 성공")
    void createTransactionForImmediatePurchase_Success() throws Exception {
        // Given
        Mockito.doNothing().when(transactionService).createTransactionForImmediatePurchase(anyLong());

        // When & Then
        mockMvc.perform(post("/transactions/immediate-purchase/1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection()) // 리디렉션 상태 코드
                .andExpect(redirectedUrl("/transactions")); // 리디렉션 URL 확인
    }

    @Test
    @DisplayName("즉시 구매 트랜잭션 생성 실패")
    void createTransactionForImmediatePurchase_Failure() throws Exception {
        // Given
        doThrow(new IllegalArgumentException("Invalid item ID"))
                .when(transactionService).createTransactionForImmediatePurchase(anyLong());

        // When & Then
        mockMvc.perform(post("/transactions/immediate-purchase/1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest()) // 적절한 에러 상태 코드 확인
                .andExpect(content().string("Invalid item ID")); // 에러 메시지 확인
    }

    @Test
    @DisplayName("경매 종료 트랜잭션 생성 성공")
    void createTransactionForAuctionEnd_Success() throws Exception {
        // Given
        Mockito.doNothing().when(transactionService).createTransactionForAuctionEnd(anyLong());

        // When & Then
        mockMvc.perform(post("/transactions/auction-end/1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection()) // 리디렉션 상태 코드
                .andExpect(redirectedUrl("/transactions")); // 리디렉션 URL 확인
    }

    @Test
    @DisplayName("경매 종료 트랜잭션 생성 실패")
    void createTransactionForAuctionEnd_Failure() throws Exception {
        // Given
        doThrow(new IllegalArgumentException("Invalid item ID"))
                .when(transactionService).createTransactionForAuctionEnd(anyLong());

        // When & Then
        mockMvc.perform(post("/transactions/auction-end/1")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isBadRequest()) // 적절한 에러 상태 코드 확인
                .andExpect(content().string("Invalid item ID")); // 에러 메시지 확인
    }
}




