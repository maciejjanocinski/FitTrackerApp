package app.services;

import app.dto.DeleteProfileDto;
import app.dto.UpdateProfileInfoDto;
import app.dto.UserDto;
import app.dto.updatePasswordDto;
import app.models.User;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
import app.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public ResponseEntity<?> getProfile(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return ResponseEntity.ok(user);
    }

    @Transactional
    public ResponseEntity<String> updateProfile(Authentication authentication, UpdateProfileInfoDto updates) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Not found user"));

        if (updates.getUpdates().containsKey("username")) {
            user.setUsername(updates.getUpdates().get("username"));
        }
        if (updates.getUpdates().containsKey("name")) {
            user.setName(updates.getUpdates().get("name"));
        }
        if (updates.getUpdates().containsKey("surname")) {
            user.setSurname(updates.getUpdates().get("surname"));
        }
        if (updates.getUpdates().containsKey("email")) {
            user.setEmail(updates.getUpdates().get("email"));
        }
        if (updates.getUpdates().containsKey("phone")) {
            user.setPhone(updates.getUpdates().get("phone"));
        }

        user.setUsername(user.getUsername());
        user.setName(user.getName());
        user.setSurname(user.getSurname());
        user.setEmail(user.getEmail());
        user.setPhone(user.getPhone());

        return ResponseEntity.ok("Changes has been successfully approved");
    }

    @Transactional
    public ResponseEntity<String> updatePassword(Authentication authentication, updatePasswordDto password) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Not found user"));
        UserDto userDto = new UserDto();

        if (password.oldPassword().equals(password.confirmOldPassword()) &&
                passwordEncoder.matches(password.oldPassword(), user.getPassword())) {

            if (userDto.setPasswordWithValidation(password.newPassword()) == null) {
                user.setPassword(passwordEncoder.encode(user.getPassword()));
                return ResponseEntity.ok("Password has been successfully changed");
            }
            return ResponseEntity.badRequest().body(userDto.setPasswordWithValidation(password.newPassword()));
        } else if (!password.oldPassword().equals(password.confirmOldPassword())) {
            return ResponseEntity.badRequest().body("Passwords are not the same.");
        }

        return ResponseEntity.badRequest().body("You have passed wrong password.");
    }

    public ResponseEntity<String> deleteProfile(Authentication authentication, DeleteProfileDto deleteDto) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));


        if (passwordEncoder.matches(deleteDto.password(), user.getPassword())) {
            userRepository.delete(user);
            return ResponseEntity.ok("Profile with username \"" + user.getUsername() + "\" has been deleted.");
        }

        return ResponseEntity.badRequest().body("You have passed wrong password");
    }
}
