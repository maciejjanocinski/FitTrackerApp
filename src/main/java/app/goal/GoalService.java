package app.goal;

import app.diary.Diary;
import app.exceptions.InvalidInputException;
import app.user.User;
import app.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

import static app.user.UserService.getUser;

@Service
@RequiredArgsConstructor
class GoalService {

    private final UserRepository userRepository;

    ResponseEntity<Map<String, Double>> getGoal(Authentication authentication) {
        Diary diary = getUser(userRepository, authentication).getDiary();

        Map<String, Double> goal = Map.of(
                "kcal", diary.getGoalKcal(),
                "protein", diary.getGoalProtein(),
                "carbohydrates", diary.getGoalCarbohydrates(),
                "fat", diary.getGoalFat(),
                "fiber", diary.getGoalFiber()
        );

        return ResponseEntity.ok(goal);
    }

    @Transactional
    public ResponseEntity<Map<String, Double>> setGoal(Authentication authentication, GoalDto goalsDto) {
        User user = getUser(userRepository, authentication);
        Diary diary = user.getDiary();

        if (goalsDto.kcal() <= 0 ||
                goalsDto.proteinPercentage() +
                        goalsDto.carbohydratesPercentage() +
                        goalsDto.fatPercentage() != 100) {
            throw new InvalidInputException("Kcal must be greater than 0 and sum of percentages must be equal to 100");
        }

        countAndSetGoal(goalsDto, getUser(userRepository, authentication));

        Map<String, Double> goal = Map.of(
                "kcal", diary.getGoalKcal(),
                "protein", diary.getGoalProtein(),
                "carbohydrates", diary.getGoalCarbohydrates(),
                "fat", diary.getGoalFat(),
                "fiber", diary.getGoalFiber()
        );

        return ResponseEntity.ok(goal);
    }

    private void countAndSetGoal(GoalDto goalsDto, User user) {
        double protein = goalsDto.kcal() * goalsDto.proteinPercentage() / 100 / 4;
        double carbohydrates = goalsDto.kcal() * goalsDto.carbohydratesPercentage() / 100 / 4;
        double fat = goalsDto.kcal() * goalsDto.fatPercentage() / 100 / 9;
        double fiber = Objects.equals(user.getGender(), "M") ? 38 : 25;

        user.getDiary().setGoalKcal(goalsDto.kcal());
        user.getDiary().setGoalProtein(protein);
        user.getDiary().setGoalCarbohydrates(carbohydrates);
        user.getDiary().setGoalFat(fat);
        user.getDiary().setGoalFiber(fiber);
    }
}
