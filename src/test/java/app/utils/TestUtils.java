package app.utils;

import app.authentication.Role;
import app.authentication.TokenService;
import app.diary.Diary;
import app.user.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static app.user.User.setGenderFromString;

public class TestUtils {
    public static final String username = "username";
    public static final String query = "bread";
    public static final String userNotFoundMessage = "User not found";
    public static final String productNotFoundMessage = "Product not found";

    public static User buildUser(Role role, PasswordEncoder passwordEncoder) {
        Set<Role> authorities = new HashSet<>();
        authorities.add(role);

        return User.builder()
                .username("username")
                .password(passwordEncoder.encode("Password123!"))
                .diary(new Diary())
                .name("name")
                .surname("surname")
                .gender(setGenderFromString("MALE"))
                .email("maciek@gmail.com")
                .phone("123456789")
                .authorities(authorities)
                .build();
    }

    public static String generateAuthorizationHeader(
            TokenService tokenService,
            Collection<? extends GrantedAuthority> authorities,
            String username) {

        return "Bearer " + tokenService.generateJwt(authorities, username);
    }
}
