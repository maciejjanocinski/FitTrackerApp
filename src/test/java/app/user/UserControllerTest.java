package app.user;

import app.user.dto.UpdatePasswordDto;
import app.user.dto.UpdateProfileInfoDto;
import app.user.dto.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;

import static app.user.User.setGenderFromString;
import static app.utils.TestUtils.userNotFoundMessage;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.parseMediaType;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private final UserDto userDto = buildUserDto();

    @Test
    void getUser_inputDataOk_returns200() throws Exception {
        //given
        when(userService.getUser(any(Authentication.class))).thenReturn(userDto);

        //when
        mockMvc.perform(get("/user/")
                .with(jwt().jwt(j -> j.claim("roles", "ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(userDto)))
                .andDo(print());

        //then
        verify(userService).getUser(any(Authentication.class));
    }

    @Test
    void getUser_userNotFound_returns404() throws Exception {
        //given
        when(userService.getUser(any(Authentication.class)))
                .thenThrow(new UsernameNotFoundException(userNotFoundMessage));

        //when
        mockMvc.perform(get("/user/")
                        .with(jwt().jwt(j -> j.claim("roles", "ROLE_USER"))))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(parseMediaType("text/plain;charset=UTF-8")))
                .andExpect(content().string(userNotFoundMessage))
                .andDo(print());

        //then
        verify(userService).getUser(any(Authentication.class));
    }

    @Test
    void updateProfile_inputDataOk_returns200() throws Exception {
        //given
        UpdateProfileInfoDto profileInfoDto = UpdateProfileInfoDto.builder()
                .username("Username")
                .name("Name")
                .surname("Surname")
                .gender("MALE")
                .email("maciek@gmail.com")
                .phone("123456789")
                .build();
        String message = "Changes has been successfully approved";
        when(userService.updateProfile(any(), any())).thenReturn(message);

        //when
        mockMvc.perform(patch("/user/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileInfoDto))
                        .with(jwt().jwt(j -> j.claim("roles", "ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(parseMediaType("text/plain;charset=UTF-8")))
                .andExpect(content().string(message))
                .andDo(print());

        //then
        verify(userService).updateProfile(any(), any());
    }

    @Test
    void updateProfile_wrongUsernameMoreThan20Chars_returns400() throws Exception {
        //given
        UpdateProfileInfoDto profileInfoDto = UpdateProfileInfoDto.builder()
                .username("Usernameeeeeeeeeeeeeeeeeeeeeeeeee")
                .name("Name")
                .surname("Surname")
                .gender("MALE")
                .email("maciek@gmail.com")
                .phone("123456789")
                .build();

        String message = "Username cannot have more than 20 characters.";

        //when
        mockMvc.perform(patch("/user/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileInfoDto))
                        .with(jwt().jwt(j -> j.claim("roles", "ROLE_USER"))))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(parseMediaType("text/plain;charset=UTF-8")))
                .andExpect(content().string(message))
                .andDo(print());

        //then
        verify(userService, never()).updateProfile(any(), any());
    }

    @Test
    void updateProfile_wrongUsernameLessThan6Chars_returns400() throws Exception {
        //given
        UpdateProfileInfoDto profileInfoDto = UpdateProfileInfoDto.builder()
                .username("User")
                .name("Name")
                .surname("Surname")
                .gender("MALE")
                .email("maciek@gmail.com")
                .phone("123456789")
                .build();

        String message = "Username must have at least 6 characters.";

        //when
        mockMvc.perform(patch("/user/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileInfoDto))
                        .with(jwt().jwt(j -> j.claim("roles", "ROLE_USER"))))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(parseMediaType("text/plain;charset=UTF-8")))
                .andExpect(content().string(message))
                .andDo(print());

        //then
        verify(userService, never()).updateProfile(any(), any());
    }

    @Test
    void updateProfile_wrongUsernameEmpty_returns400() throws Exception {
        //given
        UpdateProfileInfoDto profileInfoDto = UpdateProfileInfoDto.builder()
                .name("Name")
                .surname("Surname")
                .gender("MALE")
                .email("maciek@gmail.com")
                .phone("123456789")
                .build();

        String message = "You have to pass your username.";

        //when
        mockMvc.perform(patch("/user/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileInfoDto))
                        .with(jwt().jwt(j -> j.claim("roles", "ROLE_USER"))))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(parseMediaType("text/plain;charset=UTF-8")))
                .andExpect(content().string(message))
                .andDo(print());

        //then
        verify(userService, never()).updateProfile(any(), any());
    }

    @Test
    void updateProfile_wrongNameEmpty_returns400() throws Exception {
        //given
        UpdateProfileInfoDto profileInfoDto = UpdateProfileInfoDto.builder()
                .username("Username")
                .name("")
                .surname("Surname")
                .gender("MALE")
                .email("maciek@gmail.com")
                .phone("123456789")
                .build();

        String message = "You have to pass your name.";

        //when
        mockMvc.perform(patch("/user/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileInfoDto))
                        .with(jwt().jwt(j -> j.claim("roles", "ROLE_USER"))))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(parseMediaType("text/plain;charset=UTF-8")))
                .andExpect(content().string(message))
                .andDo(print());

        //then
        verify(userService, never()).updateProfile(any(), any());
    }

    @Test
    void updateProfile_wrongSurnameEmpty_returns400() throws Exception {
        //given
        UpdateProfileInfoDto profileInfoDto = UpdateProfileInfoDto.builder()
                .username("Username")
                .name("name")
                .surname("")
                .gender("MALE")
                .email("maciek@gmail.com")
                .phone("123456789")
                .build();

        String message = "You have to pass your surname.";

        //when
        mockMvc.perform(patch("/user/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileInfoDto))
                        .with(jwt().jwt(j -> j.claim("roles", "ROLE_USER"))))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(parseMediaType("text/plain;charset=UTF-8")))
                .andExpect(content().string(message))
                .andDo(print());

        //then
        verify(userService, never()).updateProfile(any(), any());
    }

    @Test
    void updateProfile_wrongGender_returns400() throws Exception {
        //given
        UpdateProfileInfoDto profileInfoDto = UpdateProfileInfoDto.builder()
                .username("Username")
                .name("name")
                .surname("Surname")
                .gender("M")
                .email("maciek@gmail.com")
                .phone("123456789")
                .build();

        String message = "Gender doesn't match any of the values. Should be \"MALE\" or \"FEMALE\".";

        //when
        mockMvc.perform(patch("/user/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileInfoDto))
                        .with(jwt().jwt(j -> j.claim("roles", "ROLE_USER"))))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(parseMediaType("text/plain;charset=UTF-8")))
                .andExpect(content().string(message))
                .andDo(print());

        //then
        verify(userService, never()).updateProfile(any(), any());
    }

    @Test
    void updateProfile_wrongEmailEmpty_returns400() throws Exception {
        //given
        UpdateProfileInfoDto profileInfoDto = UpdateProfileInfoDto.builder()
                .username("Username")
                .name("name")
                .surname("Surname")
                .gender("MALE")
                .email("")
                .phone("123456789")
                .build();

        String message = "You have to pass your email.";

        //when
        mockMvc.perform(patch("/user/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileInfoDto))
                        .with(jwt().jwt(j -> j.claim("roles", "ROLE_USER"))))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(parseMediaType("text/plain;charset=UTF-8")))
                .andExpect(content().string(message))
                .andDo(print());

        //then
        verify(userService, never()).updateProfile(any(), any());
    }

    @Test
    void updateProfile_wrongEmail_returns400() throws Exception {
        //given
        UpdateProfileInfoDto profileInfoDto = UpdateProfileInfoDto.builder()
                .username("Username")
                .name("name")
                .surname("Surname")
                .gender("MALE")
                .email("maciek.gmail.com")
                .phone("123456789")
                .build();

        String message = "must be a well-formed email address";

        //when
        mockMvc.perform(patch("/user/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileInfoDto))
                        .with(jwt().jwt(j -> j.claim("roles", "ROLE_USER"))))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(parseMediaType("text/plain;charset=UTF-8")))
                .andExpect(content().string(message))
                .andDo(print());

        //then
        verify(userService, never()).updateProfile(any(), any());
    }

    @Test
    void updateProfile_wrongPhone_returns400() throws Exception {
        //given
        UpdateProfileInfoDto profileInfoDto = UpdateProfileInfoDto.builder()
                .username("Username")
                .name("name")
                .surname("Surname")
                .gender("MALE")
                .email("maciek@gmail.com")
                .phone("123456")
                .build();

        String message = "Phone number must contain 9 digits.";

        //when
        mockMvc.perform(patch("/user/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileInfoDto))
                        .with(jwt().jwt(j -> j.claim("roles", "ROLE_USER"))))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(parseMediaType("text/plain;charset=UTF-8")))
                .andExpect(content().string(message))
                .andDo(print());

        //then
        verify(userService, never()).updateProfile(any(), any());
    }

    @Test
    void updatePassword_inputDataOk_returns200() throws Exception {
        //given
        UpdatePasswordDto updatePasswordDto = UpdatePasswordDto.builder()
                .oldPassword("OldPassword123!")
                .newPassword("NewPassword123!")
                .confirmNewPassword("NewPassword123!")
                .build();
        String message = "Changes has been successfully approved";
        when(userService.updatePassword(any(), any())).thenReturn(message);

        //when
        mockMvc.perform(patch("/user/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePasswordDto))
                        .with(jwt().jwt(j -> j.claim("roles", "ROLE_USER"))))
                .andExpect(status().isOk())
                .andExpect(content().contentType(parseMediaType("text/plain;charset=UTF-8")))
                .andExpect(content().string(message))
                .andDo(print());

        //then
        verify(userService).updatePassword(any(), any());
    }

    @Test
    void updatePassword_wrongPasswordNull_returns400() throws Exception {
        //given
        UpdatePasswordDto updatePasswordDto = UpdatePasswordDto.builder()
                .oldPassword(null)
                .newPassword("NewPassword123!")
                .confirmNewPassword("NewPassword123!")
                .build();
        String message = "Password cannot be null.";
        when(userService.updatePassword(any(), any())).thenReturn(message);

        //when
        mockMvc.perform(patch("/user/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePasswordDto))
                        .with(jwt().jwt(j -> j.claim("roles", "ROLE_USER"))))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(parseMediaType("text/plain;charset=UTF-8")))
                .andExpect(content().string(message))
                .andDo(print());

        //then
        verify(userService, never()).updatePassword(any(), any());
    }

    @Test
    void updatePassword_wrongPasswordMoreThan20_returns400() throws Exception {
        //given
        UpdatePasswordDto updatePasswordDto = UpdatePasswordDto.builder()
                .oldPassword("oldPassword123!")
                .newPassword("NewPassword123!!!!!!!!!!")
                .confirmNewPassword("NewPassword123!!!!!!!!!")
                .build();
        String message = "Password must have 8-20 characters.";
        when(userService.updatePassword(any(), any())).thenReturn(message);

        //when
        mockMvc.perform(patch("/user/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePasswordDto))
                        .with(jwt().jwt(j -> j.claim("roles", "ROLE_USER"))))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(parseMediaType("text/plain;charset=UTF-8")))
                .andExpect(content().string(message))
                .andDo(print());

        //then
        verify(userService, never()).updatePassword(any(), any());
    }

    @Test
    void updatePassword_wrongPasswordLessThan8_returns400() throws Exception {
        //given
        UpdatePasswordDto updatePasswordDto = UpdatePasswordDto.builder()
                .oldPassword("OldPassword12!")
                .newPassword("New12!")
                .confirmNewPassword("New12!")
                .build();
        String message = "Password must have 8-20 characters.";
        when(userService.updatePassword(any(), any())).thenReturn(message);

        //when
        mockMvc.perform(patch("/user/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePasswordDto))
                        .with(jwt().jwt(j -> j.claim("roles", "ROLE_USER"))))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(parseMediaType("text/plain;charset=UTF-8")))
                .andExpect(content().string(message))
                .andDo(print());

        //then
        verify(userService, never()).updatePassword(any(), any());
    }

    @Test
    void updatePassword_wrongPasswordNoDigit_returns400() throws Exception {
        //given
        UpdatePasswordDto updatePasswordDto = UpdatePasswordDto.builder()
                .oldPassword("OldPassword12!")
                .newPassword("NewPassword!")
                .confirmNewPassword("NewPassword!")
                .build();
        String message = "Password must have at least one digit.";
        when(userService.updatePassword(any(), any())).thenReturn(message);

        //when
        mockMvc.perform(patch("/user/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePasswordDto))
                        .with(jwt().jwt(j -> j.claim("roles", "ROLE_USER"))))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(parseMediaType("text/plain;charset=UTF-8")))
                .andExpect(content().string(message))
                .andDo(print());

        //then
        verify(userService, never()).updatePassword(any(), any());
    }

    @Test
    void updatePassword_wrongPasswordNoLowercase_returns400() throws Exception {
        //given
        UpdatePasswordDto updatePasswordDto = UpdatePasswordDto.builder()
                .oldPassword("OLDPASSWORD12!")
                .newPassword("NEWPASS12!")
                .confirmNewPassword("NEWPASS12!")
                .build();
        String message = "Password must have at least one lowercase letter.";
        when(userService.updatePassword(any(), any())).thenReturn(message);

        //when
        mockMvc.perform(patch("/user/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePasswordDto))
                        .with(jwt().jwt(j -> j.claim("roles", "ROLE_USER"))))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(parseMediaType("text/plain;charset=UTF-8")))
                .andExpect(content().string(message))
                .andDo(print());

        //then
        verify(userService, never()).updatePassword(any(), any());
    }

    private UserDto buildUserDto() {
        return UserDto.builder()
                .username("Username")
                .name("Name")
                .surname("Surname")
                .gender(setGenderFromString("MALE").toString())
                .email("maciek@gmail.com")
                .phone("123456789")
                .build();
    }
}