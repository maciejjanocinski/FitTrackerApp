package app.goal;

import lombok.Builder;

import java.math.BigDecimal;
@Builder
record GoalDto(
        BigDecimal kcal,
        int proteinPercentage,
        int carbohydratesPercentage,
        int fatPercentage) {
}
