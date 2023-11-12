package app.diary.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AddProductToDiaryDto(
        Long id,
        String measureLabel,
        BigDecimal quantity) {
}
