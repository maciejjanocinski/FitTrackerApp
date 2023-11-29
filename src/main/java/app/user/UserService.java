package app.user;

import app.util.exceptions.InvalidPasswordException;
import app.user.dto.DeleteUserDto;
import app.user.dto.UpdatePasswordDto;
import app.user.dto.UpdateProfileInfoDto;
import app.user.dto.UserDto;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static app.util.Utils.USER_NOT_FOUND_MESSAGE;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND_MESSAGE));
    }

    UserDto getUser(Authentication authentication) {
        User user = getUserByUsername(authentication.getName());
        return userMapper.mapUserToUserDto(user);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND_MESSAGE));
    }

    @Transactional
    public String updateProfile(Authentication authentication, UpdateProfileInfoDto updateProfileInfoDto) {
        getUserByUsername(authentication.getName()).updateUserProfile(updateProfileInfoDto);
        return "Changes has been successfully approved";
    }

    @Transactional
    public String updatePassword(Authentication authentication, UpdatePasswordDto password) {
        User user = getUserByUsername(authentication.getName());

        if (passwordsAreTheSameAndMatchOldPassword(password, user.getPassword())) {
                user.setPassword(passwordEncoder.encode(password.newPassword()));
                return "Password has been successfully changed";

        } else if (!password.newPassword().equals(password.confirmNewPassword())) {
            throw new InvalidPasswordException("Passwords are not the same.");
        }
        throw new InvalidPasswordException("You have passed wrong password.");
    }

    String deleteProfile(Authentication authentication, DeleteUserDto deleteUserDto) {
        User user = getUserByUsername(authentication.getName());

        if (passwordEncoder.matches(deleteUserDto.password(), user.getPassword())) {
            userRepository.delete(user);
            return "Profile with username \"" + user.getUsername() + "\" has been deleted.";
        }

        throw new InvalidPasswordException("You have passed wrong password.");
    }

    boolean passwordsAreTheSameAndMatchOldPassword(UpdatePasswordDto passwordDto, String currentPassword) {
        return passwordDto.newPassword().equals(passwordDto.confirmNewPassword()) &&
                passwordEncoder.matches(passwordDto.oldPassword(), currentPassword);
    }
}
