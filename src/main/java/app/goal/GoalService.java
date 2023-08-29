package app.goal;

import app.diary.Diary;
import app.exceptions.InvalidInputException;
import app.user.User;
import app.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Service
@RequiredArgsConstructor
class GoalService {

    private final UserRepository userRepository;
    private final GoalMapper goalMapper;

    GoalResponseDto getGoal(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Diary diary = user.getDiary();
        diary.calculateNutrientsLeft();
        diary.calculateNutrientsSum();

        return goalMapper.INSTANCE.mapToGoalResponseDto(diary);
    }

    @Transactional
    public GoalResponseDto setGoal(Authentication authentication, GoalDto goalsDto) {
        validateGoalDto(goalsDto);
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Diary diary = user.getDiary();

        countAndSetGoal(goalsDto, diary, user.getGender());
        diary.calculateNutrientsLeft();
        diary.calculateNutrientsSum();

        return goalMapper.INSTANCE.mapToGoalResponseDto(diary);
    }

     void countAndSetGoal(GoalDto goalDto, Diary diary, String gender) {
        BigDecimal protein = goalDto.kcal().multiply(BigDecimal.valueOf(goalDto.proteinPercentage())).divide(BigDecimal.valueOf(400), 2, RoundingMode.HALF_UP);
        BigDecimal carbohydrates = goalDto.kcal().multiply(BigDecimal.valueOf(goalDto.carbohydratesPercentage())).divide(BigDecimal.valueOf(400), 2, RoundingMode.HALF_UP);
        BigDecimal fat = goalDto.kcal().multiply(BigDecimal.valueOf(goalDto.fatPercentage())).divide(BigDecimal.valueOf(900), 2, RoundingMode.HALF_UP);
        BigDecimal fiber = Objects.equals(gender, "M") ? BigDecimal.valueOf(38) : BigDecimal.valueOf(25);

        diary.setGoalKcal(goalDto.kcal());
        diary.setGoalProtein(protein);
        diary.setGoalCarbohydrates(carbohydrates);
        diary.setGoalFat(fat);
        diary.setGoalFiber(fiber);
    }

     void validateGoalDto(GoalDto goalDto) {
        if (goalDto.kcal().compareTo(BigDecimal.valueOf(0)) < 1 ||
                goalDto.proteinPercentage() + goalDto.carbohydratesPercentage() + goalDto.fatPercentage() != 100) {

            throw new InvalidInputException("Kcal must be greater than 0 and sum of percentages must be equal to 100");
        }
    }
}
