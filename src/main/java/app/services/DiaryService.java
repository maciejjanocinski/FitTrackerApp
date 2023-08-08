package app.services;

import app.dto.AddProductDto;
import app.dto.EditProductDto;
import app.models.*;
import app.repositories.*;
//import app.repository.UsersProductsRepository;
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

        NutrientsLeftToReachTodayGoals nutrientsLeftToReachTodayGoals =
                calculateNutrientsLeftToReachTodayGoals(
                        user.getDiary().getNutrientsLeftToReachTodayGoals(),
                        user.getDiary().getGoals(),
                        user.getDiary().getNutrientsSum()
                );
        user.getDiary().setNutrientsLeftToReachTodayGoals(nutrientsLeftToReachTodayGoals);

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
        System.out.println(editProductDto.getId());
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

        return ResponseEntity.ok(null);
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

    private NutrientsLeftToReachTodayGoals calculateNutrientsLeftToReachTodayGoals(NutrientsLeftToReachTodayGoals nutrientsLeft, Goals goals, NutrientsSum nutrientsSum) {
        nutrientsLeft.setKcal(goals.getKcal() - nutrientsSum.getTotalKcal());
        nutrientsLeft.setProtein(goals.getProtein() - nutrientsSum.getTotalProtein());
        nutrientsLeft.setCarbohydrates(goals.getCarbohydrates() - nutrientsSum.getTotalCarbohydrates());
        nutrientsLeft.setFat(goals.getFat() - nutrientsSum.getTotalFat());
        nutrientsLeft.setFiber(goals.getFiber() - nutrientsSum.getTotalFiber());
        return nutrientsLeft;
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
