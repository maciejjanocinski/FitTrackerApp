package app.recipe;

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
public class RecipeService {
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

    List<Recipe> searchRecipes(String query, Authentication authentication) {
        String lowerCasedQuery = query.toLowerCase();

        User user = userService.getUserByUsername(authentication.getName());
        if (user.getLastRecipeQuery() != null && user.getLastRecipeQuery().equals(lowerCasedQuery)) {
            return user.getLastSearchedRecipes().stream()
                    .filter(r -> r.getQuery().equals(lowerCasedQuery))
                    .toList();
        }

        clearNotUsedRecipes(user);
        recipeRepository.deleteNotFavouriteRecipes(user.getId());

        user.setLastRecipeQuery(lowerCasedQuery);
        String url = createUrl(id, key, lowerCasedQuery);
        SearchResult result = getRecipesFromApi(url);

        List<Recipe> recipes = result.getHits().stream()
                .map(recipeAndLinkDto -> {
                    RecipeDto recipeDto = recipeAndLinkDto.getRecipe();
                    recipeDto.calculateNutrientsPerServing();
                    Recipe recipe = recipeMapper.mapToRecipe(recipeDto);
                    recipe.setUsed(false);
                    recipe.setUser(user);
                    recipe.setQuery(lowerCasedQuery);
                    return recipe;
                })
                .toList();

        recipeRepository.saveAll(recipes);
        return recipes;
    }

    @Transactional
    public Recipe addRecipeToFavourites(Long id, Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());

        for (Recipe recipe : user.getLastSearchedRecipes()) {
            if (recipe.getId().equals(id) && recipe.isUsed()) {
                throw new RecipeAlreadyAddedException("Recipe already added");
            }
        }

        Recipe recipe = user.getLastSearchedRecipes().stream()
                .filter(r -> r.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RecipeAlreadyAddedException("Recipe not found"));

        recipe.setUsed(true);
        return recipe;
    }

    public List<Recipe> getMyRecipes(Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        return user.getLastSearchedRecipes().stream()
                .filter(Recipe::isUsed)
                .toList();
    }

    private void clearNotUsedRecipes(User user) {
        user.getLastSearchedRecipes().stream()
                .filter(recipe -> !recipe.isUsed())
                .forEach(recipe -> {
                    recipe.setUser(null);
                    recipeRepository.delete(recipe);
                })
        ;
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
