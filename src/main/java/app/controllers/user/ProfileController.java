package app.controllers.user;

import app.dto.PasswordDTO;
import app.models.UserEntity;
import app.repository.UserRepository;
import app.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@AllArgsConstructor
@Controller
@RequestMapping("/user/profile")
public class ProfileController {

    UserService userService;


    @GetMapping("/")
    public ResponseEntity<UserEntity> getProfile(@CurrentSecurityContext(expression = "authentication")
                                                 Authentication authentication) {
        return userService.getProfile(authentication);
    }

    @DeleteMapping("/")
    public ResponseEntity<String> deleteProfile(@CurrentSecurityContext(expression = "authentication")
                                                Authentication authentication, @RequestBody Map<String, String> password) {
        return userService.deleteProfile(authentication, password);
    }

    @PatchMapping("/")
    public ResponseEntity<String> updateProfile(@CurrentSecurityContext(expression = "authentication")
                                                Authentication authentication,
                                                @RequestBody(required = false) Map<String, String> updates,
                                                @RequestParam(required = false) PasswordDTO password) {
        if (password != null) {
            return userService.updatePassword(authentication, password);
        }
        return userService.updateProfile(authentication, updates);
    }
}
