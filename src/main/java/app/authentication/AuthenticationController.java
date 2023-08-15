package app.authentication;

import app.user.UserDto;
import app.user.User;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
@AllArgsConstructor
class AuthenticationController {

    AuthenticationService authenticationService;

    @PostMapping("/register")
    private ResponseEntity<User> register(@RequestBody @Valid UserDto user) {
        return authenticationService.register(user);
    }

    @PostMapping("/login")
    private ResponseEntity<Object> login(@RequestBody @Valid LoginDto loginDto) {
        return authenticationService.login(loginDto);
    }

}
