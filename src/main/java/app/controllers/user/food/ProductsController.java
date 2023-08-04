package app.controllers.user.food;

import app.models.ProductEntity;
import app.services.ProductsService;
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
@RequestMapping("/user/food")
public class ProductsController {

    private final ProductsService productsService;

    @GetMapping("/")
    public ResponseEntity<?> getProductsSearchPage(){
        return ResponseEntity.ok("Products search page");
    }
    @GetMapping("/search")
    public ResponseEntity<List<ProductEntity>> searchProducts(@RequestParam String product) throws IOException, InterruptedException {
        return productsService.searchProducts(product);
    }
}
