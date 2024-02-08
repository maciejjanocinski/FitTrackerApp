package app.ingredient;

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

import static app.ingredient.Ingredient.*;


@Service
@RequiredArgsConstructor
class IngredientService {
    private final IngredientRepository productsRepository;
    private final UserService userService;

    @Value("${api.products.url}")
    private String baseUrl;

    @Value("${api.products.key}")
    private String key;

    @Value("${api.products.id}")
    private String id;
    private final RestTemplate restTemplate = new RestTemplate();
    private final IngredientMapper ingredientMapper = IngredientMapper.INSTANCE;

    @Transactional
    public List<IngredientDto> search(String query, Authentication authentication) {
        String lowerCasedQuery = query.toLowerCase();
        User user = userService.getUserByUsername(authentication.getName());

        if (user.getLastProductQuery() != null && user.getLastProductQuery().equals(lowerCasedQuery)) {
            List<Ingredient> ingredients = user.getLastlySearchedIngredients().stream()
                    .filter(p -> p.getDiary() == null && !p.isLastlyAdded() && p.getQuery().equals(lowerCasedQuery))
                    .toList();
            return ingredientMapper.mapToDto(ingredients);
        }

        clearNotUsedProducts(user);
        user.setLastProductQuery(lowerCasedQuery);
        productsRepository.deleteNotUsedProducts(user.getId());

        String url = createUrl(id, key, lowerCasedQuery);
        ResponseDto response = getProductsResponseFromApi(url);

        List<Ingredient> ingredients = parseProductsFromResponseDto(response, lowerCasedQuery, user);
        ingredients.sort(Comparator.comparing(p -> p.getImage().length(), Comparator.reverseOrder()));
        setImagesToAllEmptyProducts(ingredients);

        user.getLastlySearchedIngredients().addAll(ingredients);
        productsRepository.saveAll(ingredients);

        return ingredientMapper.mapToDto(ingredients);
    }

    List<Ingredient> parseProductsFromResponseDto(ResponseDto response,
                                                  String query,
                                                  User user) {
        if (response == null) {
            return Collections.emptyList();
        }
        List<Ingredient> ingredients = new ArrayList<>();

        for (HintDto hint : response.getHints()) {
            FoodDto food = hint.getFood();
            Map<String, BigDecimal> nutrientsData = food.getNutrients();


            Ingredient ingredient = new Ingredient();
            ingredient.checkIfFieldsAreNotNullAndSetValues(
                    food.getLabel(),
                    food.getImage(),
                    query
            );

            List<Measure> measures = hint.getMeasures().stream()
                    .map(measureResponseDto -> Measure.builder()
                            .label(measureResponseDto.getLabel())
                            .weight(measureResponseDto.getWeight())
                            .ingredient(ingredient)
                            .build())
                    .toList();

            Nutrients nutrientsForProduct = ingredient.createNutrients(nutrientsData);
            nutrientsForProduct.setIngredient(ingredient);

            Measure usedMeasure = searchForMeasure(measures, "Gram");

            ingredient.setCurrentlyUsedMeasureName(usedMeasure.getLabel());
            ingredient.setQuantity(BigDecimal.valueOf(100));
            ingredient.setNutrients(nutrientsForProduct);
            ingredient.setMeasures(measures);
            ingredient.setUser(user);
            ingredients.add(ingredient);
        }
        return ingredients;
    }

    void clearNotUsedProducts(User user) {
        List<Ingredient> ingredients = user.getLastlySearchedIngredients().stream().filter(p -> p.getDiary() == null && !p.isLastlyAdded()).toList();
        ingredients.forEach(p -> {
            p.getMeasures().clear();
            p.setNutrients(null);
        });
        user.getLastlySearchedIngredients().removeAll(ingredients);
    }

    IngredientDto getProductById(Authentication authentication, Long id) {
        User user = userService.getUserByUsername(authentication.getName());
        Ingredient ingredient = user.getLastlySearchedIngredients().stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new ProductNotFoundException("Product with activityid: " + id + " not found."));

        return ingredientMapper.mapToDto(ingredient);
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



    private void setImagesToAllEmptyProducts(List<Ingredient> ingredients) {
        String image = ingredients.get(0).getImage();
        ingredients.stream().filter(p -> p.getImage().isEmpty()).forEach(p -> {
            p.setImage(image);
        });
    }
}
