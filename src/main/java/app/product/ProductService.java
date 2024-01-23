package app.product;

import app.nutrients.Nutrients;
import app.user.User;
import app.user.UserService;
import app.exceptions.ProductNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.*;

import static app.product.Product.*;


@Service
@RequiredArgsConstructor
class ProductService {
    private final ProductRepository productsRepository;
    private final UserService userService;

    @Value("${api.products.url}")
    private String baseUrl;

    @Value("${api.products.key}")
    private String key;

    @Value("${api.products.id}")
    private String id;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ProductMapper productMapper = ProductMapper.INSTANCE;

    @Transactional
    public List<ProductDto> searchProducts(String query, Authentication authentication) {
        String lowerCasedQuery = query.toLowerCase();
        User user = userService.getUserByUsername(authentication.getName());

        if (user.getLastProductQuery() != null && user.getLastProductQuery().equals(lowerCasedQuery)) {
            List<Product> products = user.getLastlySearchedProducts().stream()
                    .filter(p -> p.getDiary() == null && !p.isLastlyAdded() && p.getQuery().equals(lowerCasedQuery))
                    .toList();
            return productMapper.mapToDto(products);
        }

        clearNotUsedProducts(user);
        user.setLastProductQuery(lowerCasedQuery);
        productsRepository.deleteNotUsedProducts(user.getId());

        String url = createUrl(id, key, lowerCasedQuery);
        ResponseDto response = getProductsResponseFromApi(url);

        List<Product> products = parseProductsFromResponseDto(response, lowerCasedQuery, user);
        products.sort(Comparator.comparing(p -> p.getImage().length(), Comparator.reverseOrder()));
        setImagesToAllEmptyProducts(products);

        user.getLastlySearchedProducts().addAll(products);
        productsRepository.saveAll(products);

        return productMapper.mapToDto(products);
    }

    List<Product> parseProductsFromResponseDto(ResponseDto response,
                                               String query,
                                               User user) {
        if (response == null) {
            return Collections.emptyList();
        }
        List<Product> products = new ArrayList<>();

        for (HintDto hint : response.getHints()) {
            FoodDto food = hint.getFood();
            Map<String, BigDecimal> nutrientsData = food.getNutrients();


            Product product = new Product();
            product.checkIfFieldsAreNotNullAndSetValues(
                    food.getLabel(),
                    food.getImage(),
                    query
            );

            List<Measure> measures = hint.getMeasures().stream()
                    .map(measureResponseDto -> Measure.builder()
                            .label(measureResponseDto.getLabel())
                            .weight(measureResponseDto.getWeight())
                            .product(product)
                            .build())
                    .toList();

            Nutrients nutrientsForProduct = product.createNutrients(nutrientsData);
            nutrientsForProduct.setProduct(product);

            Measure usedMeasure = searchForMeasure(measures, "Gram");

            product.setCurrentlyUsedMeasureName(usedMeasure.getLabel());
            product.setQuantity(BigDecimal.valueOf(100));
            product.setNutrients(nutrientsForProduct);
            product.setMeasures(measures);
            product.setUser(user);
            products.add(product);
        }
        return products;
    }

    void clearNotUsedProducts(User user) {
        List<Product> products = user.getLastlySearchedProducts().stream().filter(p -> p.getDiary() == null && !p.isLastlyAdded()).toList();
        products.forEach(p -> {
            p.getMeasures().clear();
            p.setNutrients(null);
        });
        user.getLastlySearchedProducts().removeAll(products);
    }

    ProductDto getProductById(Authentication authentication, Long id) {
        User user = userService.getUserByUsername(authentication.getName());
        Product product = user.getLastlySearchedProducts().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ProductNotFoundException("Product with activityid: " + id + " not found."));

        return productMapper.mapToDto(product);
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



    private void setImagesToAllEmptyProducts(List<Product> products) {
        String image = products.get(0).getImage();
        products.stream().filter(p -> p.getImage().isEmpty()).forEach(p -> {
            p.setImage(image);
        });
    }
}
