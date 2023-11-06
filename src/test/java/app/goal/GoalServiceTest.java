package app.goal;

import app.diary.Diary;
import app.user.User;
import app.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import static app.utils.TestUtils.username;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {
    @Mock
    private  UserRepository userRepository;
    @Mock
    private  GoalMapper goalMapper;
    @Mock
    private  Authentication authentication;
    @Mock
    private Diary diary;
    @InjectMocks
    private GoalService goalService;

    @Test
    void getGoal_inputDataOk() {
        //given
        GoalResponseDto expectedResponse = buildGoalResponseDto();
        User user = User.builder()
                .username(username)
                .diary(diary)
                .build();

        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(goalMapper.mapToGoalResponseDto(diary)).thenReturn(expectedResponse);

        //when
        GoalResponseDto goalResponseDto = goalService.getGoal(authentication);

        //then
        verify(authentication).getName();
        verify(userRepository).findByUsername(username);
        verify(diary).calculateNutrientsLeft();
        verify(diary).calculateNutrientsSum();
        verify(goalMapper).mapToGoalResponseDto(diary);

        assertEquals(expectedResponse, goalResponseDto);
    }

    @Test
    void getGoal_throwsException() {
        //given

        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        //when
        Exception ex = assertThrows(UsernameNotFoundException.class,
                () -> goalService.getGoal(authentication));

        //then
        verify(authentication).getName();
        verify(userRepository).findByUsername(username);
        verify(diary, never()).calculateNutrientsLeft();
        verify(diary, never()).calculateNutrientsSum();
        verify(goalMapper, never()).mapToGoalResponseDto(diary);

        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void setGoal_inputDataOk() {
        //given
        GoalResponseDto expectedResponse = buildGoalResponseDto();
        GoalDto goalDto = buildGoalDto();
        User user = User.builder()
                .username(username)
                .gender(User.setGenderFromString("FEMALE"))
                .diary(diary)
                .build();
        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(diary.setGoal(goalDto, user.getGender())).thenReturn(diary);
        when(goalMapper.mapToGoalResponseDto(any(Diary.class))).thenReturn(expectedResponse);

        //when
        GoalResponseDto goalResponseDto = goalService.setGoal(authentication, goalDto);

        //then
        verify(authentication).getName();
        verify(userRepository).findByUsername(username);
        verify(diary).setGoal(goalDto, user.getGender());
        assertEquals(expectedResponse, goalResponseDto);
    }

    @Test
    void setGoal_throwsException() {
        //given
        GoalDto goalDto = buildGoalDto();

        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        //when
        Exception ex = assertThrows(UsernameNotFoundException.class,
                () -> goalService.setGoal(authentication, goalDto));

        //then
        verify(authentication).getName();
        verify(userRepository).findByUsername(username);
        verify(diary, never()).calculateNutrientsLeft();
        verify(diary, never()).calculateNutrientsSum();
        verify(goalMapper, never()).mapToGoalResponseDto(diary);

        assertEquals("User not found", ex.getMessage());
    }


    private GoalDto buildGoalDto() {
        return GoalDto.builder()
                .kcal(BigDecimal.valueOf(1000))
                .proteinPercentage(30)
                .carbohydratesPercentage(25)
                .fatPercentage(45)
                .build();
    }

    private GoalValues buildGoalValuesForMale() {
        return GoalValues.builder()
                .kcal(BigDecimal.valueOf(1000))
                .protein(BigDecimal.valueOf(75).setScale(2, RoundingMode.HALF_UP))
                .carbohydrates(BigDecimal.valueOf(62.5).setScale(2, RoundingMode.HALF_UP))
                .fat(BigDecimal.valueOf(50).setScale(2, RoundingMode.HALF_UP))
                .fiber(BigDecimal.valueOf(38))
                .build();
    }

    private GoalResponseDto buildGoalResponseDto() {
        return GoalResponseDto.builder()
                .kcalGoal(BigDecimal.valueOf(1000))
                .proteinInGram(BigDecimal.valueOf(75))
                .carbohydratesInGram(BigDecimal.valueOf(63))
                .fatInGram(BigDecimal.valueOf(50))
                .fiberInGram(BigDecimal.valueOf(25))
                .build();
    }
}