package app.recipe;

import app.diary.Diary;
import app.exceptions.RecipeAlreadyAddedException;
import app.ingredient.Ingredient;
import app.ingredient.IngredientDto;
import app.ingredient.IngredientMapper;
import app.ingredient.IngredientRepository;
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

import static app.util.Utils.RECIPE_NOT_FOUND_MESSAGE;
import static app.util.Utils.ROLE_NOT_FOUND_MESSAGE;

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
    private final RecipeMapper recipeMapper = RecipeMapper.INSTANCE;
    private final IngredientMapper ingredientMapper = IngredientMapper.INSTANCE;

    private final UserService userService;

    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;

    List<RecipeDto> searchRecipes(String query, Authentication authentication) {
        String lowerCasedQuery = query.toLowerCase();
        User user = userService.getUserByUsername(authentication.getName());

        if (user.getLastRecipeQuery() != null && user.getLastRecipeQuery().equals(lowerCasedQuery)) {
            return recipeMapper.mapToDto(user.getLastlySearchedRecipes());
        }

        clearNotUsedRecipes(user);
        user.setLastRecipeQuery(lowerCasedQuery);
        recipeRepository.deleteNotFavouriteRecipes(user.getId());

        String url = createUrl(id, key, lowerCasedQuery);
        SearchResult result = getRecipesFromApi(url);

        List<Recipe> recipes = result.getHits().stream()
                .map(recipeAndLinkDto -> {
                    RecipeApiResult recipeApiResult = recipeAndLinkDto.getRecipe();
                    recipeApiResult.calculateNutrientsPerServing();
                    Recipe recipe = recipeMapper.mapToRecipe(recipeApiResult);
                    recipe.setUser(user);
                    recipe.setQuery(lowerCasedQuery);
                    return recipe;
                })
                .toList();

        recipeRepository.saveAll(recipes);
        return recipeMapper.mapToDto(recipes);

    }

    public IngredientDto addRecipeToDiary(AddRecipeToDiaryDto addProductToDiaryDto, Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        Diary diary = user.getDiary();
        Recipe recipe = recipeRepository.findByIdAndUser(addProductToDiaryDto.id(), user)
                .orElseThrow(() -> new IllegalArgumentException(ROLE_NOT_FOUND_MESSAGE));

        Ingredient ingredient = recipe.mapToIngredient();

        ingredient.getMeasures().forEach(measure -> measure.setIngredient(ingredient));
        ingredient.setUser(user);
        ingredient.setDiary(diary);
        diary.addProduct(ingredient);
        ingredientRepository.save(ingredient);
        return ingredientMapper.mapToDto(ingredient);
    }


    @Transactional
    public Recipe addRecipeToFavourites(Long id, Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        Diary diary = user.getDiary();
        for (Recipe recipe : user.getLastlySearchedRecipes()) {
            if (recipe.getId().equals(id) && recipe.getDiary() != null) {
                throw new RecipeAlreadyAddedException(RECIPE_NOT_FOUND_MESSAGE);
            }
        }

        Recipe recipe = recipeRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new IllegalArgumentException(RECIPE_NOT_FOUND_MESSAGE));

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
