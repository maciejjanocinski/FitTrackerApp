package app.services;


import app.dto.PasswordDTO;
import app.dto.UserDTO;
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
        UserEntity user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return ResponseEntity.ok(user);
    }

    public ResponseEntity<String> deleteProfile(Authentication authentication, Map<String, String> password) {
        UserEntity user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));


        if (passwordEncoder.matches(password.get("password"), user.getPassword())) {
            userRepository.delete(user);
            return ResponseEntity.ok("Profile with username \"" + user.getUsername() + "\" has been deleted.");
        }

        return ResponseEntity.badRequest().body("You passed wrong password");
    }

    public ResponseEntity<String> updateProfile(Authentication authentication, Map<String, String> updates) {
        UserEntity user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Not found user"));


        if (updates.containsKey("username")) {
            user.setUsername(updates.get("username"));
        }
        if (updates.containsKey("name")) {
            user.setName(updates.get("name"));
        }
        if (updates.containsKey("surname")) {
            user.setSurname(updates.get("surname"));
        }
        if (updates.containsKey("email")) {
            user.setEmail(updates.get("email"));
        }
        if (updates.containsKey("phone")) {
            user.setPhone(updates.get("phone"));
        }

        user.setUsername(user.getUsername());
        user.setName(user.getName());
        user.setSurname(user.getSurname());
        user.setEmail(user.getEmail());
        user.setPhone(user.getPhone());

        userRepository.save(user);
        return ResponseEntity.ok("Changes has been successfully approved");

    }

    public ResponseEntity<String> updatePassword(Authentication authentication, PasswordDTO password) {
        UserEntity user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Not found user"));

        if (passwordEncoder.matches(password.getOldPassword(), user.getPassword())) {
            user.setPassword(passwordEncoder.encode(password.getNewPassword()));
            userRepository.save(user);
            return ResponseEntity.ok("Password has been successfully changed");
        }

        return ResponseEntity.badRequest().body("You passed wrong password");
    }
}
