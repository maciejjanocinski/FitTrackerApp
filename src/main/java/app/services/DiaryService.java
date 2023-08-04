package app.services;

import app.dto.AddProductDto;
import app.dto.EditProductDto;
import app.models.*;
import app.repository.ProductsRepository;
import app.repository.UsedProductsRepository;
import app.repository.UserRepository;
import app.repository.UsersProductsRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DiaryService {


    UsersProductsRepository usersProductsRepository;
    ProductsRepository productsRepository;
    UsedProductsRepository usedProductsRepository;
    UserRepository userRepository;

    public ResponseEntity<String> addProductToDiary(AddProductDto addProductDto, Authentication authentication) {
        UserEntity user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        ProductEntity product = productsRepository.findById(addProductDto.getFoodId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        UsersProductsEntity usersProductsEntity = generateNewUsersProductsRecord(
                product,
                user.getId(),
                addProductDto.getMeasureLabel(),
                addProductDto.getQuanity());

        UsedProductsEntity usedProductsEntity = createNewUsedProductsEntity(product);

        usersProductsRepository.save(usersProductsEntity);
       // usedProductsRepository.save(usedProductsEntity);

        return ResponseEntity.ok("Product: " +
                product.getName() +
                addProductDto.getQuanity() +
                addProductDto.getMeasureLabel() +
                " added to diary successfully");
    }

    public ResponseEntity<String> editProductAmountInDiary(EditProductDto editProductDto) {
        UsersProductsEntity oldUsersProductsEntity = usersProductsRepository.findById(editProductDto.getUsersProductsId())
                .orElseThrow(() -> new RuntimeException("Product not found"));
        UsedProductsEntity usedProductsEntity = usedProductsRepository.findById(editProductDto.getUsersProductsId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        UsersProductsEntity newUsersProductsEntity = generateNewUsersProductsRecord(
                usedProductsEntity,
                oldUsersProductsEntity.getUserId(),
                editProductDto.getMeasureLabel(),
                editProductDto.getQuantity());

        usersProductsRepository.deleteById(oldUsersProductsEntity.getUsersProductsId());
        usersProductsRepository.save(newUsersProductsEntity);

        return ResponseEntity.ok("Edited successfully");
    }

    public ResponseEntity<String> deleteProductFromDiary(Long usersProductsId) {
        usersProductsRepository.deleteById(usersProductsId);
        usedProductsRepository.deleteById(usersProductsId);
        return ResponseEntity.ok("Product deleted from diary successfully");
    }


    private UsersProductsEntity generateNewUsersProductsRecord(ProductEntity product, Long id, String measureLabel, double quantity) {
        long userId = id;
        String productId = product.getId();
        double calories = product.getKcal() / 100 * product.getMeasures().get(measureLabel) * quantity;
        double proteins = product.getProtein() / 100 * product.getMeasures().get(measureLabel) * quantity;
        double fats = product.getFat() / 100 * product.getMeasures().get(measureLabel) * quantity;
        double carbs = product.getCarbohydrates() / 100 * product.getMeasures().get(measureLabel) * quantity;
        double fiber = product.getFiber() / 100 * product.getMeasures().get(measureLabel) * quantity;
        String image = product.getImage();

        return new UsersProductsEntity(userId, productId, calories, proteins, fats, carbs, fiber, image, measureLabel, quantity);
    }

    private UsedProductsEntity createNewUsedProductsEntity(ProductEntity product) {
        return new UsedProductsEntity(
                product.getId(),
                product.getName(),
                product.getKcal(),
                product.getProtein(),
                product.getFat(),
                product.getCarbohydrates(),
                product.getFiber(),
                product.getImage(),
                product.getMeasures());
    }


}
