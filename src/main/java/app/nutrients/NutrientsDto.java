package app.nutrients;

import java.math.BigDecimal;

public record NutrientsDto(
        BigDecimal kcal,
        BigDecimal proteinGrams,
        BigDecimal carbohydratesGrams,
        BigDecimal fatGrams,
        BigDecimal fiberGrams
) {}
