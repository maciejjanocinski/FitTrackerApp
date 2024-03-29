package app.roles;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class RoleInitializer implements CommandLineRunner {

 private final RoleRepository roleRepository;
    @Override
    public void run(String... args) {
        roleRepository.save(Role.builder().name(RoleType.ROLE_USER_STANDARD.toString()).build());
        roleRepository.save(Role.builder().name(RoleType.ROLE_USER_PREMIUM.toString()).build());
        roleRepository.save(Role.builder().name(RoleType.ROLE_ADMIN.toString()).build());
    }
}
