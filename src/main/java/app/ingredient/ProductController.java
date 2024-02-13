package app.ingredient;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/products")
 class ProductController {

    private final IngredientService productsService;

    @GetMapping("/search")
    List<IngredientDto> searchProducts(@RequestParam String product,
                                       @CurrentSecurityContext(expression = "authentication")
                                    Authentication authentication) {
        return productsService.search(product, authentication);
    }

    @GetMapping("/{id}")
    IngredientDto getProductById(@CurrentSecurityContext(expression = "authentication")
                              Authentication authentication,
                                 @PathVariable Long id) {
        return productsService.getProductById(authentication, id);
    }
}
