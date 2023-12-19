package app.workout;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record WorkoutDto(
         Long id,
         String activityId,
         String workoutType,
         String description,
         BigDecimal kcalBurned,
         Double durationInMinutes,
         int intensityLevel,
         LocalDate date
 ) {}
