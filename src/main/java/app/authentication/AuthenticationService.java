package app.authentication;

import app.authentication.RoleEnum.roles;
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

@Service
@AllArgsConstructor
class AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    RegisterDto register(RegisterDto registerDto) {

        Role userStandardRole = roleRepository.findByAuthority(roles.USER_STANDARD.toString())
                .orElseThrow(() -> new RuntimeException("User standard role not found."));
        Set<Role> authorities = new HashSet<>();
        authorities.add(userStandardRole);
        Diary diary = new Diary();
        User user = User.builder()
                .name(registerDto.name().trim())
                .surname(registerDto.surname().trim())
                .username(registerDto.username().trim())
                .password(passwordEncoder.encode(registerDto.password().trim()))
                .gender(User.validateGender(registerDto.gender()))
                .email(registerDto.email().trim())
                .phone(registerDto.phone().trim())
                .diary(diary)
                .authorities(authorities)
                .build();

        userRepository.save(user);
        return registerDto;
    }


    LoginResponseDto login(LoginDto loginDto) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.username(), loginDto.password())
        );
        String token = tokenService.generateJwt(auth);

        return new LoginResponseDto(loginDto.username(), token);

    }


}
