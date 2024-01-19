package app.util;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDate;

@UtilityClass
public class Utils {

    public static final String USER_NOT_FOUND_MESSAGE = "User not found";
    public static final String PRODUCT_NOT_FOUND_MESSAGE = "Product not found";
    public static final String ROLE_NOT_FOUND_MESSAGE = "Role not found";
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
}
