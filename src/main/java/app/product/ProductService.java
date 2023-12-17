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

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static app.product.ProductMapper.mapToProductDto;

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
    public List<ProductDto> searchProducts(String query, Authentication authentication) {
        String lowerCasedQuery = query.toLowerCase();
        User user = userService.getUserByUsername(authentication.getName());

        if (user.getLastProductQuery() != null && user.getLastProductQuery().equals(lowerCasedQuery)) {
            List<Product> products = user.getLastSearchedProducts().stream()
                    .filter(p -> p.getDiary() == null)
                    .toList();
            return mapToProductsDtoList(products);
        }

        clearNotUsedProducts(user);
        user.setLastProductQuery(lowerCasedQuery);
        productsRepository.deleteNotUsedProducts(user.getId());

        String url = createUrl(id, key, lowerCasedQuery);
        ResponseDto response = getProductsResponseFromApi(url);

        List<Product> products = Product.parseProductsFromResponseDto(response, lowerCasedQuery, user);
        products.sort(Comparator.comparing(p -> p.getImage().length(), Comparator.reverseOrder()));
        setImagesToAllEmptyProducts(products);

        user.getLastSearchedProducts().addAll(products);
        productsRepository.saveAll(products);

        return mapToProductsDtoList(products);
    }

    void clearNotUsedProducts(User user) {
        List<Product> products = user.getLastSearchedProducts().stream().filter(p -> p.getDiary() == null).toList();
        products.forEach(p -> {
            p.getMeasures().clear();
            p.setNutrients(null);
        });
        user.getLastSearchedProducts().removeAll(products);
    }

    ProductDto getProductById(Authentication authentication, Long id) {
        User user = userService.getUserByUsername(authentication.getName());
        Product product = user.getLastSearchedProducts().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ProductNotFoundException("Product with activityid: " + id + " not found."));

        return mapToProductDto(product);
    }

    private ResponseDto getProductsResponseFromApi(String url) {
        ResponseEntity<ResponseDto> responseEntity = restTemplate.getForEntity(url, ResponseDto.class);
        return responseEntity.getBody();
    }

    private String createUrl(String id, String key, String query) {

        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("app_id", id)
                .queryParam("app_key", key)
                .queryParam("ingr", query)
                .toUriString();
    }

    private List<ProductDto> mapToProductsDtoList(List<Product> products) {
        return products.stream()
                .map(ProductMapper::mapToProductDto)
                .toList();
    }

    private void setImagesToAllEmptyProducts(List<Product> products) {
        String image = products.get(0).getImage();
        products.stream().filter(p -> p.getImage().isEmpty()).forEach(p -> {
            p.setImage(image);
        });
    }
}
