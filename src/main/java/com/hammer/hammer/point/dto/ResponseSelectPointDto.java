package com.hammer.hammer.point.dto;

import com.hammer.hammer.point.entity.PointStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseSelectPointDto {

    private BigDecimal pointAmount;
    private LocalDateTime createAt;
    private PointStatus pointType;
    private String description;
    private BigDecimal currentPoint;
    private BigDecimal balanceAmount;
}
