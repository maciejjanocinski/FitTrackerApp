package app.authentication;

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
     ResponseEntity<User> register(@RequestBody @Valid RegisterDto user) {
        return authenticationService.register(user);
    }

    @PostMapping("/login")
     ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginDto loginDto) {
        return authenticationService.login(loginDto);
    }

}
