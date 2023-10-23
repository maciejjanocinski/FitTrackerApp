package app.goal;

import app.diary.Diary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface GoalMapper {

    @Mapping(target = "kcalGoal", source = "goalKcal")
    @Mapping(target = "proteinInGram", source = "goalProtein")
    @Mapping(target = "carbohydratesInGram", source = "goalCarbohydrates")
    @Mapping(target = "fatInGram", source = "goalFat")
    @Mapping(target = "fiberInGram", source = "goalFiber")
    GoalResponseDto mapToGoalResponseDto(Diary diary);
}
