package app.diary;

import app.diary.dto.AddProductToDiaryDto;
import app.diary.dto.DiaryDto;
import app.diary.dto.EditProductInDiaryDto;
import app.diary.dto.ProductInDiaryDto;
import app.user.UserService;
import app.util.exceptions.ProductNotFoundException;
import app.product.Product;
import app.product.ProductRepository;
import app.user.User;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


@Service
@AllArgsConstructor
@Transactional
class DiaryService {

    private final ProductRepository productsRepository;
    private final UserService userService;
    private final ProductAddedToDiaryRepository productsAddedToDiaryRepository;
    private final ProductMapper productMapper;
    private final DiaryMapper diaryMapper;

    public DiaryDto getDiary(Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        //TODO implement class with static String fields like "User not found" and "Product not found"
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
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

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
        ProductInDiary productInDiary = productsAddedToDiaryRepository.findById(editProductDto.id())
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        Product product = productsRepository.findProductByProductIdAndName
                        (
                                productInDiary.getProductId(),
                                productInDiary.getProductName()
                        )
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

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

    public String deleteProductFromDiary(Long id, Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        Diary diary = user.getDiary();
        ProductInDiary productInDiary = productsAddedToDiaryRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        productsAddedToDiaryRepository.delete(productInDiary);

        Product product = productsRepository.findProductByProductIdAndName(
                        productInDiary.getProductId(),
                        productInDiary.getProductName()
                )
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        product.setUsed(false);

        diary.calculateNutrientsSum();
        diary.calculateNutrientsLeft();

        return "Product deleted from diary successfully";
    }
}
