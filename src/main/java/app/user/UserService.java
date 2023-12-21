package app.user;

import app.product.Product;
import app.util.exceptions.InvalidPasswordException;
import app.user.dto.DeleteUserDto;
import app.user.dto.UpdatePasswordDto;
import app.user.dto.UpdateProfileInfoDto;
import app.authentication.TokenService;
import app.user.dto.UserDto;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayDeque;
import java.util.List;

import static app.user.UserMapper.mapUserToUserDto;
import static app.util.Utils.USER_NOT_FOUND_MESSAGE;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND_MESSAGE));
    }

    UserDto getUser(Authentication authentication) {
        User user = getUserByUsername(authentication.getName());
        return mapUserToUserDto(user);
    }

    List<Product> getlastlyAddedProducts(Authentication authentication) {
        User user = getUserByUsername(authentication.getName());
        return user.getLastlyAddedProducts();
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND_MESSAGE));
    }

    @Transactional
    public String updateProfile(Authentication authentication, UpdateProfileInfoDto updateProfileInfoDto) {
        getUserByUsername(authentication.getName()).updateUserProfile(updateProfileInfoDto);
        return tokenService.generateJwt(authentication.getAuthorities(), updateProfileInfoDto.username());
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
            return "Profile with name \"" + user.getUsername() + "\" has been deleted.";
        }

        throw new InvalidPasswordException("You have passed wrong password.");
    }

    boolean passwordsAreTheSameAndMatchOldPassword(UpdatePasswordDto passwordDto, String currentPassword) {
        return passwordDto.newPassword().equals(passwordDto.confirmNewPassword()) &&
                passwordEncoder.matches(passwordDto.oldPassword(), currentPassword);
    }
}
