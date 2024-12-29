package com.hammer.hammer.point;

import com.hammer.hammer.point.dto.ResponseSelectPointDto;
import com.hammer.hammer.point.entity.Point;
import com.hammer.hammer.point.entity.PointStatus;
import com.hammer.hammer.point.repository.PointRepository;
import com.hammer.hammer.point.service.PointService;
import com.hammer.hammer.user.entity.User;
import com.hammer.hammer.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class SelectPointsByUserTest {

    @Autowired
    @InjectMocks
    private PointService pointService;

    @MockitoBean
    private PointRepository pointRepository;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private UserDetails userDetails;

    @Test
    @DisplayName("사용자 입출금 내역 조회")
    void selectAllPointsByUser(){

        //Given
        User selectedUser = User.builder()
                .userId(1L)
                .build();

        Point points = Point.builder()
                .pointId(1L)
                .pointType(PointStatus.D)
                .pointAmount(BigDecimal.valueOf(20000))
                .createDate(LocalDateTime.parse("2024-12-10T00:00:00"))
                .description("충전")
                .user(selectedUser)
                .build();

        when(pointRepository.save(points)).thenReturn(points);
        when(userRepository.save(selectedUser)).thenReturn(selectedUser);
        when(pointRepository.findByUser_UserId(selectedUser.getUserId())).thenReturn(Optional.of(List.of(points)));
        when(userDetails.getUsername()).thenReturn("1");

        //When
        List<ResponseSelectPointDto> pointLisByUser = pointService.getAllPoints(selectedUser.getUserId(),null);

        //Then
        assertEquals(1, pointLisByUser.size());
        assertEquals(BigDecimal.valueOf(20000), pointLisByUser.get(0).getPointAmount());
        assertEquals(PointStatus.D, pointLisByUser.get(0).getPointType());
        assertEquals("충전", pointLisByUser.get(0).getDescription());
        assertEquals(LocalDateTime.parse("2024-12-10T00:00:00"),pointLisByUser.get(0).getCreateAt());
    }
}
