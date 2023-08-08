package app.services;

import app.dto.GoalsDto;
import app.exceptions.InvalidInputException;
import app.models.Goals;
import app.models.User;
import app.repositories.GoalsRepository;
import app.repositories.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class GoalsService {

    private final GoalsRepository goalsRepository;
    private final UserRepository userRepository;

    public ResponseEntity<Goals> getGoals(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Goals goals = goalsRepository.findById(user.getDiary().getGoals().getId())
                .orElseThrow(() -> new RuntimeException("Goals not found"));

        return ResponseEntity.ok(goals);
    }

    @Transactional
    public ResponseEntity<Goals> setGoals(Authentication authentication, GoalsDto goalsDto) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (goalsDto.getKcal() <= 0 ||
                goalsDto.getProteinPercentage() +
                        goalsDto.getCarbohydratesPercentage() +
                        goalsDto.getFatPercentage() != 100) {
            throw new InvalidInputException("Kcal must be greater than 0 and sum of percentages must be equal to 100");
        }
        Goals goal = countGoals(goalsDto, user);
        user.getDiary().getGoals().setKcal(goal.getKcal());
        user.getDiary().getGoals().setProtein(goal.getProtein());
        user.getDiary().getGoals().setCarbohydrates(goal.getCarbohydrates());
        user.getDiary().getGoals().setFat(goal.getFat());
        user.getDiary().getGoals().setFiber(goal.getFiber());

        return ResponseEntity.ok(user.getDiary().getGoals());
    }

    private Goals countGoals(GoalsDto goalsDto, User user) {
        double protein = goalsDto.getKcal() * goalsDto.getProteinPercentage() / 100 / 4;
        double carbohydrates = goalsDto.getKcal() * goalsDto.getCarbohydratesPercentage() / 100 / 4;
        double fat = goalsDto.getKcal() * goalsDto.getFatPercentage() / 100 / 9;
        double fiber = Objects.equals(user.getGender(), "M") ? 38 : 25;

        return new Goals(goalsDto.getKcal(), protein, fat, carbohydrates, fiber);
    }
}
