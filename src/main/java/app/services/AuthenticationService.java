package app.services;

import app.dto.LoginResponseDTO;
import app.dto.RegisterDTO;
import app.models.RoleEntity;
import app.models.UserEntity;
import app.repository.RoleRepository;
import app.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.AuthenticationException;
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


    public UserEntity register(RegisterDTO body) {
        UserEntity user = new UserEntity();

        user.setName(body.getName());
        user.setSurname(body.getSurname());
        user.setEmail(body.getEmail());
        user.setPhone(body.getPhone());


        user.setUsername(body.getUsername());
        user.setPassword(passwordEncoder.encode(body.getPassword()));

        RoleEntity userRole = roleRepository.findByAuthority("USER").get();
        RoleEntity userStandardRole = roleRepository.findByAuthority("USER_STANDARD").get();
        Set<RoleEntity> authorities = new HashSet<>();
        authorities.add(userRole);
        authorities.add(userStandardRole);
        user.setAuthorities(authorities);

        return userRepository.save(user);
    }


    public ResponseEntity<Object> login(String username, String password) {

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            String token = tokenService.generateJwt(auth);

            LoginResponseDTO responseDTO = new LoginResponseDTO(userRepository.findByUsername(username).get(), token);
            return ResponseEntity.ok(responseDTO);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", "User with this credentials does not exist."));
        }
    }


}
