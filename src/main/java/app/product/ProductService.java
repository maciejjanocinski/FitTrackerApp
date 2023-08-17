package app.product;

import app.util.FoodApiManager;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;

@Service
@AllArgsConstructor
public class ProductService {

    private final Dotenv dotenv = Dotenv.load();
    private final ProductRepository productsRepository;
    private final ObjectMapper objectMapper;
    private final FoodApiManager foodApiManager = new FoodApiManager();

    public ResponseEntity<List<Product>> searchProducts(String product) {
        if (foodApiManager.getLastQuery() != null && foodApiManager.getLastQuery().equals(product)) {
            return ResponseEntity.ok(productsRepository.findAllByQuery(product));
        }
        productsRepository.deleteNotUsedProducts();
        foodApiManager.setLastQuery(product);

        String key = dotenv.get("PRODUCTS_API_KEY");
        String id = dotenv.get("PRODUCTS_API_ID");

        String json = getProductsFromFoodApi(id, key, product);
        List<Product> products = parseProductsFromJson(json, product);

        productsRepository.saveAll(products);
        return ResponseEntity.ok(products);
    }

    private String getProductsFromFoodApi(String id, String key, String product) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.edamam.com/api/food-database/v2/parser?app_id=" + id + "&app_key=" + key + "&ingr=" + product + "&nutrition-type=cooking"))
                    .GET()
                    .build();
            HttpResponse<String> res = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            return res.body();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    private List<Product> parseProductsFromJson(String json, String query) {
        try {
            JsonNode rootNode = objectMapper.readTree(json);
            JsonNode hintsNode = rootNode.get("hints");
            List<Product> products = new ArrayList<>();

            for (JsonNode node : hintsNode) {

                JsonNode foodId = node.get("food").get("foodId");
                JsonNode label = node.get("food").get("label");
                JsonNode kcal = node.get("food").get("nutrients").get("ENERC_KCAL");
                JsonNode protein = node.get("food").get("nutrients").get("PROCNT");
                JsonNode fat = node.get("food").get("nutrients").get("FAT");
                JsonNode carbohydrates = node.get("food").get("nutrients").get("CHOCDF");
                JsonNode fiber = node.get("food").get("nutrients").get("FIBTG");
                JsonNode image = node.get("food").get("image");
                JsonNode measuresNodes = node.get("measures");

                Map<String, Double> measures = new HashMap<>();
                for (JsonNode measureNode : measuresNodes
                ) {
                    JsonNode measureLabel = measureNode.get("label");
                    JsonNode measureWeight = measureNode.get("weight");
                    measures.put(
                            (String) valueOrEmpty(measureLabel, false),
                            (Double) valueOrEmpty(measureWeight, true));
                }

                Product product = new Product();
                product.setMeasures(measures);
                checkIfFieldsAreNotNullAndSetValues(product, foodId, label, kcal, protein, fat, carbohydrates, fiber, image, query);
                products.add(product);
            }
            return products;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    private void checkIfFieldsAreNotNullAndSetValues(Product product,
                                                     JsonNode foodId,
                                                     JsonNode label,
                                                     JsonNode kcal,
                                                     JsonNode protein,
                                                     JsonNode fat,
                                                     JsonNode carbohydrates,
                                                     JsonNode fiber,
                                                     JsonNode image,
                                                     String query
    ) {

        product.setProductId((String) valueOrEmpty(foodId, false));
        product.setName((String) valueOrEmpty(label, false));
        product.setKcal((Double) valueOrEmpty(kcal, true));
        product.setProtein((Double) valueOrEmpty(protein, true));
        product.setFat((Double) valueOrEmpty(fat, true));
        product.setCarbohydrates((Double) valueOrEmpty(carbohydrates, true));
        product.setFiber((Double) valueOrEmpty(fiber, true));
        product.setImage((String) valueOrEmpty(image, false));
        product.setQuery(query);
    }

    private Object valueOrEmpty(JsonNode node, boolean isDouble) {
        if (isDouble) {
            return node == null ? 0 : node.asDouble();
        }
        return node == null ? "" : node.asText();
    }
}
