package app.goal;

import app.diary.Diary;
import app.user.User;
import app.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final UserService userService;
    private final GoalMapper goalMapper;

    GoalResponseDto getGoal(Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        Diary diary = user.getDiary();
        diary.calculateNutrientsLeft();
        diary.calculateNutrientsSum();

        return goalMapper.mapToGoalResponseDto(diary);
    }

    @Transactional
    public GoalResponseDto setGoal(Authentication authentication, GoalDto goalDto) {
        User user = userService.getUserByUsername(authentication.getName());
        Diary diary = user.getDiary().setGoal(goalDto, user.getGender());
        //TODO change gender type to enum
        return goalMapper.mapToGoalResponseDto(diary);
    }
}
