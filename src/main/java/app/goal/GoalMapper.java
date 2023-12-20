package app.goal;

import app.diary.Diary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.math.RoundingMode;

@Mapper(componentModel = "spring")
public interface GoalMapper {

    static GoalResponseDto mapToGoalResponseDto(Diary diary) {
        return GoalResponseDto.builder()
                .kcalGoal(diary.getGoalNutrients().getKcal().setScale(1, RoundingMode.HALF_UP))
                .proteinInGram(diary.getGoalNutrients().getProteinQuantityInGrams().setScale(1, RoundingMode.HALF_UP))
                .carbohydratesInGram(diary.getGoalNutrients().getCarbohydratesQuantityInGrams().setScale(1, RoundingMode.HALF_UP))
                .fatInGram(diary.getGoalNutrients().getFatQuantityInGrams().setScale(1, RoundingMode.HALF_UP))
                .fiberInGram(diary.getGoalNutrients().getFiberQuantityInGrams().setScale(1, RoundingMode.HALF_UP))
                .build();
    };
}
