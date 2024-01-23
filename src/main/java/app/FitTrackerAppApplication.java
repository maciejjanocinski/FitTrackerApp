package app;

import app.roles.Role;
import app.roles.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class FitTrackerAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(FitTrackerAppApplication.class, args);
    }
}
