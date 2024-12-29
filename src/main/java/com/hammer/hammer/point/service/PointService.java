package com.hammer.hammer.point.service;

import com.hammer.hammer.point.dto.RequestChargePointDto;
import com.hammer.hammer.point.dto.ResponseCurrentPointDto;
import com.hammer.hammer.point.dto.ResponseSelectPointDto;
import com.hammer.hammer.point.entity.Point;
import com.hammer.hammer.point.entity.PointStatus;
import com.hammer.hammer.point.repository.PointRepository;
import com.hammer.hammer.user.entity.User;
import com.hammer.hammer.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PointService {
    private final PointRepository pointRepository;
    private final UserRepository userRepository;

    /**
     *  포인트 조회
     */
    @Transactional(readOnly = true)
    public List<ResponseSelectPointDto> getAllPoints(Long userId, UserDetails userDetails) {

        if (!userId.toString().equals(userDetails.getUsername())) {
            throw new IllegalStateException("접근 권한이 없습니다.");
        }

        List<Point> selectPoint = pointRepository.findByUser_UserId(userId).orElseThrow(
                ()->new IllegalStateException("입출금 내역을 찾을 수 없습니다.")
        );


        return selectPoint.stream()
                .map(point -> ResponseSelectPointDto.builder()
                        .pointType(point.getPointType())
                        .pointAmount(point.getPointAmount())
                        .currentPoint(selectPoint.get(0).getUser().getCurrentPoint())
                        .description(point.getDescription())
                        .createAt(point.getCreateDate())
                        .balanceAmount(point.getBalanceAmount())
                        .build())
                        .collect(Collectors.toList());

    }

    /**
     *  point 충전
     */
    @Transactional
    public void chargePoint(Long userId, RequestChargePointDto requestChargePointDto, UserDetails userDetails){

        if (!userId.toString().equals(userDetails.getUsername())) {
            throw new IllegalStateException("접근 권한이 없습니다.");
        }

        User chargePointUser = userRepository.findByUserId(userId).orElseThrow(
                () -> new IllegalStateException("사용자를 찾을 수 없습니다.")
        );

        BigDecimal currentPoint = chargePointUser.getCurrentPoint();
        BigDecimal updatePoint = currentPoint.add(requestChargePointDto.getPointAmount());

        chargePointUser.chargePoint(updatePoint);
        userRepository.save(chargePointUser);

        Point point = Point.builder()
                .user(chargePointUser)
                .pointType(PointStatus.D)
                .createDate(LocalDateTime.now())
                .pointAmount(requestChargePointDto.getPointAmount())
                .description(requestChargePointDto.getDescription())
                .balanceAmount(updatePoint)
                .build();

        pointRepository.save(point);
    }

    /**
     *  currentPoint 조회
     */
    @Transactional(readOnly = true)
    public ResponseCurrentPointDto currentPointByUser(Long userId, UserDetails userDetails){

        if (!userId.toString().equals(userDetails.getUsername())) {
            throw new IllegalStateException("접근 권한이 없습니다.");
        }

        User findCurrentPointByUser = userRepository.findByUserId(userId).orElseThrow(
                () -> new IllegalStateException("사용자를 찾을 수 없습니다.")
        );

        return ResponseCurrentPointDto
                .builder()
                .currentPoint(findCurrentPointByUser.getCurrentPoint())
                .build();
    }

    @Transactional
    public void currencyPoint(Long userId, RequestChargePointDto requestChargePointDto, UserDetails userDetails){

        if (!userId.toString().equals(userDetails.getUsername())) {
            throw new IllegalStateException("접근 권한이 없습니다.");
        }

        User currencyUser = userRepository.findByUserId(userId).orElseThrow(
                () -> new IllegalStateException("사용자를 찾을 수 없습니다.")
        );

        BigDecimal currentPoint = currencyUser.getCurrentPoint();
        BigDecimal updatePoint = currentPoint.subtract(requestChargePointDto.getPointAmount());

        if (updatePoint.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalStateException("포인트가 부족합니다.");
        }

        currencyUser.chargePoint(updatePoint);
        userRepository.save(currencyUser);

        Point currencyPoint = Point.builder()
                .pointType(PointStatus.C)
                .createDate(LocalDateTime.now())
                .pointAmount(requestChargePointDto.getPointAmount())
                .balanceAmount(updatePoint)
                .description(requestChargePointDto.getDescription())
                .user(currencyUser)
                .build();

        pointRepository.save(currencyPoint);
    }
}
