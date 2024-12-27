package com.hammer.hammer.point.service;

import com.hammer.hammer.point.dto.ResponseSelectPointDto;
import com.hammer.hammer.point.entity.Point;
import com.hammer.hammer.point.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PointService {
    private final PointRepository pointRepository;

    @Transactional(readOnly = true)
    public List<ResponseSelectPointDto> getAllPoints(Long userId) {

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
                        .balanceAmount(point.getPointAmount())
                        .build())
                        .collect(Collectors.toList());

    }
}
