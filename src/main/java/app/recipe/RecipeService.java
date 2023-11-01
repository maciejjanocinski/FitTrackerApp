package app.recipe;

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
public class RecipeService {

    private final Dotenv dotenv = Dotenv.load();
    private final RestTemplate restTemplate = new RestTemplate();
    String key = dotenv.get("RECIPES_API_KEY");
    String id = dotenv.get("RECIPES_API_ID");
    String baseUrl = dotenv.get("RECIPES_API_URL");

    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;

    SearchResult searchRecipes(String query) {
        String url = createUrl(id, key, query);

        SearchResult result = getProductsFromApi(url);

        List<RecipeAndLinkDto> recipesDtos = getProductsFromApi(url).getHits();
        List<RecipeDto> recipes = recipesDtos.stream()
                .map(RecipeAndLinkDto::getRecipe)
                .toList();
        recipes.forEach(RecipeDto::calculateNutrientsPerServing);

        recipeRepository.saveAll(recipes);
        return result;
    }

    private SearchResult getProductsFromApi(String url) {
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
