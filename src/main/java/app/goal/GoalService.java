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

        return goalMapper.mapToGoalResponseDto(diary);
    }

    @Transactional
    public GoalResponseDto setGoal(Authentication authentication, GoalDto goalDto) {
        validateGoalDto(goalDto);
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Diary diary = user.getDiary();

        GoalValuesObj goalValuesObj = countGoal(goalDto, user.getGender());
        setGoalValuesToDiary(goalValuesObj, diary);
        diary.calculateNutrientsLeft();
        diary.calculateNutrientsSum();

        return goalMapper.mapToGoalResponseDto(diary);
    }

    GoalValuesObj countGoal(GoalDto goalDto, String gender) {
        BigDecimal protein = goalDto.kcal().multiply(BigDecimal.valueOf(goalDto.proteinPercentage())).divide(BigDecimal.valueOf(400), 2, RoundingMode.HALF_UP);
        BigDecimal carbohydrates = goalDto.kcal().multiply(BigDecimal.valueOf(goalDto.carbohydratesPercentage())).divide(BigDecimal.valueOf(400), 2, RoundingMode.HALF_UP);
        BigDecimal fat = goalDto.kcal().multiply(BigDecimal.valueOf(goalDto.fatPercentage())).divide(BigDecimal.valueOf(900), 2, RoundingMode.HALF_UP);
        BigDecimal fiber = Objects.equals(gender, "M") ? BigDecimal.valueOf(38) : BigDecimal.valueOf(25);

        return GoalValuesObj.builder()
                .kcal(goalDto.kcal())
                .protein(protein)
                .carbohydrates(carbohydrates)
                .fat(fat)
                .fiber(fiber)
                .build();
    }

    void setGoalValuesToDiary(GoalValuesObj goalValuesObj, Diary diary) {
    diary.setGoalKcal(goalValuesObj.kcal());
    diary.setGoalProtein(goalValuesObj.protein());
    diary.setGoalCarbohydrates(goalValuesObj.carbohydrates());
    diary.setGoalFat(goalValuesObj.fat());
    diary.setGoalFiber(goalValuesObj.fiber());
    }

    void validateGoalDto(GoalDto goalDto) {
        if (goalDto.kcal().compareTo(BigDecimal.valueOf(0)) < 1 ||
                goalDto.proteinPercentage() + goalDto.carbohydratesPercentage() + goalDto.fatPercentage() != 100) {
            throw new InvalidInputException("Kcal must be greater than 0 and sum of percentages must be equal to 100");
        }
    }
}
