package app.workout;

import app.activity.Activity;
import app.diary.Diary;
import app.user.User;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;


@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Workout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String activityId;
    private String workoutType;
    private String description;
    private BigDecimal kcalBurned;
    private BigDecimal durationInMinutes;
    private int intensityLevel;
    private LocalDate date;

    @ManyToOne
    private Diary diary;


    static Workout generateNewCustomWorkout(AddCustomWorkoutDto addCustomWorkoutDto) {
       return Workout.builder()
                .workoutType(addCustomWorkoutDto.workoutType())
                .description(addCustomWorkoutDto.description())
                .kcalBurned(addCustomWorkoutDto.kcalBurned())
                .durationInMinutes(addCustomWorkoutDto.durationInMinutes())
                .date(LocalDate.now())
                .build();
    }

    static Workout generateNewWorkout(AddWorkoutDto addWorkoutDto, Activity activity, BigDecimal burnedCalorie) {
        return Workout.builder()
                .activityId(activity.getId())
                .workoutType(activity.getActivity())
                .description(activity.getDescription())
                .kcalBurned(burnedCalorie)
                .durationInMinutes(addWorkoutDto.activitymin())
                .intensityLevel(activity.getIntensityLevel())
                .date(LocalDate.now())
                .build();
    }



















}
