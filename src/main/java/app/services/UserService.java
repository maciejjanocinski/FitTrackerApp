package app.services;


import app.models.UserEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import app.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Map;


@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public ResponseEntity<UserEntity> getProfile(Authentication authentication) {
        UserEntity user = userRepository.findByUsername(authentication.getName()).get();
        return ResponseEntity.ok(user);
    }

    public ResponseEntity<String> deleteProfile(Authentication authentication, Map<String, String> password) {
        UserEntity user = userRepository.findByUsername(authentication.getName()).get();


        if (passwordEncoder.matches(password.get("password"), user.getPassword())) {
            userRepository.delete(user);
            return ResponseEntity.ok("Your profile with username \"" + user.getUsername() + "\" has been deleted.");
        }

        return ResponseEntity.badRequest().body("You passed wrong password");


    }
}
