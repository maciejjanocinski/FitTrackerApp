package app.services;

import app.dto.AddProductDto;
import app.models.*;
import app.repository.*;
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
    private final GoalsRepository goalsRepository;
    private final  DiaryRepository diaryRepository;
    private final NutrientsLeftToReachTodayGoalsRepository nutrientsLeftToReachTodayGoalsRepository;

    @Transactional
    public ResponseEntity<Diary> getDiary(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        NutrientsSum nutrientsSum = nutrientsSumRepository.getTotalNutrientsSum(user.getDiary().getId());
        user.getDiary().setNutrientsSum(nutrientsSum);

       NutrientsLeftToReachTodayGoals nutrientsLeftToReachTodayGoals = calculateNutrientsLeftToReachTodayGoals(user.getDiary().getGoals(), nutrientsSum);
       user.getDiary().setNutrientsLeftToReachTodayGoals(nutrientsLeftToReachTodayGoals);


        return ResponseEntity.ok(user.getDiary());
    }

    @Transactional
    public ResponseEntity<?> addProductToDiary(AddProductDto addProductDto, Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        Product product = productsRepository.findProductEntityByProductIdAndName(addProductDto.getFoodId(), addProductDto.getName());
        product.setUsed(true);



        ProductAddedToDiary productAddedToDiary = generateNewUsersProductsRecord(
                user.getDiary(),
                product,
                addProductDto.getMeasureLabel(),
                addProductDto.getQuantity()
        );

        user.getDiary().getProducts().add(productAddedToDiary);
        userRepository.save(user);

        return ResponseEntity.ok("ok");
    }

    //
//    public ResponseEntity<UsersProductsEntity> editProductAmountInDiary(EditProductDto editProductDto) {
//        UsersProductsEntity oldUsersProductsEntity = usersProductsRepository.findById(editProductDto.getUsersProductsId())
//                .orElseThrow(() -> new RuntimeException("Product not found"));
//
//        UsersProductsEntity newUsersProductsEntity = generateNewUsersProductsRecord(
//                productsRepository.findProductEntityByProductIdAndName(oldUsersProductsEntity.getProductId(), oldUsersProductsEntity.getProductName()),
//                oldUsersProductsEntity.getUserId(),
//                editProductDto.getMeasureLabel(),
//                editProductDto.getQuantity());
//
//        usersProductsRepository.deleteById(oldUsersProductsEntity.getUsersProductsId());
//        usersProductsRepository.save(newUsersProductsEntity);
//
//        return ResponseEntity.ok(newUsersProductsEntity);
//    }
//
//    @Transactional
//    public ResponseEntity<String> deleteProductFromDiary(Long usersProductsId) {
//        UsersProductsEntity usersProductsEntity = usersProductsRepository.findById(usersProductsId)
//                .orElseThrow(() -> new RuntimeException("Product not found"));
//
//        ProductEntity product = productsRepository.findProductEntityByProductIdAndName(
//                usersProductsEntity.getProductId(),
//                usersProductsEntity.getProductName());
//
//        product.setUsed(false);
//        usersProductsRepository.deleteById(usersProductsId);
//        return ResponseEntity.ok("Product deleted from diary successfully");
//    }
//
//
    private ProductAddedToDiary generateNewUsersProductsRecord(Diary diary, Product product, String measureLabel, double quantity) {

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

    private NutrientsLeftToReachTodayGoals calculateNutrientsLeftToReachTodayGoals(Goals goals, NutrientsSum nutrientsSum) {
        double caloriesLeft = goals.getKcal() - nutrientsSum.getTotalKcal();
        double proteinsLeft = goals.getProtein() - nutrientsSum.getTotalProtein();
        double fatsLeft = goals.getFat() - nutrientsSum.getTotalFat();
        double carbsLeft = goals.getCarbohydrates() - nutrientsSum.getTotalCarbohydrates();
        double fiberLeft = goals.getFiber() - nutrientsSum.getTotalFiber();

        return new NutrientsLeftToReachTodayGoals(caloriesLeft, proteinsLeft, fatsLeft, carbsLeft, fiberLeft);
    }


}
