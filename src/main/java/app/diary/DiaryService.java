package app.diary;

import app.diary.dto.*;
import app.user.UserService;
import app.util.exceptions.ProductNotFoundException;
import app.product.Product;
import app.product.ProductRepository;
import app.user.User;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import static app.util.UtilityClass.productNotFoundMessage;


@Service
@AllArgsConstructor
@Transactional
class DiaryService {

    private final ProductRepository productsRepository;
    private final UserService userService;
    private final ProductsInDiaryRepository productsInDiaryRepository;
    private final ProductMapper productMapper;
    private final DiaryMapper diaryMapper;

    public DiaryDto getDiary(Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        Diary diary = user.getDiary();
        diary.calculateNutrientsSum();
        diary.calculateNutrientsLeft();

        return diaryMapper.mapDiaryToDiaryDto(diary);
    }

    public ProductInDiaryDto addProductToDiary(AddProductToDiaryDto addProductDto, Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        Diary diary = user.getDiary();
        Product product = productsRepository
                .findProductByProductIdAndName(
                        addProductDto.foodId(),
                        addProductDto.name())
                .orElseThrow(() -> new ProductNotFoundException(productNotFoundMessage));

        product.setUsed(true);

        ProductInDiary productInDiary = diary.generateNewProductInDiary(
                product,
                addProductDto.measureLabel(),
                addProductDto.quantity()
        );

        diary.addProduct(productInDiary);
        return productMapper.mapToProductInDiaryDto(productInDiary);
    }

    public ProductInDiaryDto editProductAmountInDiary(EditProductInDiaryDto editProductDto, Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        Diary diary = user.getDiary();
        ProductInDiary productInDiary = productsInDiaryRepository.findById(editProductDto.id())
                .orElseThrow(() -> new ProductNotFoundException(productNotFoundMessage));
        Product product = productsRepository.findProductByProductIdAndName
                        (
                                productInDiary.getProductId(),
                                productInDiary.getProductName()
                        )
                .orElseThrow(() -> new ProductNotFoundException(productNotFoundMessage));

        ProductInDiary productWithNewValues = diary.generateNewProductInDiary(
                product,
                editProductDto.measureLabel(),
                editProductDto.quantity()
        );

        productMapper.mapToProductInDiary(productWithNewValues, productInDiary);

        diary.calculateNutrientsSum();
        diary.calculateNutrientsLeft();

        return productMapper.mapToProductInDiaryDto(productInDiary);
    }

    public String deleteProductFromDiary(DeleteProductDto deleteProductDto, Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        Diary diary = user.getDiary();
        ProductInDiary productInDiary = productsInDiaryRepository.findById(deleteProductDto.id())
                .orElseThrow(() -> new ProductNotFoundException(productNotFoundMessage));
        diary.removeProduct(productInDiary);
        productsInDiaryRepository.deleteProductInDiaryById(productInDiary.getId());

        Product product = productsRepository.findProductByProductIdAndName(
                        productInDiary.getProductId(),
                        productInDiary.getProductName()
                )
                .orElseThrow(() -> new ProductNotFoundException(productNotFoundMessage));

        product.setUsed(false);
        return "Product deleted from diary successfully";
    }
}
