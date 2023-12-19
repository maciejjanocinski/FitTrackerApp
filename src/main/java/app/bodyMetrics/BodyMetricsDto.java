package app.bodyMetrics;

import lombok.Builder;

import java.time.LocalDate;

@Builder
public record BodyMetricsDto(
        Gender gender,
        LocalDate birthDate,
        Double height,
        Double weight,
        Double neck,
        Double waist,
        Double hip
) {
}
