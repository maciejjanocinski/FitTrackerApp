package app.authentication;

import app.user.User;
import app.utils.repositories.TestUserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

import static app.user.User.setGenderFromString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AuthenticationIntegrationTest {

    @LocalServerPort
    private int port;

    private static RestTemplate restTemplate;

    @Autowired
    private TestUserRepository testUserRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtDecoder jwtDecoder;

    @BeforeAll
    public static void init() {
        restTemplate = new RestTemplate();
    }

    @BeforeEach
    public void setUp() {
        testUserRepository.deleteAll();
    }

    @Test
    public void testRegister() {
        //given
        RegisterDto registerDto = buildRegisterDto();

        //when
        ResponseEntity<RegisterDto> response = restTemplate
                .postForEntity("http://localhost:" + port + "/auth/register", registerDto, RegisterDto.class);

        Optional<User> user = testUserRepository.findByUsername(registerDto.username());

        //then
        assertNotNull(user);
        assertEquals(response.getHeaders().getContentType(), MediaType.APPLICATION_JSON);
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(registerDto, response.getBody());
        assertEquals(user.get().getUsername(), registerDto.username());
        assertEquals(user.get().getAuthorities().size(), 1);

    }

    @Test
    public void testLogin() {
        //given
        LoginDto loginDto = buildLoginDto();
        User user = buildUser();
        testUserRepository.save(user);

        //when
        ResponseEntity<String> response = restTemplate
                .postForEntity("http://localhost:" + port + "/auth/login", loginDto, String.class);

        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(jwtDecoder.decode(response.getBody()).getSubject(), loginDto.username()); //valid token
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

    private User buildUser() {
        return User.builder()
                .username("username")
                .password(passwordEncoder.encode("Password123!"))
                .name("name")
                .surname("surname")
                .gender(setGenderFromString("MALE"))
                .email("maciek@gmail.com")
                .phone("123456789")
                .build();
    }
}
