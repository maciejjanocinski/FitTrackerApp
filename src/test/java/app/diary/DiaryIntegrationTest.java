package app.diary;

import app.authentication.TokenService;
import app.diary.dto.AddProductToDiaryDto;
import app.diary.dto.DeleteProductDto;
import app.diary.dto.EditProductInDiaryDto;
import app.product.Measure;
import app.product.Product;
import app.product.ProductRepository;
import app.user.User;
import app.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static app.utils.TestUtils.buildUser;
import static app.utils.TestUtils.generateAuthorizationHeader;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.parseMediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@NoArgsConstructor
@AutoConfigureMockMvc
public class DiaryIntegrationTest {

    @LocalServerPort
    @Value("${app.test.port}")
    private int port;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

//    @Autowired
//    RecipeRepository recipeRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User user;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        productRepository.deleteAll();

        user = buildUser(passwordEncoder);

        Product product = buildProduct();
        userRepository.save(user);
        productRepository.save(product);
        user.getLastSearchedProducts().add(product);
        setProductInDiary(user.getDiary(), product);

//        recipeRepository.deleteAll();
//        Recipe recipe = buildRecipe();
//        user.setLastSearchedRecipes(List.of(recipe));
//        recipeRepository.save(recipe);

    }

    @Test
    void getDiary() throws Exception {
        //when
        mockMvc.perform(get("http://localhost:" + port + "/diary/")
                        .header("Authorization", generateAuthorizationHeader(
                                tokenService,
                                user.getAuthorities(),
                                user.getUsername()))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.sumKcal")
                        .value(BigDecimal.valueOf(100).setScale(1, RoundingMode.HALF_UP)))
                .andDo(print());

        Diary diaryAfterGet = userRepository.findByUsername(user.getUsername()).get().getDiary();
        assertEquals(diaryAfterGet.getDiaryId(), user.getDiary().getDiaryId());
    }

    @Test
    void addProductToDiary() throws Exception {
        AddProductToDiaryDto addProductToDiaryDto = buildAddProductToDiaryDto();

        mockMvc.perform(post("http://localhost:" + port + "/diary/product")
                        .header("Authorization", generateAuthorizationHeader(
                                tokenService,
                                user.getAuthorities(),
                                user.getUsername())
                        )
                        .content(objectMapper.writeValueAsString(addProductToDiaryDto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.quantity")
                        .value(BigDecimal.valueOf(1).setScale(1, RoundingMode.HALF_UP)))
                .andDo(print());

        Diary diary = userRepository.findByUsername(user.getUsername()).get().getDiary();

        assertEquals(1, diary.getProductsInDiary().size());
    }

    @Test
    void editProductAmountInDiary() throws Exception {

        EditProductInDiaryDto editProductInDiaryDto = buildEditProductInDiaryDto();

        mockMvc.perform(patch("http://localhost:" + port + "/diary/product")
                        .header("Authorization", generateAuthorizationHeader(
                                tokenService,
                                user.getAuthorities(),
                                user.getUsername())
                        )
                        .content(objectMapper.writeValueAsString(editProductInDiaryDto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.quantity")
                        .value(BigDecimal.valueOf(200).setScale(1, RoundingMode.HALF_UP)))
                .andDo(print());

        Diary diary = userRepository.findByUsername(user.getUsername()).get().getDiary();
        assertEquals(
                diary.getProductsInDiary().get(0).getQuantity(),
                BigDecimal.valueOf(200).setScale(2, RoundingMode.HALF_UP)
        );

    }

    @Test
    void deleteProductFromDiary() throws Exception {
        DeleteProductDto deleteProductDto = buildDeleteProductDto();
        mockMvc.perform(delete("http://localhost:" + port + "/diary/product")
                        .header("Authorization", generateAuthorizationHeader(
                                tokenService,
                                user.getAuthorities(),
                                user.getUsername())
                        )
                        .content(objectMapper.writeValueAsString(deleteProductDto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(parseMediaType("text/plain;charset=UTF-8")))
                .andExpect(content().string("Product deleted from diary successfully"))
                .andDo(print());

        Diary diaryAfterDelete = userRepository.findByUsername(user.getUsername()).get().getDiary();
        assertEquals(0, diaryAfterDelete.getProductsInDiary().size());
    }


    private  Product buildProduct() {
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

//    private Recipe buildRecipe() {
//        Recipe recipe = Recipe.builder()
//                .id(1L)
//                .label("label")
//                .image("image")
//                .source("source")
//                .url("url")
//                .yield(1)
//                .caloriesPerServing(1)
//                .proteinPerServing(1)
//                .carbsPerServing(1)
//                .fatPerServing(1)
//                .fiberPerServing(1)
//                .isUsed(false)
//                .query("query")
//                .user(user)
//                .build();
//        IngredientLine ingredientLine = IngredientLine.builder()
//                .text("ingredientLine")
//                .recipe(recipe)
//                .build();
//        recipe.setIngredientLines(List.of(ingredientLine));
//        return recipe;
//    }

    private AddProductToDiaryDto buildAddProductToDiaryDto() {
        return AddProductToDiaryDto.builder()
                .id(1L)
                .measureLabel("Gram")
                .quantity(BigDecimal.valueOf(1))
                .build();
    }

    private EditProductInDiaryDto buildEditProductInDiaryDto() {
        return EditProductInDiaryDto.builder()
                .id(1L)
                .measureLabel("Gram")
                .quantity(BigDecimal.valueOf(200))
                .build();
    }

    private DeleteProductDto buildDeleteProductDto() {
        return DeleteProductDto.builder()
                .id(1L)
                .build();
    }

    private void setProductInDiary(Diary diary, Product product) {
        ProductInDiary productInDiary = diary.generateNewProductInDiary(
                product,
                "Gram",
                BigDecimal.valueOf(100)
        );
        diary.addProduct(productInDiary);
    }


}
