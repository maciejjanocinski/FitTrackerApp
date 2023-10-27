package app.user;

import app.exceptions.InvalidPasswordException;
import app.user.dto.DeleteUserDto;
import app.user.dto.UpdatePasswordDto;
import app.user.dto.UpdateProfileInfoDto;
import app.user.dto.UserDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static app.utils.TestUtils.username;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    UserMapper userMapper;
    @Mock
    Authentication authentication;

    @Test
    void loadUserByUsername_inputDataOk_returnsUserDetails() {
        //given
        User user = buildUser();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        //when
        UserDetails actualUser = userService.loadUserByUsername(username);

        //then
        assertEquals(actualUser.getUsername(), username);
        verify(userRepository).findByUsername(username);
    }

    @Test
    void loadUserByUsername_userNotFound_throwsUsernameNotFoundException() {
        //given
        String message = "User not found";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        //when
        Exception ex = assertThrows(UsernameNotFoundException.class,
                () -> userService.loadUserByUsername(username));

        //then
        assertEquals(ex.getMessage(), message);
        verify(userRepository).findByUsername(username);
    }

    @Test
    void getUser_inputDataOk_returnsUserDto() {
        //given
        User user = buildUser();
        UserDto userDto = buildUserDto();
        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userMapper.mapUserToUserDto(user)).thenReturn(userDto);

        //when
        UserDto actualUserDto = userService.getUser(authentication);

        //then
        assertEquals(actualUserDto, userDto);
        verify(userRepository).findByUsername(username);
        verify(userMapper).mapUserToUserDto(user);
    }

    @Test
    void getUser_UserNotFound_throwsUsernameNotFoundException() {
        //given
        String expectedMessage = "User not found";
        User user = buildUser();
        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        //when
        Exception ex = assertThrows(UsernameNotFoundException.class,
                () -> userService.getUser(authentication));

        //then
        assertEquals(expectedMessage, ex.getMessage());
        verify(authentication).getName();
        verify(userRepository).findByUsername(username);
        verify(userMapper, never()).mapUserToUserDto(user);
    }

    @Test
    void updateProfile_inputDataOk_returnsString() {
        //given
        String expectedMessage = "Changes has been successfully approved";
        User user = buildUser();
        UpdateProfileInfoDto updateProfileInfoDto = buildUpdateProfileInfoDto(username);

        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        //when
        String actualMessage = userService.updateProfile(authentication, updateProfileInfoDto);

        //then
        assertEquals(expectedMessage, actualMessage);
        assertEquals(user.getUsername(), updateProfileInfoDto.username());

        verify(authentication).getName();
        verify(userRepository).findByUsername(username);
    }

    @Test
    void updateProfile_usernameNotFound_throwsUsernameNotFoundException() {
        //given
        String expectedMessage = "User not found";
        UpdateProfileInfoDto updateProfileInfoDto = buildUpdateProfileInfoDto(username);

        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        //when
        Exception ex = assertThrows(UsernameNotFoundException.class,
                () -> userService.updateProfile(authentication, updateProfileInfoDto));

        //then
        assertEquals(expectedMessage, ex.getMessage());

        verify(authentication).getName();
        verify(userRepository).findByUsername(username);
    }

    @Test
    void updatePassword_inputDataOk_returnsString() {
        //given
        String expectedMessage = "Password has been successfully changed";
        User user = buildUser();
        UpdatePasswordDto updatePasswordDto = buildUpdatePasswordDto();

        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(updatePasswordDto.oldPassword(), user.getPassword())).thenReturn(true);
        when(passwordEncoder.encode(updatePasswordDto.newPassword())).thenReturn("encodedPassword");

        //when
        String actualMessage = userService.updatePassword(authentication, updatePasswordDto);

        //then
        assertEquals(expectedMessage, actualMessage);

        verify(authentication).getName();
        verify(userRepository).findByUsername(username);
        verify(passwordEncoder).encode(updatePasswordDto.newPassword());
    }

    @Test
    void updatePassword_usernameNotFound_throwsUsernameNotFoundException() {
        //given
        String expectedMessage = "User not found";
        UpdatePasswordDto updatePasswordDto = buildUpdatePasswordDto();

        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        //when

        Exception ex = assertThrows(UsernameNotFoundException.class,
                () -> userService.updatePassword(authentication, updatePasswordDto));

        //then
        assertEquals(expectedMessage, ex.getMessage());

        verify(authentication).getName();
        verify(userRepository).findByUsername(username);
        verify(passwordEncoder, never()).encode(updatePasswordDto.newPassword());
    }

    @Test
    void updatePassword_passwordsAreNotTheSame_throwsInvalidPasswordException() {
        //given
        User user = buildUser();
        String expectedMessage = "Passwords are not the same.";
        UpdatePasswordDto updatePasswordDto = buildUpdatePasswordDto_passwordsNotTheSame();

        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        //when

        Exception ex = assertThrows(InvalidPasswordException.class,
                () -> userService.updatePassword(authentication, updatePasswordDto));

        //then
        assertEquals(expectedMessage, ex.getMessage());

        verify(authentication).getName();
        verify(userRepository).findByUsername(username);
        verify(passwordEncoder, never()).encode(updatePasswordDto.newPassword());
    }

    @Test
    void updatePassword_passedWrongPassword_throwsInvalidPasswordException() {
        //given
        User user = buildUser();
        String expectedMessage = "You have passed wrong password.";
        UpdatePasswordDto updatePasswordDto = buildUpdatePasswordDto_wrongPassword();

        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        //when

        Exception ex = assertThrows(InvalidPasswordException.class,
                () -> userService.updatePassword(authentication, updatePasswordDto));

        //then
        assertEquals(expectedMessage, ex.getMessage());

        verify(authentication).getName();
        verify(userRepository).findByUsername(username);
        verify(passwordEncoder, never()).encode(updatePasswordDto.newPassword());
    }

    @Test
    void deleteProfile_inputDataOk_returnsString() {
        //given
        User user = buildUser();
        String expectedMessage = "Profile with username \"" + user.getUsername() + "\" has been deleted.";
        DeleteUserDto deleteUserDto = buildDeleteUserDto();

        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(deleteUserDto.password(), user.getPassword())).thenReturn(true);

        //when
        String actualMessage = userService.deleteProfile(authentication, deleteUserDto);

        //then
        assertEquals(expectedMessage, actualMessage);

        verify(authentication).getName();
        verify(userRepository).findByUsername(username);
        verify(passwordEncoder).matches(deleteUserDto.password(), user.getPassword());
        verify(userRepository).delete(user);
    }

    @Test
    void deleteProfile_usernameNotFound_throwsUsernameNotFoundException() {
        //given
        User user = buildUser();
        String expectedMessage = "User not found";
        DeleteUserDto deleteUserDto = buildDeleteUserDto();

        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        //when
        Exception ex = assertThrows(UsernameNotFoundException.class,
                () -> userService.deleteProfile(authentication, deleteUserDto));

        //then
        assertEquals(expectedMessage, ex.getMessage());

        verify(authentication).getName();
        verify(userRepository).findByUsername(username);
        verify(passwordEncoder, never()).matches(deleteUserDto.password(), user.getPassword());
        verify(userRepository, never()).delete(user);
    }

    @Test
    void deleteProfile_passedWrongPassword_throwsInvalidPasswordException() {
        //given
        User user = buildUser();
        String expectedMessage = "Passwords are not the same.";
        DeleteUserDto deleteUserDto = buildDeleteUserDto_wrongPassword();

        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(deleteUserDto.password(), user.getPassword())).thenReturn(false);

        //when
        Exception ex = assertThrows(InvalidPasswordException.class,
                () -> userService.deleteProfile(authentication, deleteUserDto));

        //then
        assertEquals(expectedMessage, ex.getMessage());

        verify(authentication).getName();
        verify(userRepository).findByUsername(username);
        verify(passwordEncoder).matches(deleteUserDto.password(), user.getPassword());
        verify(userRepository, never()).delete(user);
    }

    @Test
    void setPasswordWithValidation_inputDataOk_returnsTrue() {
        //given
        String password = "Password123@";

        //when
        boolean actualResult = userService.setPasswordWithValidation(password);

        //then
        assertTrue(actualResult);
    }

    @Test
    void setPasswordWithValidation_passedWrongPassword_throwsException() {
        //given
        String password = "Password123";
        String expectedMessage = "Password must have at least one special character like ! @ # & ( ).";

        //when
        Exception ex = assertThrows(InvalidPasswordException.class,
                () -> userService.setPasswordWithValidation(password));

        //then
        assertEquals(expectedMessage, ex.getMessage());
    }

    @Test
    void updateUserProfile_inputDataOk() {
        //given
        String changedUsername = "otherUsername";
        User user = buildUser();
        UpdateProfileInfoDto updateProfileInfoDto = buildUpdateProfileInfoDto(changedUsername);

        //when
        userService.updateUserProfile(user, updateProfileInfoDto);

        //then
        assertNotEquals(username, user.getUsername());
    }


    private DeleteUserDto buildDeleteUserDto() {
        return DeleteUserDto.builder()
                .password("oldPassword123!")
                .confirmPassword("oldPassword123!")
                .build();
    }

    private DeleteUserDto buildDeleteUserDto_wrongPassword() {
        return DeleteUserDto.builder()
                .password("oldPassword123!_wrongOne")
                .confirmPassword("oldPassword123!")
                .build();
    }

    private UpdatePasswordDto buildUpdatePasswordDto() {
        return UpdatePasswordDto.builder()
                .oldPassword("oldPassword123!")
                .confirmNewPassword("newPassword123!")
                .newPassword("newPassword123!")
                .build();
    }

    private UpdatePasswordDto buildUpdatePasswordDto_wrongPassword() {
        return UpdatePasswordDto.builder()
                .oldPassword("oldPassword1_wrong!")
                .confirmNewPassword("newPassword123!")
                .newPassword("newPassword123!")
                .build();
    }

    private UpdatePasswordDto buildUpdatePasswordDto_passwordsNotTheSame() {
        return UpdatePasswordDto.builder()
                .oldPassword("oldPassword123!")
                .confirmNewPassword("oldPassword1234!")
                .newPassword("newPassword123!")
                .build();
    }

    private UpdateProfileInfoDto buildUpdateProfileInfoDto(String username) {
        return UpdateProfileInfoDto.builder()
                .username(username)
                .name("name")
                .surname("surname")
                .gender("MALE")
                .email("email")
                .phone("123456789")
                .build();
    }

    private UserDto buildUserDto() {
        return UserDto.builder()
                .username(username)
                .email("email")
                .gender("MALE")
                .build();
    }

    private User buildUser() {
        return User.builder()
                .username(username)
                .password("oldPassword123!")
                .build();
    }
}