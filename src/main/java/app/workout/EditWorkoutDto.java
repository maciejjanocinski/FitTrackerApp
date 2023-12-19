package app.workout;

import lombok.Builder;

@Builder
public record EditWorkoutDto(
        Long id,
        Double activitymin) {
}
