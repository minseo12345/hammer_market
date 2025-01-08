package com.hammer.hammer.point;

import com.hammer.hammer.point.dto.RequestChargePointDto;
import com.hammer.hammer.point.repository.PointRepository;
import com.hammer.hammer.point.service.PointService;
import com.hammer.hammer.user.entity.Role;
import com.hammer.hammer.user.entity.User;
import com.hammer.hammer.user.repository.RoleRepository;
import com.hammer.hammer.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

public class SynchronizedPointTest {

    @InjectMocks
    private PointService pointService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    private UserDetails userDetails;

    @BeforeEach
    public void setUpMocks() {
        MockitoAnnotations.openMocks(this);

        userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("1");

        Role role = Role.builder().roleName("ROLE_ADMIN").build();
        when(roleRepository.findByRoleName("ROLE_ADMIN")).thenReturn(role);

        User user = User.builder()
                .userId(1L)
                .currentPoint(BigDecimal.valueOf(0))
                .username("test")
                .phoneNumber("01012345678")
                .email("qkrals@naver.com")
                .password("test")
                .role(role)
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    }

    @Test
    @DisplayName("동시에 100개의 요청")
    public void requests_100_AtTheSameTime() throws InterruptedException{

        RequestChargePointDto requestChargePointDto = RequestChargePointDto.builder()
                .pointAmount(BigDecimal.valueOf(1000))
                .description("충전 테스트")
                .build();

        int threadCount = 100;

        //멀티스레드 이용 ExecutorService : 비동기를 단순하게 처리할 수 있도록 해주는 java api
        ExecutorService executorService = Executors.newFixedThreadPool(32);

        //다른 스레드에서 수행이 완료될 때 까지 대기할 수 있도록 도와주는 api - 요청이 끝날 때 까지 기다림
        CountDownLatch latch = new CountDownLatch(threadCount);

        for(int i = 0; i < threadCount; i++){
            executorService.submit(() -> {
                try{
                    pointService.chargePoint(1L, requestChargePointDto, userDetails);
                }
                finally {
                    latch.countDown();
                }
            });
        }

        latch.await();  // 다른 스레드에서 수행중인 작업이 완료될 때 까지 기다려줌

        User user = userRepository.findById(1L).orElseThrow();
        assertThat(user.getCurrentPoint()).isEqualTo(BigDecimal.valueOf(100000));
    }
}
