package app.product;

import app.user.User;
import app.user.UserRepository;
import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final Dotenv dotenv = Dotenv.load();
    private final ProductRepository productsRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate = new RestTemplate();


    public List<Product> searchProducts(String query, Authentication authentication) {

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.getLastProductQuery() != null && user.getLastProductQuery().equals(query)) {
            return productsRepository.findAllByQuery(query);
        }

        productsRepository.deleteNotUsedProducts();
        user.setLastProductQuery(query);

        String key = dotenv.get("PRODUCTS_API_KEY");
        String id = dotenv.get("PRODUCTS_API_ID");

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
