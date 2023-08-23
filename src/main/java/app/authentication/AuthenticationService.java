package app.authentication;

import app.diary.Diary;
import app.user.UserDto;
import app.user.User;
import app.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@Transactional
@AllArgsConstructor
class AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    ResponseEntity<RegisterDto> register(RegisterDto body) {
        User user = new User();

        user.setName(body.name());
        user.setSurname(body.surname());
        user.setGender(body.gender());
        user.setEmail(body.email());
        user.setPhone(body.phone());

        user.setUsername(body.username());
        user.setPassword(passwordEncoder.encode(body.password()));

        Role userStandardRole = roleRepository.findByAuthority("USER_STANDARD")
                .orElseThrow(() -> new RuntimeException("User standard role not found."));
        Set<Role> authorities = new HashSet<>();
        authorities.add(userStandardRole);
        user.setAuthorities(authorities);

        Diary diary = new Diary();
        user.setDiary(diary);

        userRepository.save(user);

        return ResponseEntity.ok(body);
    }


    ResponseEntity<LoginResponseDto> login(LoginDto loginDto) {

            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.username(), loginDto.password())
            );
            String token = tokenService.generateJwt(auth);

            LoginResponseDto responseDTO = new LoginResponseDto(loginDto.username(), token);
            return ResponseEntity.ok(responseDTO);

    }


}
