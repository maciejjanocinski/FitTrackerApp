package app.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EnableJpaRepositories
@EntityScan(basePackages = "app")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUsername_inputDataOk() {
        //given
        User user = buildUser();
        userRepository.save(user);

        //when
        Optional<User> userFromDb = userRepository.findByUsername(user.getUsername());

        //then
        assertTrue(userFromDb.isPresent());
        assertEquals(user, userFromDb.get());
    }

    @Test
    void findByUsername_wrongUsername() {
        //given
        User user = buildUser();
        userRepository.save(user);

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
                .gender(User.validateGender("MALE"))
                .email("maciek@gmail.com")
                .phone("123456789")
                .password("Password123!")
                .build();
    }
}