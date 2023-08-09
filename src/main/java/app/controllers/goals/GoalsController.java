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

//todo clear diary every midnight
// implement saving diary details for each past day up to 7 days
// add recipes enpoint
// add get premium option
// add bmi endpoint
// add daily check-in of measurements and/or weight
// add to env variables
// add enpoints documentation
// add tests
// add social media login with oauth2
// add custom excepion handling
// every day generate motivational quote (maybe from openAI api)
// add filters to product search (vegetarian, no-alcohol, etc.)
// add option to add custom product
// add feature to add products to favourites
// add feature to add products to blacklist
// add feature to add products to shopping list
// add rating products feature
// connect with endomondo api

}
