package app.services;

import app.dto.AddProductDto;
import app.dto.EditProductDto;
import app.models.*;
import app.repository.ProductsRepository;
import app.repository.UserRepository;
import app.repository.UsersProductsRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DiaryService {


    UsersProductsRepository usersProductsRepository;
    ProductsRepository productsRepository;
    UserRepository userRepository;


    public ResponseEntity<UserDiary> getDiary() {
        NutrientsSum nutrientsSum = usersProductsRepository.sumNutrients();
        List<UsersProductsEntity> products = usersProductsRepository.findAll();
        UserDiary diary = new UserDiary(nutrientsSum, products);

        return ResponseEntity.ok(diary);
    }

    @Transactional
    public ResponseEntity<UsersProductsEntity> addProductToDiary(AddProductDto addProductDto, Authentication authentication) {
        UserEntity user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        ProductEntity product = productsRepository.findById(addProductDto.getFoodId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        UsersProductsEntity usersProductsEntity = generateNewUsersProductsRecord(
                product,
                user.getId(),
                addProductDto.getMeasureLabel(),
                addProductDto.getQuantity());

        product.setUsed(true);
        usersProductsRepository.save(usersProductsEntity);

        return ResponseEntity.ok(usersProductsEntity);
    }

    public ResponseEntity<UsersProductsEntity> editProductAmountInDiary(EditProductDto editProductDto) {
        UsersProductsEntity oldUsersProductsEntity = usersProductsRepository.findById(editProductDto.getUsersProductsId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        UsersProductsEntity newUsersProductsEntity = generateNewUsersProductsRecord(
                productsRepository.findById(oldUsersProductsEntity.getProductId()).orElseThrow(() -> new RuntimeException("Product not found")),
                oldUsersProductsEntity.getUserId(),
                editProductDto.getMeasureLabel(),
                editProductDto.getQuantity());

        usersProductsRepository.deleteById(oldUsersProductsEntity.getUsersProductsId());
        usersProductsRepository.save(newUsersProductsEntity);

        return ResponseEntity.ok(newUsersProductsEntity);
    }

    @Transactional
    public ResponseEntity<String> deleteProductFromDiary(Long usersProductsId) {
        UsersProductsEntity usersProductsEntity = usersProductsRepository.findById(usersProductsId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        ProductEntity product = productsRepository.findById(usersProductsEntity.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        product.setUsed(false);
        usersProductsRepository.deleteById(usersProductsId);
        return ResponseEntity.ok("Product deleted from diary successfully");
    }


    private UsersProductsEntity generateNewUsersProductsRecord(ProductEntity product, Long id, String measureLabel, double quantity) {
        long userId = id;
        String productId = product.getId();
        String productName = product.getName();
        double calories = product.getKcal() / 100 * product.getMeasures().get(measureLabel) * quantity;
        double proteins = product.getProtein() / 100 * product.getMeasures().get(measureLabel) * quantity;
        double fats = product.getFat() / 100 * product.getMeasures().get(measureLabel) * quantity;
        double carbs = product.getCarbohydrates() / 100 * product.getMeasures().get(measureLabel) * quantity;
        double fiber = product.getFiber() / 100 * product.getMeasures().get(measureLabel) * quantity;
        String image = product.getImage();

        return new UsersProductsEntity(userId, productId, calories, proteins, fats, carbs, fiber, image, measureLabel, quantity);
    }


}
