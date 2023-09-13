package app.goal;

import app.diary.Diary;
import app.diary.ProductAddedToDiary;
import app.exceptions.InvalidInputException;
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
    void inputDataOk_getGoal() {
        //given
        Authentication authentication = mock(Authentication.class);
        User user = User.builder().username("username").build();
        Diary diary = buildDiary();
        user.setDiary(diary);

        //when
        when(authentication.getName()).thenReturn(user.getUsername());
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        //then
        GoalResponseDto goalResponseDto = goalService.getGoal(authentication);
        verify(userRepository).findByUsername(user.getUsername());
        verify(authentication).getName();

        assertNotNull(goalResponseDto);
        assertEquals(BigDecimal.valueOf(2000), goalResponseDto.kcalGoal());
        assertEquals(BigDecimal.valueOf(150), goalResponseDto.proteinInGram());
        assertEquals(BigDecimal.valueOf(200), goalResponseDto.carbohydratesInGram());
        assertEquals(BigDecimal.valueOf(100), goalResponseDto.fatInGram());
        assertEquals(BigDecimal.valueOf(10), goalResponseDto.fiberInGram());
    }

    @Test
    void userNotExists_getGoal() {
        //given
        User user = User.builder().username("username").build();
        Authentication authentication = mock(Authentication.class);
        //when
        when(authentication.getName()).thenReturn(user.getUsername());
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());

        //then
        Optional<User> optionalUser = userRepository.findByUsername(user.getUsername());
        verify(userRepository).findByUsername(user.getUsername());

        assertTrue(optionalUser.isEmpty());
        assertThrows(UsernameNotFoundException.class, () -> goalService.getGoal(authentication));
    }

    @Test
    void inputDataOk_setGoal() {
        //given
        Diary diary = buildDiary();
        GoalDto goalDto = buildGoalDto();
        User user = User.builder().username("username").gender("M").diary(diary).build();
        Authentication authentication = mock(Authentication.class);

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


        //when
        when(authentication.getName()).thenReturn(user.getUsername());
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        //then
        GoalResponseDto goalResponseDto = goalService.setGoal(authentication, goalDto);

        verify(userRepository).findByUsername(user.getUsername());
        verify(authentication).getName();

        assertEquals(expectedKcalGoal, goalResponseDto.kcalGoal());
        assertEquals(expectedProteinGoal, goalResponseDto.proteinInGram());
        assertEquals(expectedCarbohydratesGoal, goalResponseDto.carbohydratesInGram());
        assertEquals(expectedFatGoal, goalResponseDto.fatInGram());
        assertEquals(expectedFiberGoal, goalResponseDto.fiberInGram());
    }


    @Test
    void testCalculateNutrientsLeft() {
        //given
        Diary diary = buildDiary();
        BigDecimal expectedLeftKcal = diary.getGoalKcal().subtract(diary.getSumKcal());
        BigDecimal expectedLeftProtein = diary.getGoalProtein().subtract(diary.getSumProtein());
        BigDecimal expectedLeftCarbohydrates = diary.getGoalCarbohydrates().subtract(diary.getSumCarbohydrates());
        BigDecimal expectedLeftFat = diary.getGoalFat().subtract(diary.getSumFat());
        BigDecimal expectedLeftFiber = diary.getGoalFiber().subtract(diary.getSumFiber());

        //when
        diary.calculateNutrientsLeft();

        //then
        assertEquals(expectedLeftKcal, diary.getLeftKcal());
        assertEquals(expectedLeftProtein, diary.getLeftProtein());
        assertEquals(expectedLeftCarbohydrates, diary.getLeftCarbohydrates());
        assertEquals(expectedLeftFat, diary.getLeftFat());
        assertEquals(expectedLeftFiber, diary.getLeftFiber());

    }

    @Test
    void testCalculateNutrientsSum() {
        //given
        Diary diary = buildDiary();
        addProductsToDiary(diary);
        BigDecimal expectedSumKcal = BigDecimal.valueOf(500.0);
        BigDecimal expectedSumProtein = BigDecimal.valueOf(50.0);
        BigDecimal expectedSumCarbohydrates = BigDecimal.valueOf(50.0);
        BigDecimal expectedSumFat = BigDecimal.valueOf(50.0);
        BigDecimal expectedSumFiber = BigDecimal.valueOf(50.0);

        //when
        diary.calculateNutrientsSum();

        //then
        assertEquals(expectedSumKcal, diary.getSumKcal());
        assertEquals(expectedSumProtein, diary.getSumProtein());
        assertEquals(expectedSumCarbohydrates, diary.getSumCarbohydrates());
        assertEquals(expectedSumFat, diary.getSumFat());
        assertEquals(expectedSumFiber, diary.getSumFiber());

    }


    @Test
    void testCountAndSetGoal() {
        //given
        GoalDto goalDto = buildGoalDto();
        Diary diary = buildDiary();

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

        //then
        goalService.countAndSetGoal(goalDto, diary, "F");

        assertEquals(expectedKcalGoal, diary.getGoalKcal());
        assertEquals(expectedProteinGoal, diary.getGoalProtein());
        assertEquals(expectedCarbohydratesGoal, diary.getGoalCarbohydrates());
        assertEquals(expectedFatGoal, diary.getGoalFat());
        assertEquals(expectedFiberGoal, diary.getGoalFiber());

        goalService.countAndSetGoal(goalDto, diary, "M");
        assertEquals(expectedKcalGoal, diary.getGoalKcal());
        assertEquals(expectedProteinGoal, diary.getGoalProtein());
        assertEquals(expectedCarbohydratesGoal, diary.getGoalCarbohydrates());
        assertEquals(expectedFatGoal, diary.getGoalFat());
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
        assertDoesNotThrow(() -> goalService.validateGoalDto(buildGoalDto()));
    }

    Diary buildDiary() {
        return Diary
                .builder()
                .sumKcal(BigDecimal.valueOf(2000))
                .sumProtein(BigDecimal.valueOf(200))
                .sumCarbohydrates(BigDecimal.valueOf(200))
                .sumFat(BigDecimal.valueOf(200))
                .sumFiber(BigDecimal.valueOf(200))
                .goalKcal(BigDecimal.valueOf(2000))
                .goalProtein(BigDecimal.valueOf(150))
                .goalCarbohydrates(BigDecimal.valueOf(200))
                .goalFat(BigDecimal.valueOf(100))
                .goalFiber(BigDecimal.valueOf(10))
                .leftKcal(BigDecimal.valueOf(0))
                .leftProtein(BigDecimal.valueOf(0))
                .leftCarbohydrates(BigDecimal.valueOf(0))
                .leftFat(BigDecimal.valueOf(0))
                .leftFiber(BigDecimal.valueOf(0))
                .products(new ArrayList<>())
                .build();
    }

    GoalDto buildGoalDto() {
        return GoalDto.builder()
                .kcal(BigDecimal.valueOf(1000))
                .proteinPercentage(30)
                .carbohydratesPercentage(40)
                .fatPercentage(30)
                .build();
    }

    void addProductsToDiary(Diary diary) {

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
    }
}
