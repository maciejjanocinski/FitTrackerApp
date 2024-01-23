package app.workout;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface WorkoutMapper {

    WorkoutMapper INSTANCE = Mappers.getMapper(WorkoutMapper.class);
     WorkoutDto mapToDto(Workout workout);
     List<WorkoutDto> mapToDto(List<Workout> workouts);
}
