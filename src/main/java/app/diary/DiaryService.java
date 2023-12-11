package app.diary;

import app.diary.dto.*;
import app.product.ProductDto;
import app.user.UserService;
import app.util.exceptions.ProductNotFoundException;
import app.product.Product;
import app.product.ProductRepository;
import app.user.User;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import static app.diary.DiaryMapper.mapDiaryToDiaryDto;
import static app.product.ProductMapper.*;
import static app.util.Utils.PRODUCT_NOT_FOUND_MESSAGE;


@Service
@AllArgsConstructor
@Transactional
public class DiaryService {

    private final ProductRepository productsRepository;
    private final UserService userService;

    public DiaryDto getDiary(Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        Diary diary = user.getDiary();
        diary.calculateNutrientsSum();
        diary.calculateNutrientsLeft();

        return mapDiaryToDiaryDto(diary);
    }

    public ProductDto addProductToDiary(AddProductToDiaryDto addProductDto, Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        Diary diary = user.getDiary();
        Product product = user.getLastSearchedProducts().stream()
                .filter(p -> p.getId().equals(addProductDto.id()))
                .findFirst()
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND_MESSAGE));

        Product newProduct = new Product();
        mapProductToProduct(newProduct, product);

        newProduct.setDiary(diary);
        newProduct.setUser(user);
        newProduct.editProductAmount(addProductDto.measureLabel(), addProductDto.quantity());
        diary.addProduct(newProduct);

        productsRepository.save(newProduct);
        return mapToProductDto(newProduct);
    }

    public ProductDto editProductAmountInDiary(EditProductInDiaryDto editProductDto, Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        Diary diary = user.getDiary();

        Product product = productsRepository.findProductById(editProductDto.id())
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND_MESSAGE));

        product.editProductAmount(editProductDto.measureLabel(), editProductDto.quantity());
        diary.calculateNutrientsSum();
        diary.calculateNutrientsLeft();

        return mapToProductDto(product);
    }

    public String deleteProductFromDiary(DeleteProductDto deleteProductDto, Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        Diary diary = user.getDiary();

        Product product =  diary.getProducts().stream()
                .filter(p -> p.getId().equals(deleteProductDto.id()))
                .findFirst()
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND_MESSAGE));

        diary.removeProduct(product);
        productsRepository.delete(product);
        return "Product deleted from diary successfully";
    }
}
