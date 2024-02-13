package app.diary.dto;

import app.nutrients.NutrientsDto;
import app.ingredient.IngredientDto;
import app.recipe.RecipeDto;
import app.workout.WorkoutDto;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Builder
public record DiaryDto(
        NutrientsDto sumNutrients,
        NutrientsDto goalNutrients,
        NutrientsDto leftNutrients,
        List<IngredientDto> ingredients,
        List<RecipeDto> recipes,
        List<WorkoutDto> workouts,
        BigDecimal kcalBurned,
        LocalDate date
) {
}
