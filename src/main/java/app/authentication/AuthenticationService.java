package app.authentication;

import app.diary.Diary;
import app.user.User;
import app.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

import static app.util.UtilityClass.roleNotFoundMessage;

@Service
@AllArgsConstructor
class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final RoleRepository roleRepository;

    RegisterDto register(RegisterDto registerDto) {

        Role role = roleRepository.findByName(Role.roleType.ROLE_USER_STANDARD.toString())
                .orElseThrow(() -> new RuntimeException(roleNotFoundMessage));

        Set<Role> authorities = new HashSet<>();
        authorities.add(role);
        Diary diary = new Diary();
        User user = User.builder()
                .name(registerDto.name().trim())
                .surname(registerDto.surname().trim())
                .username(registerDto.username().trim())
                .password(passwordEncoder.encode(registerDto.password().trim()))
                .gender(User.setGenderFromString(registerDto.gender()))
                .email(registerDto.email().trim())
                .phone(registerDto.phone().trim())
                .diary(diary)
                .authorities(authorities)
                .build();

        userRepository.save(user);
        return registerDto;
    }


    String login(LoginDto loginDto) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.username(), loginDto.password())
        );

        return tokenService.generateJwt(auth);
    }


}
