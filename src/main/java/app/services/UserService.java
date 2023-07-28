package app.services;

import app.Entities.User;
import app.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class RegisterService {
    UserRepository userRepository;

    public RegisterService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void register(String name, String email, String password) {
        userRepository.save(new User(name, email, password));
    }
}
