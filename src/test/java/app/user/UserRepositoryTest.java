package app.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@EnableJpaRepositories(basePackageClasses = UserRepository.class)
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    private User user;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        this.user = buildUser();
        userRepository.save(user);
    }

    @Test
    void findByUsername_inputDataOk() {

        //when
        Optional<User> userFromDb = userRepository.findByUsername(user.getUsername());

        //then
        assertTrue(userFromDb.isPresent());
        assertEquals(user, userFromDb.get());
    }

    @Test
    void findByUsername_wrongUsername() {

        //when
        Optional<User> userFromDb = userRepository.findByUsername("WrongUsername");

        //then
        assertTrue(userFromDb.isEmpty());
    }

    private User buildUser() {
        return User.builder()
                .username("username")
                .name("name")
                .surname("surname")
                .gender(User.setGenderFromString("MALE"))
                .email("maciek@gmail.com")
                .phone("123456789")
                .password("Password123!")
                .build();
    }
}