package app.diary;

import app.diary.dto.AddProductToDiaryDto;
import app.diary.dto.DiaryDto;
import app.diary.dto.EditProductInDiaryDto;
import app.diary.dto.ProductInDiaryDto;
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
        Optional<Product> product = productsRepository
                .findProductEntityByProductIdAndName(
                        addProductDto.foodId(),
                        addProductDto.name()
                );

        if (product.isEmpty()) {
            throw new UsernameNotFoundException("Product not found");
        }
        product.get().setUsed(true);

        ProductInDiary productInDiary = generateNewProductAddedToDiary(
                diary,
                product.get(),
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
        Diary diary = getUser(userRepository, authentication).getDiary();
        ProductInDiary productInDiary = productsAddedToDiaryRepository.findById(editProductDto.id())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        Optional<Product> product = productsRepository.findProductEntityByProductIdAndName(productInDiary.getProductId(), productInDiary.getProductName());
        if (product.isEmpty()) {
            throw new RuntimeException("Product not found");
        }

        ProductInDiary productWithNewValues = generateNewProductAddedToDiary(
                productInDiary.getDiary(),
                product.get(),
                editProductDto.measureLabel(),
                editProductDto.quantity()
        );

        productMapper.mapToProductAddedToDiary(productWithNewValues, productInDiary);

        diary.calculateNutrientsSum();
        diary.calculateNutrientsLeft();

        return productMapper.mapToProductInDiaryDto(productInDiary);
    }

    @Transactional
    public String deleteProductFromDiary(Long id, Authentication authentication) {
        Diary diary = getUser(userRepository, authentication).getDiary();
        ProductInDiary productInDiary = productsAddedToDiaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        productsAddedToDiaryRepository.delete(productInDiary);

        Optional<Product> product = productsRepository.findProductEntityByProductIdAndName(productInDiary.getProductId(), productInDiary.getProductName());
        if (product.isEmpty()) {
            throw new RuntimeException("Product not found");
        }

        if (productsAddedToDiaryRepository.findProductAddedToDiaryByProductName(productInDiary.getProductName()).isEmpty()) {
            product.get().setUsed(false);
        }

        diary.calculateNutrientsSum();
        diary.calculateNutrientsLeft();

        return "Product deleted from diary successfully";
    }

    ProductInDiary generateNewProductAddedToDiary(Diary diary, Product product, String measureLabel, BigDecimal quantity) {

        String productId = product.getProductId();
        String productName = product.getName();
        BigDecimal calories = product.getKcal().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP).multiply(product.getMeasures().get(measureLabel)).multiply(quantity);
        BigDecimal proteins = product.getProtein().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP).multiply(product.getMeasures().get(measureLabel)).multiply(quantity);
        BigDecimal carbs = product.getCarbohydrates().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP).multiply(product.getMeasures().get(measureLabel)).multiply(quantity);
        BigDecimal fats = product.getFat().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP).multiply(product.getMeasures().get(measureLabel).multiply(quantity));
        BigDecimal fiber = product.getFiber().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP).multiply(product.getMeasures().get(measureLabel)).multiply(quantity);
        String image = product.getImage();

        ProductInDiary.ProductInDiaryBuilder productAddedToDiary = ProductInDiary.builder()
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


        return productAddedToDiary.build();
    }

}
