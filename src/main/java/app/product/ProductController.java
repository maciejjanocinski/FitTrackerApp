package app.product;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productsService;

    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String product) throws IOException, InterruptedException {
        return productsService.searchProducts(product);
    }
}
