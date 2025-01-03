package com.hammer.hammer.point.service;

import com.hammer.hammer.point.dto.RequestChargePointDto;
import com.hammer.hammer.point.dto.ResponseCurrentPointDto;
import com.hammer.hammer.point.dto.ResponseSelectPointDto;
import com.hammer.hammer.point.entity.Point;
import com.hammer.hammer.point.entity.PointStatus;
import com.hammer.hammer.point.repository.PointRepository;
import com.hammer.hammer.transaction.entity.Transaction;
import com.hammer.hammer.user.entity.User;
import com.hammer.hammer.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
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
    public Page<ResponseSelectPointDto> getAllPoints(Long userId, PointStatus type, UserDetails userDetails, int page, int size) {

        if (!userId.toString().equals(userDetails.getUsername())) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<Point> selectPointPage;

        if (type == null || "ALL".equalsIgnoreCase(String.valueOf(type))) {
            selectPointPage = pointRepository.findByUser_UserIdOrderByCreateDateDesc(userId, pageable);
        } else {
            selectPointPage = pointRepository.findByUser_UserIdAndPointTypeOrderByCreateDateDesc(userId, type, pageable);
        }

        DecimalFormat decimalFormat = new DecimalFormat("#,###");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        return selectPointPage.map(point -> {
            String formattedCreateDate = point.getCreateDate() != null
                    ? point.getCreateDate().format(formatter)
                    : null;

            return ResponseSelectPointDto.builder()
                    .pointType(point.getPointType())
                    .pointAmount(decimalFormat.format(point.getPointAmount()))
                    .description(point.getDescription())
                    .createAt(formattedCreateDate)
                    .balanceAmount(decimalFormat.format(point.getBalanceAmount()))
                    .build();
        });
    }


    /**
     *  point 충전
     */
    public synchronized void chargePoint(
            Long userId,
            RequestChargePointDto requestChargePointDto,
            UserDetails userDetails){

        if (!userId.toString().equals(userDetails.getUsername())) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }

        User chargePointUser = userRepository.findById(userId).orElseThrow(
                () -> new IllegalStateException("사용자를 찾을 수 없습니다.")
        );

        BigDecimal currentPoint = chargePointUser.getCurrentPoint();
        BigDecimal updatePoint = currentPoint.add(requestChargePointDto.getPointAmount());

        chargePointUser.chargePoint(updatePoint);
        userRepository.saveAndFlush(chargePointUser);

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
            throw new AccessDeniedException("접근 권한이 없습니다.");
        }

        User findCurrentPointByUser = userRepository.findByUserId(userId).orElseThrow(
                () -> new IllegalStateException("사용자를 찾을 수 없습니다.")
        );

        return ResponseCurrentPointDto
                .builder()
                .currentPoint(findCurrentPointByUser.getCurrentPoint())
                .build();
    }

    /**
     * 포인트 환전
     */
    public synchronized void currencyPoint(Long userId,
                              RequestChargePointDto requestChargePointDto,
                              UserDetails userDetails){

        if (!userId.toString().equals(userDetails.getUsername())) {
            throw new AccessDeniedException("접근 권한이 없습니다.");
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
        userRepository.saveAndFlush(currencyUser);

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

    public synchronized void processTransactionPoints(Transaction transaction) {
        User seller = transaction.getSeller();
        User buyer = transaction.getBuyer();
        BigDecimal finalPrice = transaction.getFinalPrice();

        // 판매자 포인트 증가
        BigDecimal sellerCurrentPoint = seller.getCurrentPoint();
        BigDecimal sellerUpdatedPoint = sellerCurrentPoint.add(finalPrice);
        seller.chargePoint(sellerUpdatedPoint);

        // 판매자 포인트 기록
        Point sellerPoint = Point.builder()
                .user(seller)
                .pointType(PointStatus.D)
                .createDate(LocalDateTime.now())
                .pointAmount(finalPrice)
                .description("상품 판매에 의한 포인트 증가")
                .balanceAmount(sellerUpdatedPoint)
                .build();
        pointRepository.save(sellerPoint);

        // 구매자 포인트 감소
        BigDecimal buyerCurrentPoint = buyer.getCurrentPoint();
        BigDecimal buyerUpdatedPoint = buyerCurrentPoint.subtract(finalPrice);
        buyer.chargePoint(buyerUpdatedPoint);

        // 구매자 포인트 기록
        Point buyerPoint = Point.builder()
                .user(buyer)
                .pointType(PointStatus.C)
                .createDate(LocalDateTime.now())
                .pointAmount(finalPrice)
                .description("상품 구매에 의한 포인트 감소")
                .balanceAmount(buyerUpdatedPoint)
                .build();
        pointRepository.save(buyerPoint);

        userRepository.save(seller);
        userRepository.save(buyer);
    }
}
