package app.goal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@RequiredArgsConstructor
@Controller
@RequestMapping("/goals")
public class GoalController {

    private final GoalService goalsService;

    @GetMapping("/")
    public ResponseEntity<Map<String, Double>> getGoals(@CurrentSecurityContext(expression = "authentication")
                                                        Authentication authentication) {
        return goalsService.getGoal(authentication);
    }

    @PostMapping("/")
    public ResponseEntity<Map<String, Double>> setGoals(@CurrentSecurityContext(expression = "authentication")
                                                        Authentication authentication,
                                                        @RequestBody GoalDto goalsDto) {
        return goalsService.setGoal(authentication, goalsDto);
    }
}
