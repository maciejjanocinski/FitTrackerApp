package app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class FitTrackerAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(FitTrackerAppApplication.class, args);
    }

//    @Bean
//    CommandLineRunner run(RoleRepository roleRepository) {
//        return args -> {
//            if (roleRepository.findByAuthority("ADMIN").isPresent()) return;
//            roleRepository.save(new Role("ADMIN"));
//            roleRepository.save(new Role("USER_STANDARD"));
//            roleRepository.save(new Role("USER_PREMIUM"));
//
//        };
//    }
//
}
