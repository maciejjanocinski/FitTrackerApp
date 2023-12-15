package app.workout;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@AllArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/workout")
public class WorkoutController {

    private final WorkoutService workoutService;

    @GetMapping("/")
    List<WorkoutDto> getWorkouts(@CurrentSecurityContext(expression = "authentication")
                                 Authentication authentication) {
        return workoutService.getWorkouts(authentication);
    }

    @PostMapping("/")
    WorkoutDto addCustomWorkout(@CurrentSecurityContext(expression = "authentication")
                                 Authentication authentication,
                                      AddCustomWorkoutDto addCustomWorkoutDto
                                ) {
        return workoutService.addCustomWorkout(authentication, addCustomWorkoutDto);
    }

    @GetMapping("/api")
    WorkoutApiResponse getWorkoutApiResponse() {
        return workoutService.getWorkoutApiResponse();
    }

}











