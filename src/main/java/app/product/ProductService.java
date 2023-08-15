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

    public ResponseEntity<List<Product>> searchProducts(String product) throws IOException, InterruptedException {
        if(foodApiManager.getLastQuery() != null && foodApiManager.getLastQuery().equals(product)) {
            return ResponseEntity.ok(productsRepository.findAll());
        }
        productsRepository.deleteNotUsedProducts();
        foodApiManager.setLastQuery(product);

        String key = dotenv.get("PRODUCTS_API_KEY");
        String id = dotenv.get("PRODUCTS_API_ID");

        String json = getProductsFromFoodApi(id, key, product);
        List<Product> products = parseProductsFromJson(json);

        productsRepository.saveAll(products);
        return ResponseEntity.ok(products);
    }

    private String getProductsFromFoodApi(String id, String key, String product) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.edamam.com/api/food-database/v2/parser?app_id=" + id + "&app_key=" + key + "&ingr=" + product + "&nutrition-type=cooking"))
                .GET()
                .build();
        HttpResponse<String> res = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        return res.body();
    }

    private List<Product> parseProductsFromJson(String json) throws JsonProcessingException {
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
                        measureLabel == null ? "" : measureLabel.asText(),
                        measureWeight == null ? 0 : measureWeight.asDouble());
            }

            Product product = new Product();
            product.setMeasures(measures);
            checkIfFieldsAreNotNull(product, foodId, label, kcal, protein, fat, carbohydrates, fiber, image);
            products.add(product);
        }
        return products;
    }

    private void checkIfFieldsAreNotNull(Product product,
                                         JsonNode foodId,
                                         JsonNode label,
                                         JsonNode kcal,
                                         JsonNode protein,
                                         JsonNode fat,
                                         JsonNode carbohydrates,
                                         JsonNode fiber,
                                         JsonNode image
    ) {

        product.setProductId(foodId == null ? "" : foodId.asText());
        product.setName(label == null ? "" : label.asText());
        product.setKcal(kcal == null ? 0 : kcal.asDouble());
        product.setProtein(protein == null ? 0 : protein.asDouble());
        product.setFat(fat == null ? 0 : fat.asDouble());
        product.setCarbohydrates(carbohydrates == null ? 0 : carbohydrates.asDouble());
        product.setFiber(fiber == null ? 0 : fiber.asDouble());
        product.setImage(image == null ? "" : image.asText());
    }
}
