package app.recipe;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record AddRecipeToDiaryDto(
        Long id,
        String measureLabel,
        BigDecimal quantity) {
}
