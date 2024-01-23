package app.goal;

import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.math.BigDecimal;
@Builder
public record AddCustomGoalDto(
        @Positive(message = "Calories must be positive.")
        BigDecimal kcal,
        @Positive(message = "Quantity must be positive.")
        int proteinPercentage,
        @Positive(message = "Quantity must be positive.")
        int carbohydratesPercentage,
        @Positive(message = "Quantity must be positive.")
        int fatPercentage) {
}
