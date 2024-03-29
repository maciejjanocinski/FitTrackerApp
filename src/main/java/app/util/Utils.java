package app.util;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDate;

@UtilityClass
public class Utils {

    public static final String USER_NOT_FOUND_MESSAGE = "User not found";
    public static final String PRODUCT_NOT_FOUND_MESSAGE = "Product not found";
    public static final String ROLE_NOT_FOUND_MESSAGE = "Role not found";
    public static final String MEASURE_NOT_FOUND_MESSAGE = "Measure not found";
    public static final String USER_ALREADY_EXISTS_MESSAGE = "User with this username or email already exists";
    public static final String WRONG_CREDENTIALS_MESSAGE = "Wrong login or password";
    public static final String RECIPE_ALREADY_ADDED_MESSAGE = "Recipe already added to favourites.";
    public static final String RECIPE_NOT_FOUND_MESSAGE = "Recipe already added to favourites.";
    public static final String VALIDATE_GOAL_MESSAGE = "Kcal must be greater than 0 and sum of percentages must be equal to 100";
    public static final String BALANCED = "balanced";
    public static final String LOW_FAT = "lowfat";
    public static final String LOW_CARBS = "lowcarbs";
    public static final String HIGH_PROTEIN = "highprotein";
    public static final String KCAL_API = "ENERC_KCAL";
    public static final String PROTEIN_API = "PROCNT";
    public static final String CARBS_API = "CHOCDF";
    public static final String FAT_API = "FAT";
    public static final String FIBER_API = "FIBTG";
    public static final int INTENSITY_LEVEL_MIN = 1;
    public static final int INTENSITY_LEVEL_MAX = 9;
    public static final int FIBER_MALE = 38;
    public static final int FIBER_FEMALE = 25;
    public static BigDecimal DEFAULT_HEIGHT = BigDecimal.valueOf(180);
    public static BigDecimal DEFAULT_WEIGHT = BigDecimal.valueOf(80);
    public static BigDecimal DEFAULT_NECK_SIZE = BigDecimal.valueOf(50);
    public static BigDecimal DEFAULT_WAIST_SIZE = BigDecimal.valueOf(100);
    public static BigDecimal DEFAULT_HIP_SIZE = BigDecimal.valueOf(120);
    public static LocalDate DEFAULT_BIRTH_DATE = LocalDate.of(2000, 1, 1);
    public static final String PORTION_MEASURE = "Potion";
    public static final int LASTLY_ADDED_PRODUCTS_LIMIT = 15;

}
