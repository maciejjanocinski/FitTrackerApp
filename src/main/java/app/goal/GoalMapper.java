package app.goal;

import app.diary.Diary;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface GoalMapper {

    GoalMapper INSTANCE = Mappers.getMapper(GoalMapper.class);
    @Mapping(target = "kcalGoal", source = "goalKcal")
    @Mapping(target = "proteinGoal", source = "goalProtein")
    @Mapping(target = "carbohydratesGoal", source = "goalCarbohydrates")
    @Mapping(target = "fatGoal", source = "goalFat")
    @Mapping(target = "fiberGoal", source = "goalFiber")
    GoalResponseDto mapToGoalResponseDto(Diary diary);
}
