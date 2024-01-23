package app.workout;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
 record EditWorkoutDto(
        Long id,
        BigDecimal activitymin) {
}
