package app.roles;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService implements CommandLineRunner {

 private final RoleRepository roleRepository;
    @Override
    public void run(String... args) {
        roleRepository.save(Role.builder().name(Role.roleType.ROLE_USER_STANDARD.toString()).build());
        roleRepository.save(Role.builder().name(Role.roleType.ROLE_USER_PREMIUM.toString()).build());
        roleRepository.save(Role.builder().name(Role.roleType.ROLE_ADMIN.toString()).build());
    }
}
