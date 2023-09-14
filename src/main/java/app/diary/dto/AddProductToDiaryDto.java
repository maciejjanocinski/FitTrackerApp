package app.diary.dto;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AddProductToDiaryDto(
        String foodId,
        String name,
        String measureLabel,
        BigDecimal quantity) {
}
