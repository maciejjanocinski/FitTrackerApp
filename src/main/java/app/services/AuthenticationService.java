package app.services;

import app.dto.LoginResponseDTO;
import app.models.RoleEntity;
import app.models.UserEntity;
import app.repository.RoleRepository;
import app.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.AuthenticationException;
import java.util.HashSet;
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


    public UserEntity register(String username, String password) {
        UserEntity user = new UserEntity();
        user.setUsername(username);

        user.setPassword(passwordEncoder.encode(password));

        RoleEntity role = roleRepository.findByAuthority("USER").get();
        Set<RoleEntity> authorities = new HashSet<>();
        authorities.add(role);

        user.setAuthorities(authorities);

        return userRepository.save(user);
    }


    public LoginResponseDTO login(String username, String password) {

        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );
            String token = tokenService.generateJwt(auth);

            return new LoginResponseDTO(userRepository.findByUsername(username).get(), token);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }

    }


}
