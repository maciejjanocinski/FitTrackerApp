package app.recipe;

import app.ingredient.IngredientDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/recipes")
 class RecipeController {

    private final RecipeService RecipeService;

    @GetMapping("/search")
    List<RecipeDto> searchProducts(@RequestParam String recipe,
                                   @CurrentSecurityContext(expression = "authentication")
                                Authentication authentication) {
        return RecipeService.searchRecipes(recipe, authentication);
    }

    @PostMapping("/")
    IngredientDto addRecipeToDiary(@RequestBody @Valid AddRecipeToDiaryDto addProductToDiaryDto,
                                   @CurrentSecurityContext(expression = "authentication")
                                 Authentication authentication) {
        return RecipeService.addRecipeToDiary(addProductToDiaryDto, authentication);
    }

    @PostMapping("/favourites")
    Recipe addRecipeToFavourites(@RequestBody Long id,
                                 @CurrentSecurityContext(expression = "authentication")
                                 Authentication authentication) {
        return RecipeService.addRecipeToFavourites(id, authentication);
    }

    @GetMapping("/favourites")
    List<Recipe> getMyRecipes(@CurrentSecurityContext(expression = "authentication")
                        Authentication authentication) {
        return RecipeService.getMyRecipes(authentication);
    }


}
