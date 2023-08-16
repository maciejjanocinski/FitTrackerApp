package app.user;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;


@AllArgsConstructor

@RestController
@RequestMapping("/user")
class UserController {

    UserService userService;

    @GetMapping("/")
    private ResponseEntity<User> getUser(@CurrentSecurityContext(expression = "authentication")
                                         Authentication authentication) {
        return userService.getUser(authentication);
    }

    @PatchMapping("/")
    private ResponseEntity<String> updateProfile(@CurrentSecurityContext(expression = "authentication")
                                                 Authentication authentication,
                                                 @RequestBody UpdateProfileInfoDto updates) {
        return userService.updateProfile(authentication, updates);
    }

    @PatchMapping("/updatePassword")
    private ResponseEntity<String> updatePassword(@CurrentSecurityContext(expression = "authentication")
                                                  Authentication authentication,
                                                  @RequestBody UpdatePasswordDto password){
        return userService.updatePassword(authentication,password);
    }

    @DeleteMapping("/")
    private ResponseEntity<String> deleteProfile(@CurrentSecurityContext(expression = "authentication")
                                                 Authentication authentication,
                                                 @RequestBody DeleteUserDto deleteUserDto) {
        return userService.deleteProfile(authentication, deleteUserDto);
    }

}
