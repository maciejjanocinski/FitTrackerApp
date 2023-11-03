//package app.authentication;
//
//import app.authentication.Role;
//import app.role.RoleRepository;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.domain.EntityScan;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//@ExtendWith(SpringExtension.class)
//@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@EnableJpaRepositories
//@EntityScan(basePackages = "app.authentication")
//class RoleRepositoryTest {
//
//    @Autowired
//    private RoleRepository roleRepository;
//
//    @Test
//    void findByAuthority_inputDataOk() {
//        //given
//        Role role = Role.builder()
//                .authority("ROLE_USER")
//                .build();
//        roleRepository.save(role);
//
//        //when
//        Optional<Role> roleFromDb = roleRepository.findByAuthority(role.getAuthority());
//
//        //then
//        assertTrue(roleFromDb.isPresent());
//        assertEquals(role, roleFromDb.get());
//    }
//
//    @Test
//    void findByAuthority_roleDoesNotExist() {
//        //given
//        Role role = Role.builder()
//                .authority("ROLE_USER")
//                .build();
//        roleRepository.save(role);
//
//        //when
//        Optional<Role> roleFromDb = roleRepository.findByAuthority("ROLE_MANAGER");
//
//        //then
//        assertTrue(roleFromDb.isEmpty());
//    }
//
//
//}