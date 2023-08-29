package app.goal;

import lombok.Builder;

import java.math.BigDecimal;
@Builder
record GoalResponseDto(BigDecimal kcalGoal,
                       BigDecimal proteinGoal,
                       BigDecimal carbohydratesGoal,
                       BigDecimal fatGoal,
                       BigDecimal fiberGoal
){}

