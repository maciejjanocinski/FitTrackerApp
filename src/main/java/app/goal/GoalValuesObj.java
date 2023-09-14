package app.goal;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
record GoalValuesObj(
        BigDecimal kcal,
        BigDecimal protein,
        BigDecimal carbohydrates,
        BigDecimal fat,
        BigDecimal fiber) {
}