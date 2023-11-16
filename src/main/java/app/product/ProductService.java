package app.product;

import app.user.User;
import app.user.UserService;
import app.util.exceptions.ProductNotFoundException;
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

    private final ProductMapper productMapper;

    private final RestTemplate restTemplate = new RestTemplate();

    @Transactional
    public List<ProductDto> searchProducts(String query, Authentication authentication) {
        String lowerCasedQuery = query.toLowerCase();
        User user = userService.getUserByUsername(authentication.getName());

        if (user.getLastProductQuery() != null && user.getLastProductQuery().equals(lowerCasedQuery)) {
            List<Product> products = user.getLastSearchedProducts();
            return mapToProductDto(products);
        }

        clearNotUsedProducts(user);
        user.setLastProductQuery(lowerCasedQuery);
        productsRepository.deleteNotUsedProducts(user.getId());

        String url = createUrl(id, key, lowerCasedQuery);
        ResponseDTO response = getProductsFromApi(url);

        List<Product> products = Product.parseProductsFromResponseDto(response, lowerCasedQuery, user);

        user.getLastSearchedProducts().addAll(products);
        productsRepository.saveAll(products);

        return mapToProductDto(products);
    }

    void clearNotUsedProducts(User user) {
        user.getLastSearchedProducts().removeAll(user.getLastSearchedProducts());
    }

    ProductDto getProductById(Authentication authentication, Long id) {
        User user = userService.getUserByUsername(authentication.getName());
        Product product = user.getLastSearchedProducts().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ProductNotFoundException("Product with id: " + id + " not found."));

        return productMapper.mapToProductDto(product);
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

    private List<ProductDto> mapToProductDto(List<Product> products) {
        return products.stream()
                .map(productMapper::mapToProductDto)
                .toList();
    }


}
