package app.workout;

import app.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.util.Date;


@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Workout {
    @Id
    private Long id;
    private String workoutType;
    private String description;
    private Double kcalBurned;
    private Duration duration;
    private int intensityLevel;
    private Date date;

    @ManyToOne
    private User user;


    static Workout generateNewCustomWorkout(AddCustomWorkoutDto addCustomWorkoutDto) {
       return Workout.builder()
                .workoutType(addCustomWorkoutDto.workoutType())
                .description(addCustomWorkoutDto.description())
                .kcalBurned(addCustomWorkoutDto.kcalBurned())
                .duration(addCustomWorkoutDto.duration())
                .date(new Date())
                .build();
    }





















}
