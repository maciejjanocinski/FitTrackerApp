//package app.controllers.goals;
//
//import app.models.GoalsEntity;
//import app.services.GoalsService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.annotation.CurrentSecurityContext;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.RequestMapping;
//
//@RequiredArgsConstructor
//@Controller
//@RequestMapping("/goals")
//public class GoalsController {
//
//    private final GoalsService goalsService;
//
//    @RequestMapping("/")
//    public ResponseEntity<GoalsEntity> getGoals(@CurrentSecurityContext(expression = "authentication")
//                                          Authentication authentication) {
//    return goalsService.getGoals(authentication);
//    }
//}
