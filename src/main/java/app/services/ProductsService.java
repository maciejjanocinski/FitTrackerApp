package app.services;

import app.models.ProductEntity;
import app.repository.ProductsRepository;
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
public class ProductsService {

   private final Dotenv dotenv = Dotenv.load();
   private final ProductsRepository productsRepository;
   private final ObjectMapper objectMapper;

    public ResponseEntity<List<ProductEntity>> searchProducts(String product) throws IOException, InterruptedException {
        productsRepository.deleteNotUsedProducts();
        String key = dotenv.get("PRODUCTS_API_KEY");
        String id = dotenv.get("PRODUCTS_API_ID");

        String response = apiRequest(id, key, product);
        List<ProductEntity> products = parseProductsFromJson(response);

        productsRepository.saveAll(products);
        return ResponseEntity.ok(products);
    }

    private String apiRequest(String id, String key, String query) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.edamam.com/api/food-database/v2/parser?app_id=" + id + "&app_key=" + key + "&ingr=" + query + "&nutrition-type=cooking"))
                .GET()
                .build();
        HttpResponse<String> res = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        return res.body();
    }

    private List<ProductEntity> parseProductsFromJson(String json) throws JsonProcessingException {
        JsonNode rootNode = objectMapper.readTree(json);
        JsonNode hintsNode = rootNode.get("hints");
        List<ProductEntity> products = new ArrayList<>();

        for (JsonNode node : hintsNode) {
            ProductEntity product = new ProductEntity();

            var foodId = node.get("food").get("foodId");
            var label = node.get("food").get("label");
            var kcal = node.get("food").get("nutrients").get("ENERC_KCAL");
            var protein = node.get("food").get("nutrients").get("PROCNT");
            var fat = node.get("food").get("nutrients").get("FAT");
            var carbohydrates = node.get("food").get("nutrients").get("CHOCDF");
            var fiber = node.get("food").get("nutrients").get("FIBTG");
            var image = node.get("food").get("image");
            var measuresNodes = node.get("measures");

            Map<String, Double> measures = new HashMap<>();
            for (JsonNode measureNode : measuresNodes
            ) {
                var measureLabel = measureNode.get("label");
                var measureWeight = measureNode.get("weight");
                measures.put(
                        measureLabel == null ? "" : measureLabel.asText(),
                        measureWeight == null ? 0 : measureWeight.asDouble());
            }

            product.setMeasures(measures);
            product.setProductId(foodId == null ? "" : foodId.asText());
            product.setName(label == null ? "" : label.asText());
            product.setKcal(kcal == null ? 0 : kcal.asDouble());
            product.setProtein(protein == null ? 0 : protein.asDouble());
            product.setFat(fat == null ? 0 : fat.asDouble());
            product.setCarbohydrates(carbohydrates == null ? 0 : carbohydrates.asDouble());
            product.setFiber(fiber == null ? 0 : fiber.asDouble());
            product.setImage(image == null ? "" : image.asText());

            products.add(product);
        }
        return products;
    }
}
