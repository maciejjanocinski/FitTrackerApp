package app.recipe;

import app.user.User;
import app.user.UserRepository;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.transaction.Transactional;
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
public class RecipeService {

    private final Dotenv dotenv = Dotenv.load();
    private final RestTemplate restTemplate = new RestTemplate();
    private final String key = dotenv.get("RECIPES_API_KEY");
    private final String id = dotenv.get("RECIPES_API_ID");
    private final String baseUrl = dotenv.get("RECIPES_API_URL");

    private final RecipeMapper recipeMapper;

    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;

    List<Recipe> searchRecipes(String query, Authentication authentication) {

        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (user.getLastProductQuery() != null && user.getLastProductQuery().equals(query)) {
            return recipeRepository.findAllByQuery(query);
        }
        recipeRepository.deleteNotFavouriteRecipes();

        user.setLastProductQuery(query);
        String url = createUrl(id, key, query);
        SearchResult result = getRecipesFromApi(url);

        List<Recipe> recipes = result.getHits().stream()
                .map(recipeAndLinkDto -> {
                    RecipeDto recipeDto = recipeAndLinkDto.getRecipe();
                    recipeDto.calculateNutrientsPerServing();
                    Recipe recipe = recipeMapper.mapToRecipe(recipeDto);
                    recipe.setUsed(false);
                    recipe.setQuery(query);
                    return recipe;
                })
                .toList();

        recipeRepository.saveAll(recipes);
        return recipes;
    }
    @Transactional
    public Recipe addRecipeToFavourites(Long id, Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

       for (Recipe recipe : user.getFavouriteRecipes()) {
            if (recipe.getId().equals(id)) {
                throw new RuntimeException("Recipe already added");
            }
        }

        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        recipe.setUsed(true);
        user.addFavouriteRecipe(recipe);
        return recipe;
    }

    public List<Recipe> getMyRecipes(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return user.getFavouriteRecipes();
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
