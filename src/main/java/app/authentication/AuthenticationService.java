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
import app.authentication.RoleEnum.roles;
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
        User user = new User();

        user.setName(registerDto.name().trim());
        user.setSurname(registerDto.surname().trim());
        user.setGender(registerDto.gender().trim());
        user.setEmail(registerDto.email().trim());
        user.setPhone(registerDto.phone().trim());

        user.setUsername(registerDto.username().trim());
        user.setPassword(passwordEncoder.encode(registerDto.password().trim()));

        Role userStandardRole = roleRepository.findByAuthority(roles.USER_STANDARD.toString())
                .orElseThrow(() -> new RuntimeException("User standard role not found."));
        Set<Role> authorities = new HashSet<>();
        authorities.add(userStandardRole);
        user.setAuthorities(authorities);

        Diary diary = new Diary();
        user.setDiary(diary);

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
