package app.premium;

import app.authentication.Role;
import app.authentication.RoleRepository;
import app.authentication.TokenService;
import app.user.User;
import app.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import static app.util.Utils.ROLE_NOT_FOUND_MESSAGE;

@Service
@RequiredArgsConstructor
public class PremiumService {
    private final UserService userService;
    private final TokenService tokenService;
    private final RoleRepository roleRepository;

    @Transactional
    public String getPremium(Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        Role rolePremium = roleRepository.findByName(Role.roleType.ROLE_USER_PREMIUM.toString())
                .orElseThrow(() -> new RuntimeException(ROLE_NOT_FOUND_MESSAGE));
        user.addRole(rolePremium);

        return  tokenService.generateJwt(user.getAuthorities(), user.getUsername());
    }
}
