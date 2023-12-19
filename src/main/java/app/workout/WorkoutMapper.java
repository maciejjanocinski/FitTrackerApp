package app.workout;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface WorkoutMapper {

    static List<WorkoutDto> staticMapWorkoutListToWorkoutListDto(List<Workout> workouts) {
        return workouts.stream()
                .map(WorkoutMapper::staticMapWorkoutToWorkoutDto)
                .toList();
    }

    static WorkoutDto staticMapWorkoutToWorkoutDto(Workout workout) {
        return WorkoutDto.builder()
                .id(workout.getId())
                .activityId(workout.getActivityId())
                .workoutType(workout.getWorkoutType())
                .description(workout.getDescription())
                .kcalBurned(workout.getKcalBurned())
                .durationInMinutes(workout.getDurationInMinutes())
                .intensityLevel(workout.getIntensityLevel())
                .date(workout.getDate())
                .build();
    }
}
