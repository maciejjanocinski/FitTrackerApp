package app.goal;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record GoalResponseDto(
        BigDecimal kcalGoal,
        BigDecimal proteinInGram,
        BigDecimal carbohydratesInGram,
        BigDecimal fatInGram,
        BigDecimal fiberInGram
) {
}

