package app.goal;

import java.math.BigDecimal;

record GoalDto(
        BigDecimal kcal,
        int proteinPercentage,
        int carbohydratesPercentage,
        int fatPercentage) {
}
