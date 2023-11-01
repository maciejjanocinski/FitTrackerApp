package app.diary;

import app.goal.GoalDto;
import app.goal.GoalValues;
import app.util.exceptions.InvalidInputException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DiaryTest {

    private final Diary diary = new Diary();

    @Test
    void addProduct() {
        //given
        ProductInDiary product = ProductInDiary.builder()
                .kcal(BigDecimal.valueOf(100))
                .protein(BigDecimal.valueOf(10))
                .carbohydrates(BigDecimal.valueOf(20))
                .fat(BigDecimal.valueOf(30))
                .fiber(BigDecimal.valueOf(40))
                .build();

        int expectedSize = 1;

        //when
        diary.addProduct(product);

        //then
        assertEquals(expectedSize, diary.getProducts().size());
    }

    @Test
    void calculateNutrientsSum() {
        // given
        addProductsToDiary();

        // when
        diary.calculateNutrientsSum();

        // then
        checkEqualityOfNutrientsSum();
    }

    @Test
    void calculateNutrientsLeft() {
        //given
        addProductsToDiary();
        setGoals();
        diary.calculateNutrientsSum();

        // when
        diary.calculateNutrientsLeft();

        // then
        checkEqualityOfNutrientsSum();
    }

    @Test
    void validateGoalDto_inputDataOk() {
        //given
        GoalDto goalDto = buildGoalDto();

        //when
        diary.validateGoalDto(goalDto);

        //then
        assertDoesNotThrow(() -> diary.validateGoalDto(buildGoalDto()));
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
                () -> diary.validateGoalDto(GoalDto.builder()
                        .kcal(BigDecimal.valueOf(0))
                        .proteinPercentage(30)
                        .carbohydratesPercentage(40)
                        .fatPercentage(30)
                        .build()));

        //sum of percentages != 100
        Exception ex2 = assertThrows(InvalidInputException.class,
                () -> diary.validateGoalDto(GoalDto.builder()
                        .kcal(BigDecimal.valueOf(1000))
                        .proteinPercentage(35)
                        .carbohydratesPercentage(40)
                        .fatPercentage(30)
                        .build()));

        //both
        Exception ex3 = assertThrows(InvalidInputException.class,
                () -> diary.validateGoalDto(GoalDto.builder()
                        .kcal(BigDecimal.valueOf(0))
                        .proteinPercentage(35)
                        .carbohydratesPercentage(40)
                        .fatPercentage(30)
                        .build()));

        assertEquals(expectedMessage, ex1.getMessage());
        assertEquals(expectedMessage, ex2.getMessage());
        assertEquals(expectedMessage, ex3.getMessage());
    }

    @Test
    void countGoal_male() {
        //given
        GoalDto goalDto = buildGoalDto();
        GoalValues expectedGoalValues = buildGoalValuesForMale();

        //when
        GoalValues goalValues = diary.countGoal(goalDto, Gender.MALE);

        //then
        assertEquals(expectedGoalValues, goalValues);
    }

    @Test
    void countGoal_female() {
        //given
        GoalDto goalDto = buildGoalDto();
        GoalValues expectedGoalValues = buildGoalValuesForFemale();

        //when
        GoalValues goalValues = diary.countGoal(goalDto, Gender.FEMALE);

        //then
        assertEquals(expectedGoalValues, goalValues);
    }

    @Test
    void setGoalValuesToDiary() {
        //given
        GoalValues goalValues = buildGoalValuesForMale();

        //when
        diary.setGoalValuesToDiary(goalValues);

        //then
        assertEquals(goalValues.kcal(), diary.getGoalKcal());
        assertEquals(goalValues.protein(), diary.getGoalProtein());
        assertEquals(goalValues.carbohydrates(), diary.getGoalCarbohydrates());
        assertEquals(goalValues.fat(), diary.getGoalFat());
        assertEquals(goalValues.fiber(), diary.getGoalFiber());
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

    private GoalValues buildGoalValuesForFemale() {
        return GoalValues.builder()
                .kcal(BigDecimal.valueOf(1000))
                .protein(BigDecimal.valueOf(75).setScale(2, RoundingMode.HALF_UP))
                .carbohydrates(BigDecimal.valueOf(62.5).setScale(2, RoundingMode.HALF_UP))
                .fat(BigDecimal.valueOf(50).setScale(2, RoundingMode.HALF_UP))
                .fiber(BigDecimal.valueOf(25))
                .build();
    }

    private GoalDto buildGoalDto() {
        return GoalDto.builder()
                .kcal(BigDecimal.valueOf(1000))
                .proteinPercentage(30)
                .carbohydratesPercentage(25)
                .fatPercentage(45)
                .build();
    }

    private void checkEqualityOfNutrientsSum() {
        assertEquals(BigDecimal.valueOf(500).setScale(1, RoundingMode.HALF_UP), diary.getSumKcal());
        assertEquals(BigDecimal.valueOf(50).setScale(1, RoundingMode.HALF_UP), diary.getSumProtein());
        assertEquals(BigDecimal.valueOf(100).setScale(1, RoundingMode.HALF_UP), diary.getSumCarbohydrates());
        assertEquals(BigDecimal.valueOf(150).setScale(1, RoundingMode.HALF_UP), diary.getSumFat());
        assertEquals(BigDecimal.valueOf(200).setScale(1, RoundingMode.HALF_UP), diary.getSumFiber());
    }

    private void setGoals() {
        diary.setGoalKcal(BigDecimal.valueOf(1000));
        diary.setGoalProtein(BigDecimal.valueOf(100));
        diary.setGoalCarbohydrates(BigDecimal.valueOf(200));
        diary.setGoalFat(BigDecimal.valueOf(300));
        diary.setGoalFiber(BigDecimal.valueOf(400));
    }

    private void addProductsToDiary() {
        ProductInDiary product = ProductInDiary.builder()
                .kcal(BigDecimal.valueOf(100))
                .protein(BigDecimal.valueOf(10))
                .carbohydrates(BigDecimal.valueOf(20))
                .fat(BigDecimal.valueOf(30))
                .fiber(BigDecimal.valueOf(40))
                .build();

        for (int i = 0; i < 5; i++) {
            diary.addProduct(product);
        }
    }
}