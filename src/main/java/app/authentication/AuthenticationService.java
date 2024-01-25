package app.authentication;

import app.bodymetrics.BodyMetrics;
import app.diary.Diary;
import app.exceptions.InvalidInputException;
import app.roles.Role;
import app.roles.RoleRepository;
import app.roles.RoleType;
import app.stripe.StripeCustomer;
import app.user.User;
import app.user.UserRepository;
import app.exceptions.InvalidPasswordException;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static app.util.Utils.ROLE_NOT_FOUND_MESSAGE;

@Service
@AllArgsConstructor
class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final RoleRepository roleRepository;

    RegisterResponseDto register(RegisterDto registerDto) {

        Role role = roleRepository.findByName(RoleType.ROLE_USER_STANDARD.toString())
                .orElseThrow(() -> new InvalidInputException(ROLE_NOT_FOUND_MESSAGE));

        Set<Role> authorities = new HashSet<>();
        BodyMetrics bodyMetrics = new BodyMetrics();
        StripeCustomer stripeCustomer = new StripeCustomer();
        authorities.add(role);
        Diary diary = new Diary();
        User user = User.builder()
                .name(registerDto.name().trim())
                .surname(registerDto.surname().trim())
                .username(registerDto.username().trim())
                .email(registerDto.email().trim())
                .phone(registerDto.phone().trim())
                .diary(diary)
                .diariesHistory(new ArrayList<>())
                .stripeCustomer(stripeCustomer)
                .authorities(authorities)
                .bodyMetrics(bodyMetrics)
                .build();

        diary.setUser(user);
        bodyMetrics.setUser(user);
        stripeCustomer.setUser(user);
        if (checkPasswordsMatch(registerDto.password(), registerDto.confirmPassword())) {
            user.setPassword(passwordEncoder.encode(registerDto.password()));
        } else {
            throw new InvalidPasswordException("Passwords are not the same");
        }

        userRepository.save(user);

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(registerDto.username(), registerDto.password())
        );

        return RegisterResponseDto.builder()
                .jwt(tokenService.generateJwt(auth))
                .name(registerDto.name())
                .build();
    }


    String login(LoginDto loginDto) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.username(), loginDto.password())
        );

        return tokenService.generateJwt(auth);
    }


    private boolean checkPasswordsMatch(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }
}
