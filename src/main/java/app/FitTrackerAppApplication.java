package app;

import app.models.RoleEntity;
import app.models.UserEntity;
import app.repository.RoleRepository;
import app.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@SpringBootApplication
public class FitTrackerAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(FitTrackerAppApplication.class, args);
	}

	 @Bean
	CommandLineRunner run (RoleRepository roleRepository,
						   UserRepository userRepository,
						   PasswordEncoder passwordEncoder) {
		return args -> {
			if(roleRepository.findByAuthority("ADMIN").isPresent()) return;
			roleRepository.save(new RoleEntity("USER"));
			RoleEntity adminRole = roleRepository.save(new RoleEntity("ADMIN"));

			Set<RoleEntity> roles = new HashSet<>();
			roles.add(adminRole);
			String password = passwordEncoder.encode("admin");


			UserEntity admin = new UserEntity("admin", password, roles );
			userRepository.save(admin);
 		};
	 }

}
