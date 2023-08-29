package app.goal;

import app.diary.Diary;
import app.diary.ProductAddedToDiary;
import app.exceptions.InvalidInputException;
import app.user.User;
import app.user.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GoalServiceTest {
    @InjectMocks
    private GoalService goalService;
    @Mock
    private UserRepository userRepository;

    @Test
    void testGetGoal() {
        Authentication authentication = mock(Authentication.class);
        User user = User.builder().username("username").build();
        Diary diary = Diary
                .builder()
                .sumKcal(BigDecimal.valueOf(2000))
                .sumProtein(BigDecimal.valueOf(200))
                .sumCarbohydrates(BigDecimal.valueOf(200))
                .sumFat(BigDecimal.valueOf(200))
                .sumFiber(BigDecimal.valueOf(200))
                .goalKcal(BigDecimal.valueOf(2000))
                .goalProtein(BigDecimal.valueOf(200))
                .goalCarbohydrates(BigDecimal.valueOf(200))
                .goalFat(BigDecimal.valueOf(200))
                .goalFiber(BigDecimal.valueOf(200))
                .leftKcal(BigDecimal.valueOf(0))
                .leftProtein(BigDecimal.valueOf(0))
                .leftCarbohydrates(BigDecimal.valueOf(0))
                .leftFat(BigDecimal.valueOf(0))
                .leftFiber(BigDecimal.valueOf(0))
                .products(new ArrayList<>())
                .build();

        user.setDiary(diary);

        when(authentication.getName()).thenReturn(user.getUsername());
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));


        GoalResponseDto goalResponseDto = goalService.getGoal(authentication);

        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(authentication, times(1)).getName();

        Assertions.assertNotNull(goalResponseDto);
        assertEquals(BigDecimal.valueOf(2000), goalResponseDto.kcalGoal());
        assertEquals(BigDecimal.valueOf(200), goalResponseDto.proteinGoal());
        assertEquals(BigDecimal.valueOf(200), goalResponseDto.carbohydratesGoal());
        assertEquals(BigDecimal.valueOf(200), goalResponseDto.fatGoal());
        assertEquals(BigDecimal.valueOf(200), goalResponseDto.fiberGoal());
    }


    @Test
    void testGetGoal_UserExists() {
        User user = User.builder().username("username").build();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        Optional<User> optionalUser = userRepository.findByUsername(user.getUsername());
        assertTrue(optionalUser.isPresent());
    }

    @Test
    void testGetGoal_UserNotExists() {
        User user = User.builder().username("username").build();
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());
        Optional<User> optionalUser = userRepository.findByUsername(user.getUsername());
        assertTrue(optionalUser.isEmpty());
    }

    @Test
    void testCalculateNutrientsLeft() {
        Diary diary = Diary.builder()
                .goalKcal(BigDecimal.valueOf(1000))
                .goalProtein(BigDecimal.valueOf(100))
                .goalCarbohydrates(BigDecimal.valueOf(100))
                .goalFat(BigDecimal.valueOf(100))
                .goalFiber(BigDecimal.valueOf(100))
                .sumKcal(BigDecimal.valueOf(500))
                .sumProtein(BigDecimal.valueOf(50))
                .sumCarbohydrates(BigDecimal.valueOf(50))
                .sumFat(BigDecimal.valueOf(50))
                .sumFiber(BigDecimal.valueOf(50))
                .build();

        BigDecimal expectedLeftKcal = diary.getGoalKcal().subtract(diary.getSumKcal());
        BigDecimal expectedLeftProtein = diary.getGoalProtein().subtract(diary.getSumProtein());
        BigDecimal expectedLeftCarbohydrates = diary.getGoalCarbohydrates().subtract(diary.getSumCarbohydrates());
        BigDecimal expectedLeftFat = diary.getGoalFat().subtract(diary.getSumFat());
        BigDecimal expectedLeftFiber = diary.getGoalFiber().subtract(diary.getSumFiber());

        diary.calculateNutrientsLeft();

        assertAll(
                () -> assertEquals(expectedLeftKcal, diary.getLeftKcal()),
                () -> assertEquals(expectedLeftProtein, diary.getLeftProtein()),
                () -> assertEquals(expectedLeftCarbohydrates, diary.getLeftCarbohydrates()),
                () -> assertEquals(expectedLeftFat, diary.getLeftFat()),
                () -> assertEquals(expectedLeftFiber, diary.getLeftFiber()));

    }

    @Test
    void testCalculateNutrientsSum() {
        Diary diary = Diary.builder()
                .products(new ArrayList<>())
                .build();
        diary.getProducts().add(ProductAddedToDiary.builder()
                .kcal(BigDecimal.valueOf(100))
                .protein(BigDecimal.valueOf(10))
                .carbohydrates(BigDecimal.valueOf(10))
                .fat(BigDecimal.valueOf(10))
                .fiber(BigDecimal.valueOf(10))
                .build());
        diary.getProducts().add(ProductAddedToDiary.builder()
                .kcal(BigDecimal.valueOf(100))
                .protein(BigDecimal.valueOf(10))
                .carbohydrates(BigDecimal.valueOf(10))
                .fat(BigDecimal.valueOf(10))
                .fiber(BigDecimal.valueOf(10))
                .build());
        diary.getProducts().add(ProductAddedToDiary.builder()
                .kcal(BigDecimal.valueOf(100))
                .protein(BigDecimal.valueOf(10))
                .carbohydrates(BigDecimal.valueOf(10))
                .fat(BigDecimal.valueOf(10))
                .fiber(BigDecimal.valueOf(10))
                .build());
        diary.getProducts().add(ProductAddedToDiary.builder()
                .kcal(BigDecimal.valueOf(100))
                .protein(BigDecimal.valueOf(10))
                .carbohydrates(BigDecimal.valueOf(10))
                .fat(BigDecimal.valueOf(10))
                .fiber(BigDecimal.valueOf(10))
                .build());
        diary.getProducts().add(ProductAddedToDiary.builder()
                .kcal(BigDecimal.valueOf(100))
                .protein(BigDecimal.valueOf(10))
                .carbohydrates(BigDecimal.valueOf(10))
                .fat(BigDecimal.valueOf(10))
                .fiber(BigDecimal.valueOf(10))
                .build());

        BigDecimal expectedSumKcal = BigDecimal.valueOf(500.0);
        BigDecimal expectedSumProtein = BigDecimal.valueOf(50.0);
        BigDecimal expectedSumCarbohydrates = BigDecimal.valueOf(50.0);
        BigDecimal expectedSumFat = BigDecimal.valueOf(50.0);
        BigDecimal expectedSumFiber = BigDecimal.valueOf(50.0);

        diary.calculateNutrientsSum();

        assertAll(
                () -> assertEquals(expectedSumKcal, diary.getSumKcal()),
                () -> assertEquals(expectedSumProtein, diary.getSumProtein()),
                () -> assertEquals(expectedSumCarbohydrates, diary.getSumCarbohydrates()),
                () -> assertEquals(expectedSumFat, diary.getSumFat()),
                () -> assertEquals(expectedSumFiber, diary.getSumFiber()));

    }

    @Test
    void testSetGoal() {
        Authentication authentication = mock(Authentication.class);
        User user = User.builder().username("username").gender("M").build();
        Diary diary = Diary.builder()
                .goalKcal(BigDecimal.valueOf(2000))
                .goalProtein(BigDecimal.valueOf(100))
                .goalCarbohydrates(BigDecimal.valueOf(100))
                .goalFat(BigDecimal.valueOf(100))
                .goalFiber(BigDecimal.valueOf(100))
                .sumKcal(BigDecimal.valueOf(500))
                .sumProtein(BigDecimal.valueOf(50))
                .sumCarbohydrates(BigDecimal.valueOf(50))
                .sumFat(BigDecimal.valueOf(50))
                .sumFiber(BigDecimal.valueOf(50))
                .products(new ArrayList<>())
                .build();
        GoalDto goalDto = GoalDto.builder()
                .kcal(BigDecimal.valueOf(1000))
                .proteinPercentage(30)
                .carbohydratesPercentage(40)
                .fatPercentage(30)
                .build();

        BigDecimal expectedKcalGoal = goalDto.kcal();

        BigDecimal expectedProteinGoal = expectedKcalGoal
                .multiply(BigDecimal.valueOf(goalDto.proteinPercentage()))
                .divide(BigDecimal.valueOf(400), 2, RoundingMode.HALF_UP);

        BigDecimal expectedCarbohydratesGoal = expectedKcalGoal
                .multiply(BigDecimal.valueOf(goalDto.carbohydratesPercentage()))
                .divide(BigDecimal.valueOf(400), 2, RoundingMode.HALF_UP);

        BigDecimal expectedFatGoal = expectedKcalGoal
                .multiply(BigDecimal.valueOf(goalDto.fatPercentage()))
                .divide(BigDecimal.valueOf(900), 2, RoundingMode.HALF_UP);

        BigDecimal expectedFiberGoal = BigDecimal.valueOf(38);

        user.setDiary(diary);

        when(authentication.getName()).thenReturn(user.getUsername());
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        GoalResponseDto goalResponseDto = goalService.setGoal(authentication, goalDto);

        verify(userRepository, times(1)).findByUsername(user.getUsername());
        verify(authentication, times(1)).getName();

        assertAll(
                () -> assertEquals(expectedKcalGoal, goalResponseDto.kcalGoal()),
                () -> assertEquals(expectedProteinGoal, goalResponseDto.proteinGoal()),
                () -> assertEquals(expectedCarbohydratesGoal, goalResponseDto.carbohydratesGoal()),
                () -> assertEquals(expectedFatGoal, goalResponseDto.fatGoal()),
                () -> assertEquals(expectedFiberGoal, goalResponseDto.fiberGoal()));

    }

    @Test
    void testCountAndSetGoal() {
        GoalDto goalDto = GoalDto.builder()
                .kcal(BigDecimal.valueOf(2000))
                .proteinPercentage(25)
                .carbohydratesPercentage(50)
                .fatPercentage(25)
                .build();

        Diary diary = Diary
                .builder()
                .sumKcal(BigDecimal.valueOf(3000))
                .sumProtein(BigDecimal.valueOf(200))
                .sumCarbohydrates(BigDecimal.valueOf(200))
                .sumFat(BigDecimal.valueOf(200))
                .sumFiber(BigDecimal.valueOf(200))
                .goalKcal(BigDecimal.valueOf(1000))
                .goalProtein(BigDecimal.valueOf(200))
                .goalCarbohydrates(BigDecimal.valueOf(200))
                .goalFat(BigDecimal.valueOf(200))
                .goalFiber(BigDecimal.valueOf(200))
                .leftKcal(BigDecimal.valueOf(0))
                .leftProtein(BigDecimal.valueOf(0))
                .leftCarbohydrates(BigDecimal.valueOf(0))
                .leftFat(BigDecimal.valueOf(0))
                .leftFiber(BigDecimal.valueOf(0))
                .products(new ArrayList<>())
                .build();

        String gender = "F";

        BigDecimal expectedKcalGoal = goalDto.kcal();

        BigDecimal expectedProteinGoal = expectedKcalGoal
                .multiply(BigDecimal.valueOf(goalDto.proteinPercentage()))
                .divide(BigDecimal.valueOf(400), 2, RoundingMode.HALF_UP);

        BigDecimal expectedCarbohydratesGoal = expectedKcalGoal
                .multiply(BigDecimal.valueOf(goalDto.carbohydratesPercentage()))
                .divide(BigDecimal.valueOf(400), 2, RoundingMode.HALF_UP);

        BigDecimal expectedFatGoal = expectedKcalGoal
                .multiply(BigDecimal.valueOf(goalDto.fatPercentage()))
                .divide(BigDecimal.valueOf(900), 2, RoundingMode.HALF_UP);

        BigDecimal expectedFiberGoal = BigDecimal.valueOf(25);

        goalService.countAndSetGoal(goalDto, diary, gender);

        assertAll(
                () -> assertEquals(expectedKcalGoal, diary.getGoalKcal()),
                () -> assertEquals(expectedProteinGoal, diary.getGoalProtein()),
                () -> assertEquals(expectedCarbohydratesGoal, diary.getGoalCarbohydrates()),
                () -> assertEquals(expectedFatGoal, diary.getGoalFat()),
                () -> assertEquals(expectedFiberGoal, diary.getGoalFiber()));

        goalService.countAndSetGoal(goalDto, diary, "M");
        assertEquals(BigDecimal.valueOf(38), diary.getGoalFiber());
    }

    @Test
    void testValidateGoalDto_throwsException() {

        assertThrows(InvalidInputException.class, () -> goalService.validateGoalDto(GoalDto.builder()
                .kcal(BigDecimal.valueOf(0))
                .proteinPercentage(30)
                .carbohydratesPercentage(40)
                .fatPercentage(30)
                .build()));

        assertThrows(InvalidInputException.class, () -> goalService.validateGoalDto(GoalDto.builder()
                .kcal(BigDecimal.valueOf(1000))
                .proteinPercentage(35)
                .carbohydratesPercentage(40)
                .fatPercentage(30)
                .build()));
    }

    @Test
    void testValidateGoalDto_DoesntThrowException() {
        assertDoesNotThrow(() -> goalService.validateGoalDto(GoalDto.builder()
                .kcal(BigDecimal.valueOf(1000))
                .proteinPercentage(30)
                .carbohydratesPercentage(40)
                .fatPercentage(30)
                .build()));
    }
}
