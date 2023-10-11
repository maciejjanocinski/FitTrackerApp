package app.goal;

import app.diary.Diary;
import app.exceptions.InvalidInputException;
import app.user.User;
import app.user.UserRepository;
import lombok.RequiredArgsConstructor;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
class GoalServiceTest {

    @InjectMocks
    private  GoalService goalService;
    @Mock
    private  UserRepository userRepository;
    @Mock
    private GoalMapper goalMapper;
    @Mock
    private  Authentication authentication;
    @Mock
    private  Diary diary;

    @Test
    void getGoal_inputDataOk() {
        //given
        String username = "username";
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
        String username = "username";

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
        String username = "username";
        GoalResponseDto expectedResponse = buildGoalResponseDto();
        GoalDto goalDto = buildGoalDto();
        User user = User.builder()
                .username(username)
                .gender("F")
                .diary(diary)
                .build();

        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(goalMapper.mapToGoalResponseDto(diary)).thenReturn(expectedResponse);

        //when
        GoalResponseDto goalResponseDto = goalService.setGoal(authentication, goalDto);

        //then
        verify(authentication).getName();
        verify(userRepository).findByUsername(username);
        verify(diary).calculateNutrientsLeft();
        verify(diary).calculateNutrientsSum();
        verify(goalMapper).mapToGoalResponseDto(diary);

        assertEquals(expectedResponse, goalResponseDto);
    }

    @Test
    void setGoal_throwsException() {
        //given
        String username = "username";
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

    @Test
    void countGoal_male() {
        //given
        GoalDto goalDto = buildGoalDto();
        GoalValuesObj expectedGoalValuesObj = buildGoalValuesObjForMale();

        //when
        GoalValuesObj goalValuesObj = goalService.countGoal(goalDto, "M");

        //then
        assertEquals(expectedGoalValuesObj, goalValuesObj);
    }

    @Test
    void countGoal_female() {
        //given
        GoalDto goalDto = buildGoalDto();
        GoalValuesObj expectedGoalValuesObj = buildGoalValuesObjForFemale();

        //when
        GoalValuesObj goalValuesObj = goalService.countGoal(goalDto, "F");

        //then
        assertEquals(expectedGoalValuesObj, goalValuesObj);
    }

    @Test
    void setGoalValuesToDiary() {
        //given
        GoalValuesObj goalValuesObj = buildGoalValuesObjForMale();

        //when
        goalService.setGoalValuesToDiary(goalValuesObj, diary);

        //then
        verify(diary).setGoalKcal(goalValuesObj.kcal());
        verify(diary).setGoalProtein(goalValuesObj.protein());
        verify(diary).setGoalCarbohydrates(goalValuesObj.carbohydrates());
        verify(diary).setGoalFat(goalValuesObj.fat());
        verify(diary).setGoalFiber(goalValuesObj.fiber());
    }

    @Test
    void validateGoalDto_inputDataOk() {
        //given
        GoalDto goalDto = buildGoalDto();

        //when
        goalService.validateGoalDto(goalDto);

        //then
        assertDoesNotThrow(() -> goalService.validateGoalDto(buildGoalDto()));
        assertTrue(goalDto.kcal().compareTo(BigDecimal.ZERO) >= 0);
        assertEquals(
                100,
                goalDto.proteinPercentage() + goalDto.carbohydratesPercentage() + goalDto.fatPercentage()
        );
    }

    @Test
    void validateGoalDto_throwsException() {
        String expectedMessage = "Kcal must be greater than 0 and sum of percentages must be equal to 100";

        //kcal = 0
        Exception ex1 = assertThrows(InvalidInputException.class,
                () -> goalService.validateGoalDto(GoalDto.builder()
                        .kcal(BigDecimal.valueOf(0))
                        .proteinPercentage(30)
                        .carbohydratesPercentage(40)
                        .fatPercentage(30)
                        .build()));

        //sum of percentages != 100
        Exception ex2 = assertThrows(InvalidInputException.class,
                () -> goalService.validateGoalDto(GoalDto.builder()
                        .kcal(BigDecimal.valueOf(1000))
                        .proteinPercentage(35)
                        .carbohydratesPercentage(40)
                        .fatPercentage(30)
                        .build()));

        //both
        Exception ex3 = assertThrows(InvalidInputException.class,
                () -> goalService.validateGoalDto(GoalDto.builder()
                        .kcal(BigDecimal.valueOf(0))
                        .proteinPercentage(35)
                        .carbohydratesPercentage(40)
                        .fatPercentage(30)
                        .build()));

        assertEquals(expectedMessage, ex1.getMessage());
        assertEquals(expectedMessage, ex2.getMessage());
        assertEquals(expectedMessage, ex3.getMessage());
    }

    private GoalDto buildGoalDto() {
        return GoalDto.builder()
                .kcal(BigDecimal.valueOf(1000))
                .proteinPercentage(30)
                .carbohydratesPercentage(25)
                .fatPercentage(45)
                .build();
    }

    private GoalValuesObj buildGoalValuesObjForMale() {
        return GoalValuesObj.builder()
                .kcal(BigDecimal.valueOf(1000))
                .protein(BigDecimal.valueOf(75).setScale(2, RoundingMode.HALF_UP))
                .carbohydrates(BigDecimal.valueOf(62.5).setScale(2, RoundingMode.HALF_UP))
                .fat(BigDecimal.valueOf(50).setScale(2, RoundingMode.HALF_UP))
                .fiber(BigDecimal.valueOf(38))
                .build();
    }

    private GoalValuesObj buildGoalValuesObjForFemale() {
        return GoalValuesObj.builder()
                .kcal(BigDecimal.valueOf(1000))
                .protein(BigDecimal.valueOf(75).setScale(2, RoundingMode.HALF_UP))
                .carbohydrates(BigDecimal.valueOf(62.5).setScale(2, RoundingMode.HALF_UP))
                .fat(BigDecimal.valueOf(50).setScale(2, RoundingMode.HALF_UP))
                .fiber(BigDecimal.valueOf(25))
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