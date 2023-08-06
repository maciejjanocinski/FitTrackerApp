package app.services;

import app.models.Goals;
import app.models.UserEntity;
import app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoalsService {

    private final UserRepository userRepository;

    public ResponseEntity<Goals> getGoals(Authentication authentication) {
        UserEntity user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(user.getGoals());
    }
}
