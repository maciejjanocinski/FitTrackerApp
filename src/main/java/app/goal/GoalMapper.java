package app.goal;

import app.diary.Diary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface GoalMapper {

    static GoalResponseDto mapToGoalResponseDto(Diary diary) {
        return GoalResponseDto.builder()
                .kcalGoal(diary.getGoalNutrients().getKcal())
                .proteinInGram(diary.getGoalNutrients().getProteinQuantityInGrams())
                .carbohydratesInGram(diary.getGoalNutrients().getCarbohydratesQuantityInGrams())
                .fatInGram(diary.getGoalNutrients().getFatQuantityInGrams())
                .fiberInGram(diary.getGoalNutrients().getFiberQuantityInGrams())
                .build();
    };
}
