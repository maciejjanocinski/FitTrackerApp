package app.goal;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/goal")
public class GoalController {

    private final GoalService goalsService;

    @GetMapping("/")
     GoalResponseDto getGoals(@CurrentSecurityContext(expression = "authentication")
                                                        Authentication authentication) {
        return goalsService.getGoal(authentication);
    }

    @PostMapping("/")
     GoalResponseDto setGoals(@CurrentSecurityContext(expression = "authentication")
                                                        Authentication authentication,
                                                        @RequestBody GoalDto goalsDto) {
        return goalsService.setGoal(authentication, goalsDto);
    }
}
