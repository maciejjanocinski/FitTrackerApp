package app.controllers;

import app.dto.LoginResponseDTO;
import app.dto.RegistrationDTO;
import app.models.UserEntity;
import app.services.AuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
@AllArgsConstructor
public class AuthenticationController {

    AuthenticationService authenticationService;

    @PostMapping("/register")
    public UserEntity register(@RequestBody RegistrationDTO registrationDTO) {
        return authenticationService.register(registrationDTO.getUsername(), registrationDTO.getPassword());
    }

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody RegistrationDTO body) {
        return authenticationService.login(body.getUsername(), body.getPassword());
    }

}
