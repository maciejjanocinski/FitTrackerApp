package app.services;

import app.dto.AddProductDto;
import app.dto.EditProductDto;
import app.models.*;
import app.repositories.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DiaryService {

    private final ProductsRepository productsRepository;
    private final UserRepository userRepository;
    private final NutrientsSumRepository nutrientsSumRepository;
    private final ProductsAddedToDiaryRepository productsAddedToDiaryRepository;

    @Transactional
    public ResponseEntity<Diary> getDiary(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        calculateNutrientsSum(user);
        calculateNutrientsLeftToReachTodayGoals(user);

        return ResponseEntity.ok(user.getDiary());
    }

    @Transactional
    public ResponseEntity<ProductAddedToDiary> addProductToDiary(AddProductDto addProductDto, Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Product product = productsRepository.findProductEntityByProductIdAndName(addProductDto.getFoodId(), addProductDto.getName());
        product.setUsed(true);

        ProductAddedToDiary productAddedToDiary = generateNewProductAddedToDiary(
                user.getDiary(),
                product,
                addProductDto.getMeasureLabel(),
                addProductDto.getQuantity()
        );

        user.getDiary().getProducts().add(productAddedToDiary);
        userRepository.save(user);

        return ResponseEntity.ok(productAddedToDiary);
    }

    public ResponseEntity<ProductAddedToDiary> editProductAmountInDiary(EditProductDto editProductDto) {
        ProductAddedToDiary oldProduct = productsAddedToDiaryRepository.findById(editProductDto.getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        ProductAddedToDiary newProduct = generateNewProductAddedToDiary(
                oldProduct.getDiary(),
                productsRepository.findProductEntityByProductIdAndName(oldProduct.getProductId(), oldProduct.getProductName()),
                editProductDto.getMeasureLabel(),
                editProductDto.getQuantity()
        );
        productsAddedToDiaryRepository.delete(oldProduct);
        productsAddedToDiaryRepository.save(newProduct);

        return ResponseEntity.ok(newProduct);
    }

    @Transactional
    public ResponseEntity<String> deleteProductFromDiary(Long id) {
        ProductAddedToDiary productAddedToDiary = productsAddedToDiaryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        Product product = productsRepository.findProductEntityByProductIdAndName(productAddedToDiary.getProductId(), productAddedToDiary.getProductName());
        productsAddedToDiaryRepository.delete(productAddedToDiary);

        if (productsAddedToDiaryRepository.findProductAddedToDiaryByProductName(productAddedToDiary.getProductName()).isEmpty()) {
            product.setUsed(false);
        }
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

        return new ProductAddedToDiary(productId, productName, calories, proteins, carbs, fats, fiber, image, measureLabel, quantity, diary);
    }

    private void calculateNutrientsLeftToReachTodayGoals(User user) {
        user.getDiary().getNutrientsLeftToReachTodayGoals().setKcal(user.getDiary().getGoals().getKcal() - user.getDiary().getNutrientsSum().getTotalKcal());
        user.getDiary().getNutrientsLeftToReachTodayGoals().setProtein(user.getDiary().getGoals().getProtein() - user.getDiary().getNutrientsSum().getTotalProtein());
        user.getDiary().getNutrientsLeftToReachTodayGoals().setCarbohydrates(user.getDiary().getGoals().getCarbohydrates() - user.getDiary().getNutrientsSum().getTotalCarbohydrates());
        user.getDiary().getNutrientsLeftToReachTodayGoals().setFat(user.getDiary().getGoals().getFat() - user.getDiary().getNutrientsSum().getTotalFat());
        user.getDiary().getNutrientsLeftToReachTodayGoals().setFiber(user.getDiary().getGoals().getFiber() - user.getDiary().getNutrientsSum().getTotalFiber());
    }

    private void calculateNutrientsSum(User user) {
        NutrientsSum nutrientsSum = nutrientsSumRepository.getTotalNutrientsSum(user.getDiary().getId());
        user.getDiary().getNutrientsSum().setTotalKcal(nutrientsSum.getTotalKcal());
        user.getDiary().getNutrientsSum().setTotalProtein(nutrientsSum.getTotalProtein());
        user.getDiary().getNutrientsSum().setTotalCarbohydrates(nutrientsSum.getTotalCarbohydrates());
        user.getDiary().getNutrientsSum().setTotalFat(nutrientsSum.getTotalFat());
        user.getDiary().getNutrientsSum().setTotalFiber(nutrientsSum.getTotalFiber());
    }
}
