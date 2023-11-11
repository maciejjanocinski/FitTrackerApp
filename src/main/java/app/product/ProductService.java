package app.product;

import app.user.User;
import app.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productsRepository;
    private final UserService userService;

    @Value("${api.products.url}")
    private String baseUrl;

    @Value("${api.products.key}")
    private String key;

    @Value("${api.products.id}")
    private String id;

    private final RestTemplate restTemplate = new RestTemplate();

    @Transactional
    public List<Product> searchProducts(String query, Authentication authentication) {

        User user = userService.getUserByUsername(authentication.getName());
        if (user.getLastProductQuery() != null && user.getLastProductQuery().equals(query)) {
            return productsRepository.findAllByQuery(query);
        }

        user.setLastProductQuery(query);
        productsRepository.deleteNotUsedProducts(user.getId());

        String url = createUrl(id, key, query);
        ResponseDTO response = getProductsFromApi(url);

        List<Product> products = Product.parseProductsFromResponseDto(response, query, user);

        user.getLastSearchedProducts().addAll(products);
        productsRepository.saveAll(products);
        return products;
    }

    @Transactional
    public void clearNotUsedProducts(Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        user.getLastSearchedProducts().clear();
    }

    private ResponseDTO getProductsFromApi(String url) {
        ResponseEntity<ResponseDTO> responseEntity = restTemplate.getForEntity(url, ResponseDTO.class);
        return responseEntity.getBody();
    }

    private String createUrl(String id, String key, String query) {

        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("app_id", id)
                .queryParam("app_key", key)
                .queryParam("ingr", query)
                .toUriString();
    }


}
