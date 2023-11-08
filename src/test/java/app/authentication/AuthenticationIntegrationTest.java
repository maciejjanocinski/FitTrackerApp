//package app.authentication;
//
//import app.user.User;
//import app.user.UserRepository;
//import app.utils.TestUtils;
//import lombok.RequiredArgsConstructor;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.oauth2.jwt.JwtDecoder;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//@RequiredArgsConstructor
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//public class AuthenticationIntegrationTest {
//
//    @LocalServerPort
//    private int port;
//
//    private static RestTemplate restTemplate;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private JwtDecoder jwtDecoder;
//
//    private final TestUtils utils;
//
//    @BeforeAll
//    public static void init() {
//        restTemplate = new RestTemplate();
//    }
//
//    @BeforeEach
//    public void setUp() {
//        userRepository.deleteAll();
//    }
//
//    @Test
//    public void testRegister() {
//        //given
//        RegisterDto registerDto = buildRegisterDto();
//
//        //when
//        ResponseEntity<RegisterDto> response = restTemplate
//                .postForEntity("http://localhost:" + port + "/auth/register", registerDto, RegisterDto.class);
//
//        Optional<User> user = userRepository.findByUsername(registerDto.username());
//
//        //then
//        assertNotNull(user);
//        assertEquals(response.getHeaders().getContentType(), MediaType.APPLICATION_JSON);
//        assertEquals(response.getStatusCode(), HttpStatus.OK);
//        assertEquals(registerDto, response.getBody());
//        assertEquals(user.get().getUsername(), registerDto.username());
//        assertEquals(user.get().getAuthorities().size(), 1);
//    }
//
//    @Test
//    public void testLogin() {
//        //given
//        LoginDto loginDto = buildLoginDto();
//        User user = utils.buildUser(new Role(1L, Role.roleType.ROLE_USER_STANDARD.toString()));
//        userRepository.save(user);
//
//        //when
//        ResponseEntity<String> response = restTemplate
//                .postForEntity("http://localhost:" + port + "/auth/login", loginDto, String.class);
//
//        assertEquals(response.getStatusCode(), HttpStatus.OK);
//        assertEquals(jwtDecoder.decode(response.getBody()).getSubject(), loginDto.username()); //valid token
//    }
//
//
//    private RegisterDto buildRegisterDto() {
//        return RegisterDto.builder()
//                .username("username")
//                .password("Password123!")
//                .name("name")
//                .surname("surname")
//                .gender("MALE")
//                .email("maciek@gmail.com")
//                .phone("123456789")
//                .build();
//    }
//
//    private LoginDto buildLoginDto() {
//        return LoginDto.builder()
//                .username("username")
//                .password("Password123!")
//                .build();
//    }
//
//}
