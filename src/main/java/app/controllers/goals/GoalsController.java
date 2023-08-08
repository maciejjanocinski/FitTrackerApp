package app.controllers.goals;

import app.dto.GoalsDto;
import app.models.Goals;
import app.services.GoalsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@Controller
@RequestMapping("/goals")
public class GoalsController {

    private final GoalsService goalsService;

    @GetMapping("/")
    public ResponseEntity<Goals> getGoals(@CurrentSecurityContext(expression = "authentication")
                                          Authentication authentication) {
    return goalsService.getGoals(authentication);
    }

    @PostMapping("/")
    public ResponseEntity<Goals> setGoals(@CurrentSecurityContext(expression = "authentication")
                                          Authentication authentication,
                                          @RequestBody GoalsDto goalsDto) {
        return goalsService.setGoals(authentication, goalsDto);
    }


}
