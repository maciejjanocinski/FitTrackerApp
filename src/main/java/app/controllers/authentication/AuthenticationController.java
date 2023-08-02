package app.controllers.authentication;

import app.dto.LoginDto;
import app.dto.UserDto;
import app.models.UserEntity;
import app.services.AuthenticationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
@AllArgsConstructor
public class AuthenticationController {

    AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<UserEntity> register(@RequestBody @Valid UserDto user) {
        return authenticationService.register(user);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody @Valid LoginDto loginDto) {
        return authenticationService.login(loginDto);
    }

}
