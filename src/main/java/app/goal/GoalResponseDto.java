package app.goal;

import java.math.BigDecimal;

record GoalResponseDto(BigDecimal kcalGoal,
                       BigDecimal proteinGoal,
                       BigDecimal carbohydratesGoal,
                       BigDecimal fatGoal,
                       BigDecimal fiberGoal
){}

