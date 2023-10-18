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
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Diary diary = user.getDiary().setGoal(goalDto, user.getGender());
        //TODO change gender type to enum
        return goalMapper.mapToGoalResponseDto(diary);
    }
}
