package app.user;

import app.user.dto.UpdateProfileInfoDto;
import app.user.dto.UserDto;
import org.junit.jupiter.api.Assertions;
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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        String username = "username";
        User user = buildUser(username);
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
        String username = "username";
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
        String username = "username";
        User user = buildUser(username);
        UserDto userDto = buildUserDto(username);
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
        String username = "username";
        String expectedMessage = "User not found";
        User user = buildUser(username);
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
        String username = "username";
        String expectedMessage = "Changes has been successfully approved";
        User user = buildUser(username);
        UpdateProfileInfoDto updateProfileInfoDto = buildUpdateProfileInfoDto();

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
    void updateProfile_usernameNotFound_throwsusernameNotFoundException() {
        //given
        String username = "username";
        String expectedMessage = "User not found";
        UpdateProfileInfoDto updateProfileInfoDto = buildUpdateProfileInfoDto();

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















    private UpdateProfileInfoDto buildUpdateProfileInfoDto() {
        return UpdateProfileInfoDto.builder()
                .username("Changed username")
                .name("name")
                .surname("surname")
                .gender("M")
                .email("email")
                .phone("123456789")
                .build();
    }

    private UserDto buildUserDto(String username) {
        return UserDto.builder()
                .username(username)
                .email("email")
                .build();
    }

    private User buildUser(String username) {
        return User.builder()
                .username(username)
                .build();
    }
}