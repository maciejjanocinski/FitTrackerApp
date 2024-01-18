package app.recipe;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
 record AddRecipeToDiaryDto(
        Long id,
        String measureLabel,
        BigDecimal quantity) {
}
