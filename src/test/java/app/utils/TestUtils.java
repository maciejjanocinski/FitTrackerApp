package app.utils;

import app.authentication.Role;
import app.authentication.TokenService;
import app.diary.Diary;
import app.diary.ProductInDiary;
import app.product.Measure;
import app.product.Product;
import app.user.User;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static app.user.User.setGenderFromString;
public class TestUtils {
    public static final String USERNAME = "username";
    public static final String QUERY = "bread";
    public static final String USER_NOT_FOUND_MESSAGE = "User not found";
    public static final String PRODUCT_NOT_FOUND_MESSAGE = "Product not found";

    public static User buildUser(Set<Role> roles) {
        return User.builder()
                .username("username")
                .password("password124M!")
                .name("name")
                .surname("surname")
                .gender(setGenderFromString("MALE"))
                .email("maciek@gmial.com")
                .phone("123456789")
                .authorities(roles)
                .lastSearchedProducts(new ArrayList<>())
                .build();
    }

    public static User buildUser(Set<Role> roles, String password) {
        return User.builder()
                .username("username")
                .password(password)
                .name("name")
                .surname("surname")
                .gender(setGenderFromString("MALE"))
                .email("maciek@gmial.com")
                .phone("123456789")
                .authorities(roles)
                .lastSearchedProducts(new ArrayList<>())
                .build();
    }

    public static Role buildRoleStandard() {
        return Role.builder()
                .name("ROLE_USER_STANDARD")
                .build();
    }

    public static Diary buildDiary() {
        return Diary.builder()
                .goalKcal(BigDecimal.valueOf(1000))
                .goalCarbohydrates(BigDecimal.valueOf(100))
                .goalProtein(BigDecimal.valueOf(100))
                .goalFat(BigDecimal.valueOf(100))
                .goalFiber(BigDecimal.valueOf(100))
                .sumKcal(BigDecimal.valueOf(100))
                .sumCarbohydrates(BigDecimal.valueOf(100))
                .sumProtein(BigDecimal.valueOf(100))
                .sumFat(BigDecimal.valueOf(100))
                .sumFiber(BigDecimal.valueOf(100))
                .leftKcal(BigDecimal.valueOf(100))
                .leftCarbohydrates(BigDecimal.valueOf(100))
                .leftProtein(BigDecimal.valueOf(100))
                .leftFat(BigDecimal.valueOf(100))
                .leftFiber(BigDecimal.valueOf(100))
                .productsInDiary(new ArrayList<>())
                .build();
    }

    public static Product buildProduct(User user) {
        return Product.builder()
                .productId("foodId")
                .name("name")
                .kcal(BigDecimal.valueOf(1))
                .protein(BigDecimal.valueOf(1))
                .fat(BigDecimal.valueOf(1))
                .carbohydrates(BigDecimal.valueOf(1))
                .fiber(BigDecimal.valueOf(1))
                .image("image")
                .isUsed(false)
                .user(user)
                .query("query")
                .measures(List.of(
                        Measure.builder()
                                .name("Gram")
                                .weight(BigDecimal.valueOf(100))
                                .build()
                ))
                .build();
    }
}
