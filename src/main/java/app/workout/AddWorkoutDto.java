package app.workout;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
 record AddWorkoutDto(
        String activityid,
        BigDecimal activitymin
) {
}
