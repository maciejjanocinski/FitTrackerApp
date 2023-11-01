package app.premium;

import app.authentication.Role;
import app.authentication.RoleRepository;
import app.authentication.TokenService;
import app.user.User;
import app.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PremiumService {
    private final UserRepository userRepository;
    private final TokenService tokenService;
    private final RoleRepository roleRepository;

    @Transactional
    public String getPremium(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Role rolePremium = roleRepository.findByName(Role.roleType.ROLE_USER_PREMIUM.toString())
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.addRole(rolePremium);

        return  tokenService.generateJwt(user);
    }
}
