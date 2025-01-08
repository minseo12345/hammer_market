package com.hammer.hammer.point;

import com.hammer.hammer.point.dto.RequestChargePointDto;
import com.hammer.hammer.point.entity.Point;
import com.hammer.hammer.point.repository.PointRepository;
import com.hammer.hammer.point.service.PointService;
import com.hammer.hammer.user.entity.User;
import com.hammer.hammer.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class ChargePointTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PointRepository pointRepository;

    @InjectMocks
    private PointService pointService;

    private final UserDetails userDetails = Mockito.mock(UserDetails.class);

    @Test
    void testChargePoint_Success() {

        Long userId = 1L;
        RequestChargePointDto requestChargePointDto = new RequestChargePointDto(
                new BigDecimal("100"), "충전 테스트"
        );

        User chargePointUser = User
                .builder()
                .currentPoint(new BigDecimal("500"))
                .build();

        Mockito.when(userDetails.getUsername()).thenReturn("1");
        Mockito.when(userRepository.findByUserId(userId))
                .thenReturn(Optional.of(chargePointUser));

        pointService.chargePoint(userId, requestChargePointDto, userDetails);

        assertEquals(new BigDecimal("600"), chargePointUser.getCurrentPoint());
        Mockito.verify(userRepository).save(chargePointUser);
        Mockito.verify(pointRepository).save(Mockito.any(Point.class));
    }

    @Test
    void testChargePoint_InvalidUserId_ThrowsException() {

        Long userId = 1L;
        RequestChargePointDto requestChargePointDto = new RequestChargePointDto(
                new BigDecimal("100"), "충전 테스트"
        );

        Mockito.when(userDetails.getUsername()).thenReturn("2");

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            pointService.chargePoint(userId, requestChargePointDto, userDetails);
        });

        assertEquals("접근 권한이 없습니다.", exception.getMessage());
    }

    @Test
    void testChargePoint_UserNotFound_ThrowsException() {

        Long userId = 1L;
        RequestChargePointDto requestChargePointDto = new RequestChargePointDto(
                new BigDecimal("100"), "충전 테스트"
        );

        Mockito.when(userDetails.getUsername()).thenReturn("1");

        Mockito.when(userRepository.findByUserId(userId))
                .thenReturn(Optional.empty());

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            pointService.chargePoint(userId, requestChargePointDto, userDetails);
        });

        assertEquals("사용자를 찾을 수 없습니다.", exception.getMessage());
    }
}
