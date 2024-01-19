package app.bodymetrics;

import app.common.Gender;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record BodyMetricsDto(
        Gender gender,
        LocalDate birthDate,
        BigDecimal height,
        BigDecimal weight,
        BigDecimal neck,
        BigDecimal waist,
        BigDecimal hip
) {
}
