package app.authentication;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
@AllArgsConstructor
class AuthenticationController {

    AuthenticationService authenticationService;

    @PostMapping("/register")
     RegisterDto register(@RequestBody @Valid RegisterDto user) {
        return authenticationService.register(user);
    }

    @PostMapping("/login")
     LoginResponseDto login(@RequestBody @Valid LoginDto loginDto) {
        return authenticationService.login(loginDto);
    }
}
