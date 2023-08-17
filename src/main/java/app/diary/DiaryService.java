package app.diary;

import app.product.Product;
import app.product.ProductRepository;
import app.user.User;
import app.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static app.user.UserService.getUser;

@Service
@AllArgsConstructor
class DiaryService {

    private final ProductRepository productsRepository;
    private final UserRepository userRepository;
    private final ProductAddedToDiaryRepository productsAddedToDiaryRepository;

    @Transactional
    public ResponseEntity<Diary> getDiary(Authentication authentication) {
        Diary diary = getUser(userRepository, authentication).getDiary();
        diary.calculateNutrientsSum();
        diary.calculateNutrientsLeft();
        return ResponseEntity.ok(diary);
    }

    @Transactional
    public ResponseEntity<ProductAddedToDiary> addProductToDiary(AddProductToDiaryDto addProductDto, Authentication authentication) {
        Diary diary = getUser(userRepository, authentication).getDiary();
        Optional<Product> product = productsRepository.findProductEntityByProductIdAndName(addProductDto.foodId(), addProductDto.name());

        if (product.isEmpty()) {
            throw new UsernameNotFoundException("Product not found");
        }
        product.get().setUsed(true);

        ProductAddedToDiary productAddedToDiary = generateNewProductAddedToDiary(
                diary,
                product.get(),
                addProductDto.measureLabel(),
                addProductDto.quantity()
        );

        diary.getProducts().add(productAddedToDiary);

        diary.calculateNutrientsSum();
        diary.calculateNutrientsLeft();

        return ResponseEntity.ok(productAddedToDiary);
    }

    @Transactional
    public ResponseEntity<ProductAddedToDiary> editProductAmountInDiary(EditProductInDiaryDto editProductDto, Authentication authentication) {
        Diary diary = getUser(userRepository, authentication).getDiary();
        ProductAddedToDiary productInDiary = productsAddedToDiaryRepository.findById(editProductDto.id())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        Optional<Product> product = productsRepository.findProductEntityByProductIdAndName(productInDiary.getProductId(), productInDiary.getProductName());
        if (product.isEmpty()) {
            throw new RuntimeException("Product not found");
        }

        ProductAddedToDiary productWithNewValues = generateNewProductAddedToDiary(
                productInDiary.getDiary(),
                product.get(),
                editProductDto.measureLabel(),
                editProductDto.quantity()
        );

        productInDiary = ProductMapper.INSTANCE.ProductToProduct(productWithNewValues);

        diary.calculateNutrientsSum();
        diary.calculateNutrientsLeft();

        return ResponseEntity.ok(productInDiary);
    }

    @Transactional
    public ResponseEntity<String> deleteProductFromDiary(Long id, Authentication authentication) {
        Diary diary = getUser(userRepository, authentication).getDiary();
        ProductAddedToDiary productAddedToDiary = productsAddedToDiaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        productsAddedToDiaryRepository.delete(productAddedToDiary);

        Optional<Product> product = productsRepository.findProductEntityByProductIdAndName(productAddedToDiary.getProductId(), productAddedToDiary.getProductName());
        if (product.isEmpty()) {
            throw new RuntimeException("Product not found");
        }

        if (productsAddedToDiaryRepository.findProductAddedToDiaryByProductName(productAddedToDiary.getProductName()).isEmpty()) {
            product.get().setUsed(false);
        }

        diary.calculateNutrientsSum();
        diary.calculateNutrientsLeft();

        return ResponseEntity.ok("Product deleted from diary successfully");
    }

    private ProductAddedToDiary generateNewProductAddedToDiary(Diary diary, Product product, String measureLabel, double quantity) {

        String productId = product.getProductId();
        String productName = product.getName();
        double calories = product.getKcal() / 100 * product.getMeasures().get(measureLabel) * quantity;
        double proteins = product.getProtein() / 100 * product.getMeasures().get(measureLabel) * quantity;
        double fats = product.getFat() / 100 * product.getMeasures().get(measureLabel) * quantity;
        double carbs = product.getCarbohydrates() / 100 * product.getMeasures().get(measureLabel) * quantity;
        double fiber = product.getFiber() / 100 * product.getMeasures().get(measureLabel) * quantity;
        String image = product.getImage();

        ProductAddedToDiary.ProductAddedToDiaryBuilder productAddedToDiary = ProductAddedToDiary.builder()
                .diary(diary)
                .productId(productId)
                .productName(productName)
                .kcal(calories)
                .protein(proteins)
                .fat(fats)
                .carbohydrates(carbs)
                .fiber(fiber)
                .measureLabel(measureLabel)
                .quantity(quantity)
                .image(image);


        return productAddedToDiary.build();
    }

    private void setNewValuesToProductInDiary(ProductAddedToDiary productAddedToDiary, ProductAddedToDiary productWithNewValues) {
        productAddedToDiary.setKcal(productWithNewValues.getKcal());
        productAddedToDiary.setProtein(productWithNewValues.getProtein());
        productAddedToDiary.setCarbohydrates(productWithNewValues.getCarbohydrates());
        productAddedToDiary.setFat(productWithNewValues.getFat());
        productAddedToDiary.setFiber(productWithNewValues.getFiber());
        productAddedToDiary.setMeasureLabel(productWithNewValues.getMeasureLabel());
        productAddedToDiary.setQuantity(productWithNewValues.getQuantity());
    }

}
