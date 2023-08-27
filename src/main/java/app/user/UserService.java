package app.user;

import app.exceptions.InvalidInputException;
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
        User user = getUser(userRepository, authentication);
        UserDto userDto = userMapper.INSTANCE.mapUserToUserDto(user);
        return userDto;
    }

    @Transactional
    public String updateProfile(Authentication authentication, UpdateProfileInfoDto updateProfileInfoDto) {
        User user = getUser(userRepository, authentication);
        updateUserProfile(user, updateProfileInfoDto);
        return "Changes has been successfully approved";
    }

    @Transactional
    public String updatePassword(Authentication authentication, UpdatePasswordDto password) {
        User user = getUser(userRepository, authentication);

        if (password.oldPassword().equals(password.confirmOldPassword()) &&
                passwordEncoder.matches(password.oldPassword(), user.getPassword())) {

            if (setPasswordWithValidation(password.newPassword())) {
                user.setPassword(passwordEncoder.encode(password.newPassword()));
                return "Password has been successfully changed";
            }
        } else if (!password.oldPassword().equals(password.confirmOldPassword())) {
            throw new InvalidInputException("Passwords are not the same.");
        }

      throw new InvalidInputException("You have passed wrong password.");
    }

    String deleteProfile(Authentication authentication, DeleteUserDto deleteUserDto) {
        User user = getUser(userRepository, authentication);

        if (passwordEncoder.matches(deleteUserDto.password(), user.getPassword()) &&
                deleteUserDto.password().equals(deleteUserDto.confirmPassword())) {
            userRepository.delete(user);
            return "Profile with username \"" + user.getUsername() + "\" has been deleted.";
        }

        throw new InvalidInputException("You have passed wrong password.");
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
