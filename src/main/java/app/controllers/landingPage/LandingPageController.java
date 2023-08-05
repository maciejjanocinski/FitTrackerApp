package app.controllers.landingPage;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LandingPageController {

    @GetMapping("/")
    public ResponseEntity<String> landingPage() {
        return ResponseEntity.ok("Landing page of FitTrackerApp");
    }
}
