package app.workout;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WorkoutMapper {

    @Mapping(target = "user", ignore = true)
    List<WorkoutDto> mapWorkoutListToWorkoutListDto(List<Workout> workouts);

    WorkoutDto mapWorkoutToWorkoutDto(Workout workout);

}
