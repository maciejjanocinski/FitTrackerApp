package app.utils;

import app.authentication.Role;
import app.authentication.RoleRepository;
import app.authentication.TokenService;
import app.diary.Diary;
import app.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static app.user.User.setGenderFromString;

public class TestUtils {
    public static final String username = "username";
    public static final String query = "bread";
    public static final String userNotFoundMessage = "User not found";
    public static final String productNotFoundMessage = "Product not found";

    public static User buildUser(PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        Set<Role> authorities = new HashSet<>();
        Role role = new Role(1L, Role.roleType.ROLE_USER_STANDARD.toString());
        Role role1 = new Role(2L, Role.roleType.ROLE_USER_PREMIUM.toString());
        roleRepository.save(role);
        roleRepository.save(role1);
        authorities.add(role);
        authorities.add(role1);

        return User.builder()
                .username("username")
                .password(passwordEncoder.encode("Password123!"))
                .diary(new Diary())
                .name("name")
                .surname("surname")
                .gender(setGenderFromString("MALE"))
                .email("maciek@gmail.com")
                .phone("123456789")
                .lastSearchedProducts(new ArrayList<>())
                .authorities(authorities)
                .build();
    }

    public static String generateAuthorizationHeader(
            TokenService tokenService,
            String username) {

        return "Bearer " + tokenService.generateJwt(List.of(new Role(1L, Role.roleType.ROLE_USER_STANDARD.toString())), username);
    }
}
