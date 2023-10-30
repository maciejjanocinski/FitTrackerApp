package app.recipe;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
@Service
@RequiredArgsConstructor
public class RecipeService {

    private final Dotenv dotenv = Dotenv.load();
    private final RestTemplate restTemplate = new RestTemplate();


    SearchResult searchRecipes(String query) {
        String key = dotenv.get("RECIPES_API_KEY");
        String id = dotenv.get("RECIPES_API_ID");

       String url = createUrl(id, key, query);
        return getProductsFromApi(url);
    }

    private SearchResult getProductsFromApi(String url) {
        ResponseEntity<SearchResult> responseEntity = restTemplate.getForEntity(url, SearchResult.class);
        return responseEntity.getBody();
    }

    private String createUrl(String id, String key, String query) {
        String baseUrl = dotenv.get("RECIPES_API_URL");

        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .queryParam("type", "public")
                .queryParam("q", query)
                .queryParam("app_id", id)
                .queryParam("app_key", key)
                .queryParam("field", "label")
                .queryParam("field", "image")
                .queryParam("field", "source")
                .queryParam("field", "url")
                .queryParam("field", "yield")
                .queryParam("field", "ingredientLines")
                .queryParam("field", "totalNutrients")
                .toUriString();
    }
}
