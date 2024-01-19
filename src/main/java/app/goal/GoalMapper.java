package app.goal;

import app.diary.Diary;
import org.mapstruct.Mapper;

import java.math.RoundingMode;

@Mapper(componentModel = "spring")
public interface GoalMapper {

    static GoalResponseDto mapToGoalResponseDto(Diary diary) {
        return GoalResponseDto.builder()
                .kcalGoal(diary.getGoalNutrients().getKcal().setScale(1, RoundingMode.HALF_UP))
                .proteinInGram(diary.getGoalNutrients().getProteinGrams().setScale(1, RoundingMode.HALF_UP))
                .carbohydratesInGram(diary.getGoalNutrients().getCarbohydratesGrams().setScale(1, RoundingMode.HALF_UP))
                .fatInGram(diary.getGoalNutrients().getFatGrams().setScale(1, RoundingMode.HALF_UP))
                .fiberInGram(diary.getGoalNutrients().getFiberGrams().setScale(1, RoundingMode.HALF_UP))
                .build();
    };
}
