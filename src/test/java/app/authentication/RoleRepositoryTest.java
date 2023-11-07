package app.authentication;

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
@DataJpaTest
@EnableJpaRepositories(basePackageClasses = RoleRepository.class)
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void findByName_inputDataOk() {
        //given
        Role role = Role.builder()
                .name("SOME_ROLE")
                .build();
        roleRepository.save(role);

        //when
        Optional<Role> roleFromDb = roleRepository.findByName(role.getName());

        //then
        assertTrue(roleFromDb.isPresent());
        assertEquals(role, roleFromDb.get());
    }

    @Test
    void findByName_roleDoesNotExist() {
        //given
        Role role = Role.builder()
                .name("ROLE_USER_STANDARD")
                .build();
        roleRepository.save(role);

        //when
        Optional<Role> roleFromDb = roleRepository.findByName("ROLE_MANAGER");

        //then
        assertTrue(roleFromDb.isEmpty());
    }
}