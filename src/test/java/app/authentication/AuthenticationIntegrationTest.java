package app.authentication;

import app.user.User;
import app.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Optional;

import static app.utils.TestUtils.buildUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.http.MediaType.parseMediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@NoArgsConstructor
@AutoConfigureMockMvc
public class AuthenticationIntegrationTest {

    @LocalServerPort
    @Value("${app.test.port}")
    private int port;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtDecoder jwtDecoder;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
    }

    @Test
    public void testRegister_testLogin() throws Exception {
        //given
        RegisterDto registerDto = buildRegisterDto();
        LoginDto loginDto = buildLoginDto();

        //when
        mockMvc.perform(post("http://localhost:" + port + "/auth/register")
                        .content(objectMapper.writeValueAsString(registerDto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(registerDto)))
                .andDo(print());

        ResultActions resultActions = mockMvc.perform(post("http://localhost:" + port + "/auth/login")
                        .content(objectMapper.writeValueAsString(loginDto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(parseMediaType("text/plain;charset=UTF-8")))
                .andDo(print());

        Optional<User> user = userRepository.findByUsername(registerDto.username());

        //then
        assertNotNull(user);
        assertEquals(user.get().getUsername(), registerDto.username());
        assertEquals(jwtDecoder.decode(resultActions
                        .andReturn()
                        .getResponse()
                        .getContentAsString())
                .getSubject(), loginDto.username()); //valid token
    }

    @Test
    public void testLogin() throws Exception {
        //given
        LoginDto loginDto = buildLoginDto();
        User user = buildUser(new Role(1L, Role.roleType.ROLE_USER_STANDARD.toString()), passwordEncoder);
        userRepository.save(user);

        //when
        ResultActions resultActions = mockMvc.perform(post("http://localhost:" + port + "/auth/login")
                        .content(objectMapper.writeValueAsString(loginDto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(parseMediaType("text/plain;charset=UTF-8")))
                .andDo(print());

        MvcResult result = resultActions.andReturn();

    }


    private RegisterDto buildRegisterDto() {
        return RegisterDto.builder()
                .username("username")
                .password("Password123!")
                .name("name")
                .surname("surname")
                .gender("MALE")
                .email("maciek@gmail.com")
                .phone("123456789")
                .build();
    }

    private LoginDto buildLoginDto() {
        return LoginDto.builder()
                .username("username")
                .password("Password123!")
                .build();
    }

}
