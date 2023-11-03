package app.recipe;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/recipes")
public class RecipeController {

    private final RecipeService RecipeService;

    @GetMapping("/search")
    List<Recipe> searchProducts(@RequestParam String recipe,
                                @CurrentSecurityContext(expression = "authentication")
                                Authentication authentication) {
        return RecipeService.searchRecipes(recipe, authentication);
    }

    @PostMapping("/")
    Recipe addRecipeToFavourites(@RequestBody Long id,
                                 @CurrentSecurityContext(expression = "authentication")
                                 Authentication authentication) {
        return RecipeService.addRecipeToFavourites(id, authentication);
    }

    @GetMapping("/")
    List<Recipe> getMyRecipes(@CurrentSecurityContext(expression = "authentication")
                        Authentication authentication) {
        return RecipeService.getMyRecipes(authentication);
    }


}
