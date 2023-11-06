package app.user;

import app.utils.repositories.TestUserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserIntegrationTest {

    @LocalServerPort
    private int port;

    private static RestTemplate restTemplate;

    @Autowired
    private TestUserRepository testUserRepository;

    @BeforeAll
    public static void init() {
        restTemplate = new RestTemplate();
    }
}
