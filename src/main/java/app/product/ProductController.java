package app.product;

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
public class ProductController {

    private final ProductService productsService;

    @GetMapping("/search")
    List<ProductDto> searchProducts(@RequestParam String product,
                                    @CurrentSecurityContext(expression = "authentication")
                                    Authentication authentication) {
        return productsService.searchProducts(product, authentication);
    }

    @GetMapping("/{id}")
    ProductDto getProductById(@CurrentSecurityContext(expression = "authentication")
                              Authentication authentication,
                              @PathVariable Long id) {
        return productsService.getProductById(authentication, id);
    }

}
