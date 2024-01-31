package app.premium;

import app.authentication.TokenService;
import app.roles.Role;
import app.roles.RoleService;
import app.roles.RoleType;
import app.user.User;
import app.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
 class PremiumService {
    private final UserService userService;
    private final TokenService tokenService;
    private final RoleService roleService;

    @Transactional
    public String getPremium(Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        Role rolePremium = roleService.getRole(RoleType.ROLE_USER_PREMIUM.toString());
        user.addRole(rolePremium);

        return  tokenService.generateJwt(user.getAuthorities(), user.getUsername());
    }

    public String confirmCredentials(Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        return  tokenService.generateJwt(user.getAuthorities(), user.getUsername());
    }
}
