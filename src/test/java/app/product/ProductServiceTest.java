package app.product;

import app.util.FoodApiManager;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;
    @Mock
    private Dotenv dotenv;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private FoodApiManager foodApiManager;
    @Mock
    private RestTemplate restTemplate;


    @Test
    void searchProducts_newQuery_returnsListOfProducts() {
        //given
        String query = "salad";
foodApiManager.setLastQuery("salad");
//        when(foodApiManager.getLastQuery()).thenReturn(null);
//        when(dotenv.get("PRODUCTS_API_KEY")).thenReturn("key");
//        when(dotenv.get("PRODUCTS_API_ID")).thenReturn("id");



        //when
        List<Product> actualProducts = productService.searchProducts(query);

        //then
        assertNotNull(actualProducts);
        assertEquals(3, actualProducts.size());
    }

    private List<Product> buildListOfProducts() {
        Product product1 = Product.builder()
                .id(1L)
                .productId("1")
                .name("Chicken")
                .kcal(BigDecimal.valueOf(100))
                .protein(BigDecimal.valueOf(100))
                .fat(BigDecimal.valueOf(100))
                .carbohydrates(BigDecimal.valueOf(100))
                .fiber(BigDecimal.valueOf(100))
                .image("image")
                .query("chicken")
                .build();

        Product product2 = Product.builder()
                .id(2L)
                .productId("2")
                .name("Chicken breast")
                .kcal(BigDecimal.valueOf(100))
                .protein(BigDecimal.valueOf(100))
                .fat(BigDecimal.valueOf(100))
                .carbohydrates(BigDecimal.valueOf(100))
                .fiber(BigDecimal.valueOf(100))
                .image("image")
                .query("chicken")
                .build();

        Product product3 = Product.builder()
                .id(3L)
                .productId("3")
                .name("Chicken nuggets")
                .kcal(BigDecimal.valueOf(100))
                .protein(BigDecimal.valueOf(100))
                .fat(BigDecimal.valueOf(100))
                .carbohydrates(BigDecimal.valueOf(100))
                .fiber(BigDecimal.valueOf(100))
                .image("image")
                .query("chicken")
                .build();

        return List.of(product1, product2, product3);
    }


}