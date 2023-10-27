package app.product;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static app.utils.TestUtils.query;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class ProductTest {

    @Test
    void parseProductsFromResponseDto_inputDataOk() {
        // given
        int expectedProductsListSize = 4;
        int expectedMeasuresListSize = 2;
        BigDecimal expectedGram = BigDecimal.ONE;
        BigDecimal expectedKilogram = BigDecimal.valueOf(1000);

        // when
        List<Product> products = Product.parseProductsFromResponseDto(buildResponseDto(), query);

        // then
        assertEquals(expectedProductsListSize, products.size());
        assertEquals(query, products.get(0).getQuery());
        assertEquals(expectedMeasuresListSize, products.get(0).getMeasures().size());
        assertEquals(expectedGram, products.get(0).getMeasures().get("Gram"));
        assertEquals(expectedKilogram, products.get(0).getMeasures().get("Kilogram"));
    }

    @Test
    void parseProductsFromResponseDto_inputDataNull() {
        // given
        int expectedProductsListSize = 0;

        // when
        List<Product> products = Product.parseProductsFromResponseDto(null, query);

        // then
        assertEquals(expectedProductsListSize, products.size());
    }

    @Test
    void checkIfFieldsAreNotNullAndSetValues() {
        //given
        Product product = new Product();

        //when
        Product.checkIfFieldsAreNotNullAndSetValues(
                product,
                null,
                null,
                null,
                null,
                null,
                BigDecimal.valueOf(20),
                BigDecimal.valueOf(5),
                "image",
                query
        );

        //then
        assertEquals(query, product.getQuery());
        assertEquals("image", product.getImage());
        assertEquals(BigDecimal.ZERO, product.getKcal());
        assertEquals(BigDecimal.ZERO, product.getFat());
        assertEquals("", product.getName());
        assertEquals("", product.getProductId());
    }

    @Test
    void valueOrZero_InputZero() {
        //given
        BigDecimal expected = BigDecimal.ZERO;

        //when
        BigDecimal result = Product.valueOrZero(null);

        //then
        assertEquals(expected, result);
    }

    @Test
    void valueOrZero_InputDataHigherThanZero() {
        //given
        BigDecimal expected = BigDecimal.TEN;

        //when
        BigDecimal result = Product.valueOrZero(BigDecimal.TEN);

        //then
        assertEquals(expected, result);
    }

    @Test
    void valueOrEmpty_inputBlankString() {
        //given
        String expected = "";

        //when
        String result = Product.valueOrEmpty(null);

        //then
        assertEquals(expected, result);
    }

    @Test
    void valueOrEmpty_inputNotEmptyString() {
        //given
        String expected = query;

        //when
        String result = Product.valueOrEmpty(expected);

        //then
        assertEquals(expected, result);
    }


    private ResponseDTO buildResponseDto() {
        Map<String, BigDecimal> nutrients = Map.of(
                "ENERC_KCAL", BigDecimal.valueOf(150),
                "PROCNT", BigDecimal.valueOf(10),
                "FAT", BigDecimal.valueOf(5),
                "CHOCDF", BigDecimal.valueOf(20),
                "FIBTG", BigDecimal.valueOf(5)
        );

        FoodDTO foodDTO = FoodDTO.builder()
                .foodId("foodId")
                .label("label")
                .knownAs("knownAs")
                .category("category")
                .categoryLabel("categoryLabel")
                .image("image")
                .nutrients(nutrients)
                .build();

        MeasureDTO measureGram = MeasureDTO.builder()
                .uri("uri")
                .label("Gram")
                .weight(BigDecimal.valueOf(1))
                .build();

        MeasureDTO measureDecagram = MeasureDTO.builder()
                .uri("uri")
                .label("Kilogram")
                .weight(BigDecimal.valueOf(1000))
                .build();

        List<MeasureDTO> measures = List.of(measureGram, measureDecagram);
        HintDTO hintDTO = HintDTO.builder()
                .food(foodDTO)
                .measures(measures)
                .build();

        return ResponseDTO.builder()
                .text("text")
                .parsed(new ArrayList<>())
                .hints(List.of(hintDTO, hintDTO, hintDTO, hintDTO))
                .build();
    }
}