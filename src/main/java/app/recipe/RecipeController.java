package app.recipe;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/recipes")
public class RecipeController {

    private final RecipeService RecipeService;

    @GetMapping("/search")
    SearchResult searchProducts(@RequestParam String recipe) {
        return RecipeService.searchRecipes(recipe);
    }
}
