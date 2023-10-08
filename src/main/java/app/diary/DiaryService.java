package app.diary;

import app.diary.dto.AddProductToDiaryDto;
import app.diary.dto.DiaryDto;
import app.diary.dto.EditProductInDiaryDto;
import app.diary.dto.ProductInDiaryDto;
import app.exceptions.ProductNotFoundException;
import app.product.Product;
import app.product.ProductRepository;
import app.user.User;
import app.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

import static app.user.UserService.getUser;

@Service
@AllArgsConstructor
class DiaryService {

    private final ProductRepository productsRepository;
    private final UserRepository userRepository;
    private final ProductAddedToDiaryRepository productsAddedToDiaryRepository;
    private final ProductMapper productMapper;
    private final DiaryMapper diaryMapper;

    @Transactional
    public DiaryDto getDiary(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Diary diary = user.getDiary();
        diary.calculateNutrientsSum();
        diary.calculateNutrientsLeft();

        return diaryMapper.mapDiaryToDiaryDto(diary);
    }

    @Transactional
    public ProductInDiaryDto addProductToDiary(AddProductToDiaryDto addProductDto, Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Diary diary = user.getDiary();
        Product product = productsRepository
                .findProductEntityByProductIdAndName(
                        addProductDto.foodId(),
                        addProductDto.name())
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        product.setUsed(true);

        ProductInDiary productInDiary = generateNewProductInDiary(
                diary,
                product,
                addProductDto.measureLabel(),
                addProductDto.quantity()
        );

        diary.addProduct(productInDiary);
        diary.calculateNutrientsSum();
        diary.calculateNutrientsLeft();

        return productMapper.mapToProductInDiaryDto(productInDiary);
    }

    @Transactional
    public ProductInDiaryDto editProductAmountInDiary(EditProductInDiaryDto editProductDto, Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Diary diary = user.getDiary();
        ProductInDiary productInDiary = productsAddedToDiaryRepository.findById(editProductDto.id())
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        Product product = productsRepository.findProductEntityByProductIdAndName
                        (
                                productInDiary.getProductId(),
                                productInDiary.getProductName()
                        )
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        ProductInDiary productWithNewValues = generateNewProductInDiary(
                productInDiary.getDiary(),
                product,
                editProductDto.measureLabel(),
                editProductDto.quantity()
        );

        productMapper.mapToProductInDiary(productWithNewValues, productInDiary);

        diary.calculateNutrientsSum();
        diary.calculateNutrientsLeft();

        return productMapper.mapToProductInDiaryDto(productInDiary);
    }

    @Transactional
    public String deleteProductFromDiary(Long id, Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Diary diary = user.getDiary();
        ProductInDiary productInDiary = productsAddedToDiaryRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));
        productsAddedToDiaryRepository.delete(productInDiary);

        Product product = productsRepository.findProductEntityByProductIdAndName(
                productInDiary.getProductId(),
                productInDiary.getProductName()
        )
                .orElseThrow(() -> new ProductNotFoundException("Product not found"));

        product.setUsed(false);

        diary.calculateNutrientsSum();
        diary.calculateNutrientsLeft();

        return "Product deleted from diary successfully";
    }

    ProductInDiary generateNewProductInDiary(Diary diary, Product product, String measureLabel, BigDecimal quantity) {

        String productId = product.getProductId();
        String productName = product.getName();
        BigDecimal calories = product.getKcal().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP).multiply(product.getMeasures().get(measureLabel)).multiply(quantity);
        BigDecimal proteins = product.getProtein().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP).multiply(product.getMeasures().get(measureLabel)).multiply(quantity);
        BigDecimal carbs = product.getCarbohydrates().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP).multiply(product.getMeasures().get(measureLabel)).multiply(quantity);
        BigDecimal fats = product.getFat().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP).multiply(product.getMeasures().get(measureLabel).multiply(quantity));
        BigDecimal fiber = product.getFiber().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP).multiply(product.getMeasures().get(measureLabel)).multiply(quantity);
        String image = product.getImage();

        ProductInDiary.ProductInDiaryBuilder productInDiary = ProductInDiary.builder()
                .diary(diary)
                .productId(productId)
                .productName(productName)
                .kcal(calories)
                .protein(proteins)
                .carbohydrates(carbs)
                .fat(fats)
                .fiber(fiber)
                .measureLabel(measureLabel)
                .quantity(quantity)
                .image(image);


        return productInDiary.build();
    }

}
