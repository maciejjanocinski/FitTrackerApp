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

    ResponseEntity<GoalResponseDto> getGoal(Authentication authentication) {
        Diary diary = getUser(userRepository, authentication).getDiary();
        diary.calculateNutrientsLeft();
        diary.calculateNutrientsSum();

        GoalResponseDto goal = new GoalResponseDto(diary.getGoalKcal(), diary.getGoalProtein(), diary.getGoalCarbohydrates(),
                diary.getGoalFat(), diary.getGoalFiber());
        return ResponseEntity.ok(goal);
    }

    @Transactional
    public ResponseEntity<GoalResponseDto> setGoal(Authentication authentication, GoalDto goalsDto) {
        validateGoalDto(goalsDto);
        User user = getUser(userRepository, authentication);
        Diary diary = user.getDiary();

        countAndSetGoal(goalsDto, diary, user.getGender());
        diary.calculateNutrientsLeft();
        diary.calculateNutrientsSum();

      GoalResponseDto goal = new GoalResponseDto(diary.getGoalKcal(), diary.getGoalProtein(), diary.getGoalCarbohydrates(),
                diary.getGoalFat(), diary.getGoalFiber());

        return ResponseEntity.ok(goal);
    }

    private void countAndSetGoal(GoalDto goalsDto, Diary diary, String gender) {
        double protein = goalsDto.kcal() * goalsDto.proteinPercentage() / 100 / 4;
        double carbohydrates = goalsDto.kcal() * goalsDto.carbohydratesPercentage() / 100 / 4;
        double fat = goalsDto.kcal() * goalsDto.fatPercentage() / 100 / 9;
        double fiber = Objects.equals(gender, "M") ? 38 : 25;

        diary.setGoalKcal(goalsDto.kcal());
        diary.setGoalProtein(protein);
        diary.setGoalCarbohydrates(carbohydrates);
        diary.setGoalFat(fat);
        diary.setGoalFiber(fiber);
    }

    private void validateGoalDto(GoalDto goalDto) {
        if (goalDto.kcal() <= 0 ||
                goalDto.proteinPercentage() +
                        goalDto.carbohydratesPercentage() +
                        goalDto.fatPercentage() != 100) {
            throw new InvalidInputException("Kcal must be greater than 0 and sum of percentages must be equal to 100");
        }
    }
}
