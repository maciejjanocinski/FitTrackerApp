package app.workout;

import lombok.Builder;

@Builder
 record EditWorkoutDto(
        Long id,
        Double activitymin) {
}
