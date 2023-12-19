package app.workout;

import lombok.Builder;

@Builder
public record AddWorkoutDto(
        String activityid,
        Double activitymin
) {
}
