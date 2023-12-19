package app.diary;

import app.diary.dto.DiaryDto;
import org.mapstruct.Mapper;

import java.math.RoundingMode;

import static app.product.ProductMapper.mapToProductDtoList;
import static app.recipe.RecipeMapper.mapRecipeDtoToRecipeDtoList;
import static app.workout.WorkoutMapper.staticMapWorkoutListToWorkoutListDto;

@Mapper(componentModel = "spring")
interface DiaryMapper {

    static DiaryDto mapDiaryToDiaryDto(Diary diary) {
        return DiaryDto.builder()
                .sumKcal(diary.getNutrientsSum().getKcal().setScale(1, RoundingMode.HALF_UP))
                .sumProtein(diary.getNutrientsSum().getProteinQuantityInGrams().setScale(1, RoundingMode.HALF_UP))
                .sumCarbohydrates(diary.getNutrientsSum().getCarbohydratesQuantityInGrams().setScale(1, RoundingMode.HALF_UP))
                .sumFat(diary.getNutrientsSum().getFatQuantityInGrams().setScale(1, RoundingMode.HALF_UP))
                .sumFiber(diary.getNutrientsSum().getFiberQuantityInGrams().setScale(1, RoundingMode.HALF_UP))
                .goalKcal(diary.getGoalNutrients().getKcal().setScale(1, RoundingMode.HALF_UP))
                .goalProtein(diary.getGoalNutrients().getProteinQuantityInGrams().setScale(1, RoundingMode.HALF_UP))
                .goalFat(diary.getGoalNutrients().getFatQuantityInGrams().setScale(1, RoundingMode.HALF_UP))
                .goalCarbohydrates(diary.getGoalNutrients().getCarbohydratesQuantityInGrams().setScale(1, RoundingMode.HALF_UP))
                .goalFiber(diary.getGoalNutrients().getFiberQuantityInGrams().setScale(1, RoundingMode.HALF_UP))
                .leftKcal(diary.getNutrientsLeft().getKcal().setScale(1, RoundingMode.HALF_UP))
                .leftProtein(diary.getNutrientsLeft().getProteinQuantityInGrams().setScale(1, RoundingMode.HALF_UP))
                .leftFat(diary.getNutrientsLeft().getFatQuantityInGrams().setScale(1, RoundingMode.HALF_UP))
                .leftCarbohydrates(diary.getNutrientsLeft().getCarbohydratesQuantityInGrams().setScale(1, RoundingMode.HALF_UP))
                .leftFiber(diary.getNutrientsLeft().getFiberQuantityInGrams().setScale(1, RoundingMode.HALF_UP))
                .productsInDiary(mapToProductDtoList(diary.getProducts()))
                .favouriteRecipes(mapRecipeDtoToRecipeDtoList(diary.getRecipes()))
                .workouts(staticMapWorkoutListToWorkoutListDto(diary.getWorkouts()))
                .build();
    }
}
