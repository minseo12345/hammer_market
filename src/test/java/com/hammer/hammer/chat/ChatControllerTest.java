package com.hammer.hammer.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hammer.hammer.chat.entity.ChatRoom;
import com.hammer.hammer.chat.entity.ChatRoomDto;
import com.hammer.hammer.chat.service.ChatService;
import com.hammer.hammer.user.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ChatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ChatService chatService;

    private User mockSeller;
    private User mockBuyer;
    private ChatRoom mockChatRoom;

    @BeforeEach
    void setUp() {
        mockSeller = User.builder()
                .userId(1L)
                .username("seller")
                .password("123")
                .email("seller@seller.com")
                .build();

        mockBuyer = User.builder()
                .userId(2L)
                .username("buyer")
                .password("123")
                .email("buyer@buyer.com")
                .build();

        mockChatRoom=chatService.findOrCreateChatRoom(1L,2L);
        chatService.saveMessage(mockChatRoom.getId(),mockBuyer.getUserId(),"hello");
        chatService.saveMessage(mockChatRoom.getId(),mockBuyer.getUserId(),"Are you there?");
    }

    @AfterEach
    void tearDown() {
        chatService.deleteEveryThings();
    }


    @Test
    void testGetChatRoom() throws Exception {
        ChatRoomDto chatRoomDto = new ChatRoomDto(2L, 1L);

        mockMvc.perform(post("/chat/createChatRoom")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chatRoomDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockChatRoom.getId()));
    }

    @Test
    void testGetMessagesByChatRoom() throws Exception {

        mockMvc.perform(get("/chat/"+mockChatRoom.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].content").value("hello"))
                .andExpect(jsonPath("$[1].content").value("Are you there?"));
    }

    @Test
    void testGetUserChatRooms() throws Exception {

        mockMvc.perform(get("/chat/chatrooms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(mockChatRoom.getId()));
    }

    @Test
    void testGetUnreadCount() throws Exception {

        mockMvc.perform(get("/chat/chatrooms/"+mockChatRoom.getId()+"/unreadCount")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
    }

    @Test
    void testMarkMessagesAsRead() throws Exception {

        mockMvc.perform(post("/chat/"+mockChatRoom.getId()+"/read")
                        .param("userId", "1"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/chat/chatrooms/"+mockChatRoom.getId()+"/unreadCount")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }
}
