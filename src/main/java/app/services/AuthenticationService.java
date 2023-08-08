package app.services;

import app.dto.LoginDto;
import app.dto.LoginResponseDto;
import app.dto.UserDto;
import app.models.*;
import app.repositories.*;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
@Transactional
@AllArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @Transactional
    public ResponseEntity<User> register(UserDto body) {
        User user = new User();

        user.setName(body.getName());
        user.setSurname(body.getSurname());
        user.setGender(body.getGender());
        user.setEmail(body.getEmail());
        user.setPhone(body.getPhone());

        user.setUsername(body.getUsername());
        user.setPassword(passwordEncoder.encode(body.getPassword()));

        Role userStandardRole = roleRepository.findByAuthority("USER_STANDARD").get();
        Set<Role> authorities = new HashSet<>();
        authorities.add(userStandardRole);
        user.setAuthorities(authorities);

        Diary diary = new Diary();
        NutrientsSum sumNutrientsEntity = new NutrientsSum();
        Goals goalsEntity = new Goals(0,0,0,0,0);
        NutrientsLeftToReachTodayGoals nutrientsLeftToReachTodayGoals = new NutrientsLeftToReachTodayGoals();
        diary.setProducts(new ArrayList<>());
        diary.setNutrientsSum(sumNutrientsEntity);
        diary.setGoals(goalsEntity);
        diary.setNutrientsLeftToReachTodayGoals(nutrientsLeftToReachTodayGoals);
        user.setDiary(diary);

        userRepository.save(user);

        return ResponseEntity.ok(user);
    }


    public ResponseEntity<Object> login(LoginDto loginDto) {

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.username(), loginDto.password())
            );
            String token = tokenService.generateJwt(auth);

            LoginResponseDto responseDTO = new LoginResponseDto(userRepository.findByUsername(loginDto.username()).get(), token);
            return ResponseEntity.ok(responseDTO);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "User with this credentials does not exist."));
        }
    }


}
