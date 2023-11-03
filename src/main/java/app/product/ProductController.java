package app.product;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productsService;

    @GetMapping("/search")
     List<Product> searchProducts(@RequestParam String product,
                                  @CurrentSecurityContext(expression = "authentication")
                                  Authentication authentication) {
        return productsService.searchProducts(product, authentication);
    }

}
