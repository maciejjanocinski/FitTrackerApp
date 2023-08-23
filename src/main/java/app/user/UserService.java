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
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    ResponseEntity<UserDto> getUser(Authentication authentication) {
        User user = getUser(userRepository, authentication);
        UserDto userDto = userMapper.INSTANCE.mapUserToUserDto(user);
        return ResponseEntity.ok(userDto);
    }

    @Transactional
    public ResponseEntity<String> updateProfile(Authentication authentication, UpdateProfileInfoDto updateProfileInfoDto) {
        User user = getUser(userRepository, authentication);
        updateUserProfile(user, updateProfileInfoDto);
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

    private boolean setPasswordWithValidation(String password) {
        PasswordValidator passwordValidator = new PasswordValidator();
        return passwordValidator.isValidSetterCheck(password);
    }

    private void updateUserProfile(User user, UpdateProfileInfoDto updateProfileInfoDto) {
        user.setUsername(updateProfileInfoDto.username());
        user.setName(updateProfileInfoDto.name());
        user.setSurname(updateProfileInfoDto.surname());
        user.setEmail(updateProfileInfoDto.email());
        user.setPhone(updateProfileInfoDto.phone());
        user.setGender(updateProfileInfoDto.gender());
    }
}
