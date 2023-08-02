package app.services;

import app.dto.AddProductDto;
import app.models.*;
import app.repository.ProductsRepository;
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
    UserRepository userRepository;

    @Transactional
    public ResponseEntity<?> addProductToDiary(AddProductDto addProductDto, Authentication authentication) {
        UserEntity user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        ProductEntity product = productsRepository.findById(addProductDto.getFoodId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        UsersProductsEntity usersProducts = new UsersProductsEntity();



        usersProductsRepository.save(usersProducts);

        return ResponseEntity.ok("Product added to diary successfully for user" + user.getUsername());

    }


}
