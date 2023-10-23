package app.user;

import app.exceptions.InvalidPasswordException;
import app.user.dto.DeleteUserDto;
import app.user.dto.UpdatePasswordDto;
import app.user.dto.UpdateProfileInfoDto;
import app.user.dto.UserDto;
import app.util.passwordValidation.PasswordValidator;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    UserDto getUser(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return userMapper.mapUserToUserDto(user);
    }

    @Transactional
    public String updateProfile(Authentication authentication, UpdateProfileInfoDto updateProfileInfoDto) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        updateUserProfile(user, updateProfileInfoDto);
        return "Changes has been successfully approved";
    }

    @Transactional
    public String updatePassword(Authentication authentication, UpdatePasswordDto password) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (password.newPassword().equals(password.confirmNewPassword()) &&
                passwordEncoder.matches(password.oldPassword(), user.getPassword())) {

            if (setPasswordWithValidation(password.newPassword())) {
                user.setPassword(passwordEncoder.encode(password.newPassword()));
                return "Password has been successfully changed";
            }
        } else if (!password.newPassword().equals(password.confirmNewPassword())) {
            throw new InvalidPasswordException("Passwords are not the same.");
        }

      throw new InvalidPasswordException("You have passed wrong password.");
    }

    String deleteProfile(Authentication authentication, DeleteUserDto deleteUserDto) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (passwordEncoder.matches(deleteUserDto.password(), user.getPassword()) &&
                deleteUserDto.password().equals(deleteUserDto.confirmPassword())) {
            userRepository.delete(user);
            return "Profile with username \"" + user.getUsername() + "\" has been deleted.";
        }

        throw new InvalidPasswordException("You have passed wrong password.");
    }

     boolean setPasswordWithValidation(String password) {
        PasswordValidator passwordValidator = new PasswordValidator();
        return passwordValidator.isValidSetterCheck(password);
    }

     void updateUserProfile(User user, UpdateProfileInfoDto updateProfileInfoDto) {
        user.setUsername(updateProfileInfoDto.username());
        user.setName(updateProfileInfoDto.name());
        user.setSurname(updateProfileInfoDto.surname());
        user.setEmail(updateProfileInfoDto.email());
        user.setPhone(updateProfileInfoDto.phone());
        user.setGender(User.validateGender(updateProfileInfoDto.gender()));
    }
}
