package app.product;

import app.util.FoodApiManager;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor

public class ProductService {
    private final Dotenv dotenv = Dotenv.load();
    private final ProductRepository productsRepository;
    private final FoodApiManager foodApiManager = new FoodApiManager();
    private final RestTemplate restTemplate = new RestTemplate();

    public List<Product> searchProducts(String query) {
        if (foodApiManager.getLastQuery() != null && foodApiManager.getLastQuery().equals(query)) {
            return productsRepository.findAllByQuery(query);
        }
        productsRepository.deleteNotUsedProducts();
        foodApiManager.setLastQuery(query);

        String key = dotenv.get("PRODUCTS_API_KEY");
        String id = dotenv.get("PRODUCTS_API_ID");

        ResponseDTO response = getProductsFromFoodApi(id, key, query);
        List<Product> products = parseProductsFromResponseDto(response, query);

        productsRepository.saveAll(products);
        return products;
    }

    private ResponseDTO getProductsFromFoodApi(String id, String key, String query) {

        String apiUrl = UriComponentsBuilder.fromHttpUrl("https://api.edamam.com/api/food-database/v2/parser")
                .queryParam("app_id", id)
                .queryParam("app_key", key)
                .queryParam("ingr", query)
                .toUriString();
        ResponseEntity<ResponseDTO> responseEntity = restTemplate.getForEntity(apiUrl, ResponseDTO.class);

        return responseEntity.getBody();
    }

    private List<Product> parseProductsFromResponseDto(ResponseDTO response, String query) {
        List<Product> products = new ArrayList<>();
        if (response == null) {
            return products;
        }
        for (HintDTO hint : response.getHints()) {
            FoodDTO food = hint.getFood();
            Map<String, BigDecimal> nutrients = food.getNutrients();

            Map<String, BigDecimal> measures = hint.getMeasures().stream()
                    .collect(Collectors.toMap(
                            e -> valueOrEmpty(e.getLabel()),
                            e -> valueOrZero(e.getWeight())
                    ));

            Product product = new Product();
            checkIfFieldsAreNotNullAndSetValues(
                    product,
                    food.getFoodId(),
                    food.getLabel(),
                    nutrients.get("ENERC_KCAL"),
                    nutrients.get("PROCNT"),
                    nutrients.get("FAT"),
                    nutrients.get("CHOCDF"),
                    nutrients.get("FIBTG"),
                    food.getImage(),
                    query
            );

            product.setMeasures(measures);
            products.add(product);
        }
        return products;
    }

    private void checkIfFieldsAreNotNullAndSetValues(Product product,
                                                     String foodId,
                                                     String label,
                                                     BigDecimal kcal,
                                                     BigDecimal protein,
                                                     BigDecimal fat,
                                                     BigDecimal carbohydrates,
                                                     BigDecimal fiber,
                                                     String image,
                                                     String query
    ) {

        product.setProductId(valueOrEmpty(foodId));
        product.setName(valueOrEmpty(label));
        product.setKcal(valueOrZero(kcal));
        product.setProtein(valueOrZero(protein));
        product.setFat(valueOrZero(fat));
        product.setCarbohydrates(valueOrZero(carbohydrates));
        product.setFiber(valueOrZero(fiber));
        product.setImage(valueOrEmpty(image));
        product.setQuery(query);
    }

    private BigDecimal valueOrZero(BigDecimal numValue) {
        return numValue == null ? BigDecimal.ONE : numValue;
    }

    private String valueOrEmpty(String textValue) {
        return textValue == null ? "" : textValue;
    }
}
