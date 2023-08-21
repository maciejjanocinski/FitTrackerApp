package app.user;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;


@AllArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/user")
class UserController {

    private final UserService userService;

    @GetMapping("/")
     ResponseEntity<UserDto> getUser(@CurrentSecurityContext(expression = "authentication")
                                         Authentication authentication) {
        return userService.getUser(authentication);
    }

    @PatchMapping("/")
     ResponseEntity<String> updateProfile(@CurrentSecurityContext(expression = "authentication")
                                                 Authentication authentication,
                                                 @RequestBody @Valid UpdateProfileInfoDto updates) {
        return userService.updateProfile(authentication, updates);
    }

    @PatchMapping("/password")
     ResponseEntity<String> updatePassword(@CurrentSecurityContext(expression = "authentication")
                                                  Authentication authentication,
                                                  @RequestBody UpdatePasswordDto password){
        return userService.updatePassword(authentication,password);
    }

    @DeleteMapping("/")
     ResponseEntity<String> deleteProfile(@CurrentSecurityContext(expression = "authentication")
                                                 Authentication authentication,
                                                 @RequestBody DeleteUserDto deleteUserDto) {
        return userService.deleteProfile(authentication, deleteUserDto);
    }

}
