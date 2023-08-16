package app.user;

import app.util.passwordValidation.PasswordValidator;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.AllArgsConstructor;
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

    ResponseEntity<User> getUser(Authentication authentication) {
        User user = getUser(userRepository, authentication);

        return ResponseEntity.ok(user);
    }

    @Transactional
    public ResponseEntity<String> updateProfile(Authentication authentication, UpdateProfileInfoDto updateProfileInfoDto) {
        User user = getUser(userRepository, authentication);

        if (updateProfileInfoDto.updates().containsKey("username")) {
            user.setUsername(updateProfileInfoDto.updates().get("username"));
        }
        if (updateProfileInfoDto.updates().containsKey("name")) {
            user.setName(updateProfileInfoDto.updates().get("name"));
        }
        if (updateProfileInfoDto.updates().containsKey("surname")) {
            user.setSurname(updateProfileInfoDto.updates().get("surname"));
        }
        if (updateProfileInfoDto.updates().containsKey("email")) {
            user.setEmail(updateProfileInfoDto.updates().get("email"));
        }
        if (updateProfileInfoDto.updates().containsKey("phone")) {
            user.setPhone(updateProfileInfoDto.updates().get("phone"));
        }

        user.setUsername(user.getUsername());
        user.setName(user.getName());
        user.setSurname(user.getSurname());
        user.setEmail(user.getEmail());
        user.setPhone(user.getPhone());

        return ResponseEntity.ok("Changes has been successfully approved");
    }

    @Transactional
    public ResponseEntity<String> updatePassword(Authentication authentication, UpdatePasswordDto password) {
        User user = getUser(userRepository, authentication);

        if (password.oldPassword().equals(password.confirmOldPassword()) &&
                passwordEncoder.matches(password.oldPassword(), user.getPassword())) {

            if (setPasswordWithValidation(password.newPassword())) {
                user.setPassword(passwordEncoder.encode(password.newPassword()));
                return ResponseEntity.ok("Password has been successfully changed");
            }
        } else if (!password.oldPassword().equals(password.confirmOldPassword())) {
            return ResponseEntity.badRequest().body("Passwords are not the same.");
        }

        return ResponseEntity.badRequest().body("You have passed wrong password.");
    }

    ResponseEntity<String> deleteProfile(Authentication authentication, DeleteUserDto deleteUserDto) {
        User user = getUser(userRepository, authentication);

        if (passwordEncoder.matches(deleteUserDto.password(), user.getPassword()) &&
                deleteUserDto.password().equals(deleteUserDto.confirmPassword())) {
            userRepository.delete(user);
            return ResponseEntity.ok("Profile with username \"" + user.getUsername() + "\" has been deleted.");
        }

        return ResponseEntity.badRequest().body("You have passed wrong password");
    }

    public static User getUser(UserRepository userRepository, Authentication authentication) {
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    boolean setPasswordWithValidation(String password) {
        PasswordValidator passwordValidator = new PasswordValidator();
        return passwordValidator.isValidSetterCheck(password);
    }
}
