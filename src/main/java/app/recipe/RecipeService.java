package app.recipe;

import static app.product.ProductMapper.mapRecipeToProduct;
import static app.product.ProductMapper.mapToProductDto;
import static app.recipe.RecipeMapper.mapRecipeDtoToRecipeDtoList;

import app.diary.Diary;
import app.product.Product;
import app.product.ProductDto;
import app.product.ProductRepository;
import app.user.User;
import app.user.UserService;
import app.util.exceptions.RecipeAlreadyAddedException;
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
 class RecipeService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${api.recipes.url}")
    private String baseUrl;

    @Value("${api.recipes.key}")
    private String key;

    @Value("${api.recipes.id}")
    private String id;
    private final RecipeMapper recipeMapper;

    private final UserService userService;

    private final RecipeRepository recipeRepository;
    private final ProductRepository productRepository;

    List<RecipeDto> searchRecipes(String query, Authentication authentication) {
        String lowerCasedQuery = query.toLowerCase();
        User user = userService.getUserByUsername(authentication.getName());

        if (user.getLastRecipeQuery() != null && user.getLastRecipeQuery().equals(lowerCasedQuery)) {
            return mapRecipeDtoToRecipeDtoList(user.getLastlySearchedRecipes());
        }

        clearNotUsedRecipes(user);
        user.setLastRecipeQuery(lowerCasedQuery);
        recipeRepository.deleteNotFavouriteRecipes(user.getId());

        String url = createUrl(id, key, lowerCasedQuery);
        SearchResult result = getRecipesFromApi(url);

        List<Recipe> recipes = result.getHits().stream()
                .map(recipeAndLinkDto -> {
                    RecipeDto recipeDto = recipeAndLinkDto.getRecipe();
                    recipeDto.calculateNutrientsPerServing();
                    Recipe recipe = recipeMapper.mapToRecipe(recipeDto);
                    recipe.setUser(user);
                    recipe.setQuery(lowerCasedQuery);
                    return recipe;
                })
                .toList();

        recipeRepository.saveAll(recipes);
        return mapRecipeDtoToRecipeDtoList(recipes);
    }

    public ProductDto addRecipeToDiary(AddRecipeToDiaryDto addProductToDiaryDto, Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        Diary diary = user.getDiary();
        Recipe recipe = recipeRepository.findByIdAndUser(addProductToDiaryDto.id(), user)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));

        Product product = mapRecipeToProduct(recipe);

        product.getMeasures().forEach(measure -> measure.setProduct(product));
        product.setUser(user);
        product.setDiary(diary);
        diary.addProduct(product);
        productRepository.save(product);
        return mapToProductDto(product);
    }


    @Transactional
    public Recipe addRecipeToFavourites(Long id, Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        Diary diary = user.getDiary();
        for (Recipe recipe : user.getLastlySearchedRecipes()) {
            if (recipe.getId().equals(id) && recipe.getDiary() != null) {
                throw new RecipeAlreadyAddedException("Recipe already added");
            }
        }

        Recipe recipe = recipeRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException("Recipe not found"));

        diary.getRecipes().add(recipe);
        recipe.setDiary(diary);
        return recipe;
    }

    public List<Recipe> getMyRecipes(Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        return user.getLastlySearchedRecipes().stream()
                .filter(r -> r.getDiary() != null)
                .toList();
    }

    private void clearNotUsedRecipes(User user) {
        user.getLastlySearchedRecipes().removeAll(user.getLastlySearchedRecipes());
    }


    private SearchResult getRecipesFromApi(String url) {
        ResponseEntity<SearchResult> responseEntity = restTemplate.getForEntity(url, SearchResult.class);
        return responseEntity.getBody();
    }

    private String createUrl(String id, String key, String query) {

        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("q", query)
                .queryParam("app_key", key)
                .queryParam("field", "label")
                .queryParam("field", "image")
                .queryParam("field", "source")
                .queryParam("field", "url")
                .queryParam("field", "yield")
                .queryParam("field", "ingredientLines")
                .queryParam("field", "totalNutrients")
                .queryParam("type", "public")
                .queryParam("app_id", id)
                .toUriString();
    }


}
