package app.authentication;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
@RequiredArgsConstructor
class AuthenticationController {

   private final AuthenticationService authenticationService;

    @PostMapping("/register")
    RegisterResponseDto register(@RequestBody @Valid RegisterDto registerDto) {
        return authenticationService.register(registerDto);
    }

    @PostMapping("/login")
     String login(@RequestBody @Valid LoginDto loginDto) {
        return authenticationService.login(loginDto);
    }
}
