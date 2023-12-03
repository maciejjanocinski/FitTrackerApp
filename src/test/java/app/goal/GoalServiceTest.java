//package app.goal;
//
//import app.diary.Diary;
//import app.user.User;
//import app.user.UserService;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.core.Authentication;
//
//import java.math.BigDecimal;
//
//import static app.utils.TestUtils.USERNAME;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class GoalServiceTest {
//    @Mock
//    private  GoalMapper goalMapper;
//    @Mock
//    private  Authentication authentication;
//    @Mock
//    private Diary diary;
//    @InjectMocks
//    private GoalService goalService;
//
//    @Mock
//    UserService userService;
//
//    @Test
//    void getGoal_inputDataOk() {
//        //given
//        GoalResponseDto expectedResponse = buildGoalResponseDto();
//        User user = User.builder()
//                .username(USERNAME)
//                .diary(diary)
//                .build();
//
//        when(authentication.getName()).thenReturn(USERNAME);
//        when(userService.getUserByUsername(USERNAME)).thenReturn(user);
//        when(goalMapper.mapToGoalResponseDto(diary)).thenReturn(expectedResponse);
//
//        //when
//        GoalResponseDto goalResponseDto = goalService.getGoal(authentication);
//
//        //then
//        verify(authentication).getName();
//        verify(userService).getUserByUsername(USERNAME);
//        verify(diary).calculateNutrientsLeft();
//        verify(diary).calculateNutrientsSum();
//        verify(goalMapper).mapToGoalResponseDto(diary);
//
//        assertEquals(expectedResponse, goalResponseDto);
//    }
//    @Test
//    void setGoal_inputDataOk() {
//        //given
//        GoalResponseDto expectedResponse = buildGoalResponseDto();
//        GoalDto goalDto = buildGoalDto();
//        User user = User.builder()
//                .username(USERNAME)
//                .gender(User.setGenderFromString("FEMALE"))
//                .diary(diary)
//                .build();
//        when(authentication.getName()).thenReturn(USERNAME);
//        when(userService.getUserByUsername(USERNAME)).thenReturn(user);
//        when(diary.setGoal(goalDto, user.getGender())).thenReturn(diary);
//        when(goalMapper.mapToGoalResponseDto(any(Diary.class))).thenReturn(expectedResponse);
//
//        //when
//        GoalResponseDto goalResponseDto = goalService.setGoal(authentication, goalDto);
//
//        //then
//        verify(authentication).getName();
//        verify(diary).setGoal(goalDto, user.getGender());
//        verify(userService).getUserByUsername(USERNAME);
//        assertEquals(expectedResponse, goalResponseDto);
//    }
//
//
//    private GoalDto buildGoalDto() {
//        return GoalDto.builder()
//                .kcal(BigDecimal.valueOf(1000))
//                .proteinPercentage(30)
//                .carbohydratesPercentage(25)
//                .fatPercentage(45)
//                .build();
//    }
//
//
//    private GoalResponseDto buildGoalResponseDto() {
//        return GoalResponseDto.builder()
//                .kcalGoal(BigDecimal.valueOf(1000))
//                .proteinInGram(BigDecimal.valueOf(75))
//                .carbohydratesInGram(BigDecimal.valueOf(63))
//                .fatInGram(BigDecimal.valueOf(50))
//                .fiberInGram(BigDecimal.valueOf(25))
//                .build();
//    }
//}