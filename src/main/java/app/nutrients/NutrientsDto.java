package app.nutrients;

import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record NutrientsDto(
        @Positive(message = "Quantity must be positive.")
        BigDecimal kcal,
        @Positive(message = "Quantity must be positive.")
        BigDecimal proteinGrams,
        @Positive(message = "Quantity must be positive.")
        BigDecimal carbohydratesGrams,
        @Positive(message = "Quantity must be positive.")
        BigDecimal fatGrams,
        @Positive(message = "Quantity must be positive.")
        BigDecimal fiberGrams
) {}
