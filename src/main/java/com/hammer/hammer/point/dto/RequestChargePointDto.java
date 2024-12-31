package com.hammer.hammer.point.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestChargePointDto {

    @NotNull(message = "금액은 반드시 입력해야 합니다.")
    @Positive(message = "금액은 0보다 커야 합니다.")
    BigDecimal pointAmount;

    String description;
}
