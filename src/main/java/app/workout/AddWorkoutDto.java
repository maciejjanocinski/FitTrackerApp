package app.workout;

import lombok.Builder;

@Builder
 record AddWorkoutDto(
        String activityid,
        Double activitymin
) {
}
