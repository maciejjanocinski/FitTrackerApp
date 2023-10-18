package app.goal;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record GoalValues(
        BigDecimal kcal,
        BigDecimal protein,
        BigDecimal carbohydrates,
        BigDecimal fat,
        BigDecimal fiber) {
}