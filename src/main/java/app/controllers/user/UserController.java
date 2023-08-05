package app.controllers.user;

import app.dto.DeleteProfileDto;
import app.dto.UpdateProfileInfoDto;
import app.dto.updatePasswordDto;
import app.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@AllArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

   UserService userService;

    @GetMapping("/")
    public ResponseEntity<?> getProfile(@CurrentSecurityContext(expression = "authentication")
                                        Authentication authentication) {
        return userService.getProfile(authentication);
    }

    @PatchMapping("/")
    public ResponseEntity<String> updateProfile(@CurrentSecurityContext(expression = "authentication")
                                                Authentication authentication,
                                                @RequestBody UpdateProfileInfoDto updates) {
        return userService.updateProfile(authentication, updates);
    }

    @PatchMapping("/updatePassword")
    public ResponseEntity<String> updatePassword(@CurrentSecurityContext(expression = "authentication")
                                                     Authentication authentication,
                                                 @RequestBody updatePasswordDto password){
        return userService.updatePassword(authentication,password);
    }

    @DeleteMapping("/")
    public ResponseEntity<String> deleteProfile(@CurrentSecurityContext(expression = "authentication")
                                                Authentication authentication,
                                                @RequestBody DeleteProfileDto deleteProfileDto) {
        return userService.deleteProfile(authentication, deleteProfileDto);
    }

}
