package app.goal;

import lombok.Builder;

import java.math.BigDecimal;
@Builder
public record AddCustomGoalDto(
        BigDecimal kcal,
        int proteinPercentage,
        int carbohydratesPercentage,
        int fatPercentage) {
}
