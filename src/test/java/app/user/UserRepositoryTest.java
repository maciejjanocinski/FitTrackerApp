//package app.user;
//
//import app.authentication.Role;
//import app.utils.TestUtils;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//
//import java.util.Optional;
//import java.util.Set;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//
//@DataJpaTest
//@EnableJpaRepositories(basePackageClasses = UserRepository.class)
//class UserRepositoryTest {
//
//    @Autowired
//    private UserRepository userRepository;
//    private final TestUtils utils = new TestUtils();
//    private final Role role = utils.buildRoleStandard();
//    private final User user = utils.buildUser(Set.of(role));
//
//
//    @BeforeEach
//    void setUp() {
//        userRepository.save(user);
//    }
//
//    @Test
//    void findByUsername_inputDataOk() {
//        //when
//        Optional<User> userFromDb = userRepository.findByUsername(user.getUsername());
//
//        //then
//        assertTrue(userFromDb.isPresent());
//        assertEquals(user, userFromDb.get());
//    }
//
//    @Test
//    void findByUsername_wrongUsername() {
//        //when
//        Optional<User> userFromDb = userRepository.findByUsername("WrongUsername");
//
//        //then
//        assertTrue(userFromDb.isEmpty());
//    }
//}