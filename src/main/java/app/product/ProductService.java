package app.product;

import app.util.FoodApiManager;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

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

        dotenv.get("PRODUCTS_API_URL");
        String url = createUrl(id, key, query);
        ResponseDTO response = getProductsFromApi(url);

        List<Product> products = Product.parseProductsFromResponseDto(response, query);

        productsRepository.saveAll(products);
        return products;
    }

    private ResponseDTO getProductsFromApi(String url) {
        ResponseEntity<ResponseDTO> responseEntity = restTemplate.getForEntity(url, ResponseDTO.class);
        return responseEntity.getBody();
    }

    private String createUrl(String id, String key, String query) {
        String baseUrl = dotenv.get("PRODUCTS_API_URL");

        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("app_id", id)
                .queryParam("app_key", key)
                .queryParam("ingr", query)
                .toUriString();
    }


}
