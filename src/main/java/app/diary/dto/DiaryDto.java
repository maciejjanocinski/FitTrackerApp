package app.diary.dto;

import app.nutrients.NutrientsDto;
import app.product.ProductDto;
import app.recipe.RecipeDto;
import app.workout.WorkoutDto;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Builder
public record DiaryDto(
        NutrientsDto sumNutrients,
        NutrientsDto goalNutrients,
        NutrientsDto leftNutrients,
        List<ProductDto> products,
        List<RecipeDto> recipes,
        List<WorkoutDto> workouts,
        BigDecimal kcalBurned
) {
}
