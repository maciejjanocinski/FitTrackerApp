package app.recipe;

import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
 record AddRecipeToDiaryDto(
        Long id,
        String measureLabel,
        @Positive(message = "Quantity must be positive.")
        BigDecimal quantity) {
}
