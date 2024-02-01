package app.diary;

import app.diary.dto.*;
import app.product.ProductDto;
import app.product.ProductMapper;
import app.user.UserService;
import app.exceptions.ProductNotFoundException;
import app.product.Product;
import app.product.ProductRepository;
import app.user.User;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import static app.util.Utils.PRODUCT_NOT_FOUND_MESSAGE;


@Service
@AllArgsConstructor
@Transactional
public class DiaryService {

    private final ProductRepository productsRepository;
    private final UserService userService;
    private final DiaryMapper diaryMapper = DiaryMapper.INSTANCE;
    private final ProductMapper productMapper = ProductMapper.INSTANCE;

    public DiaryDto getDiary(Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        Diary diary = user.getDiary();
        diary.calculateNutrientsSum();
        diary.calculateNutrientsLeft();

        return diaryMapper.mapToDto(diary);
    }
    public ProductDto addProductToDiary(AddProductToDiaryDto addProductDto, Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        Diary diary = user.getDiary();
        Product product = user.getLastlySearchedProducts().stream()
                .filter(p -> p.getId().equals(addProductDto.id()))
                .findFirst()
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND_MESSAGE));

        Product newProduct = new Product(product);
        newProduct.setDiary(diary);
        newProduct.setUser(user);
        newProduct.changeAmount(addProductDto.measureLabel(), addProductDto.quantity());
        diary.addProduct(newProduct);
        user.updateLastlyAddedProducts(newProduct);
        newProduct.setLastlyAdded(true);
        newProduct.getNutrients().setProduct(newProduct);

        productsRepository.save(newProduct);
        return productMapper.mapToDto(newProduct);
    }

    public ProductDto editProductAmountInDiary(EditProductInDiaryDto editProductDto, Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        Diary diary = user.getDiary();

        Product product = productsRepository.findProductById(editProductDto.id())
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND_MESSAGE));

        product.changeAmount(editProductDto.measureLabel(), editProductDto.quantity());

        diary.calculateNutrientsSum();
        diary.calculateNutrientsLeft();

        return productMapper.mapToDto(product);
    }

    public void deleteProductFromDiary(DeleteProductDto deleteProductDto, Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        Diary diary = user.getDiary();

        Product product =  diary.getProducts().stream()
                .filter(p -> p.getId().equals(deleteProductDto.id()))
                .findFirst()
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND_MESSAGE));

        diary.removeProduct(product);
        user.getLastlySearchedProducts().remove(product);
    }
}
