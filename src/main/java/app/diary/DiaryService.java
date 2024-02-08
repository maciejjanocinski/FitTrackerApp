package app.diary;

import app.diary.dto.*;
import app.ingredient.Ingredient;
import app.ingredient.IngredientDto;
import app.ingredient.IngredientMapper;
import app.user.UserService;
import app.exceptions.ProductNotFoundException;
import app.ingredient.IngredientRepository;
import app.user.User;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import static app.util.Utils.PRODUCT_NOT_FOUND_MESSAGE;


@Service
@AllArgsConstructor
@Transactional
public class DiaryService {

    private final IngredientRepository productsRepository;
    private final UserService userService;
    private final DiaryMapper diaryMapper = DiaryMapper.INSTANCE;
    private final IngredientMapper ingredientMapper = IngredientMapper.INSTANCE;

    public DiaryDto getDiary(Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        Diary diary = user.getDiary();
        diary.calculateNutrientsSum();
        diary.calculateNutrientsLeft();

        return diaryMapper.mapToDto(diary);
    }
    public IngredientDto addProductToDiary(AddProductToDiaryDto addProductDto, Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        Diary diary = user.getDiary();
        Ingredient ingredient = user.getLastlySearchedIngredients().stream()
                .filter(p -> p.getId().equals(addProductDto.id()))
                .findFirst()
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND_MESSAGE));

        Ingredient newIngredient = new Ingredient(ingredient);
        newIngredient.setDiary(diary);
        newIngredient.setUser(user);
        newIngredient.changeAmount(addProductDto.measureLabel(), addProductDto.quantity());
        diary.addProduct(newIngredient);
        user.updateLastlyAddedProducts(newIngredient);
        newIngredient.setLastlyAdded(true);
        newIngredient.getNutrients().setIngredient(newIngredient);

        productsRepository.save(newIngredient);
        return ingredientMapper.mapToDto(newIngredient);
    }

    public IngredientDto editProductAmountInDiary(EditProductInDiaryDto editProductDto, Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        Diary diary = user.getDiary();

        Ingredient ingredient = productsRepository.findProductById(editProductDto.id())
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND_MESSAGE));

        ingredient.changeAmount(editProductDto.measureLabel(), editProductDto.quantity());

        diary.calculateNutrientsSum();
        diary.calculateNutrientsLeft();

        return ingredientMapper.mapToDto(ingredient);
    }

    public void deleteProductFromDiary(DeleteProductDto deleteProductDto, Authentication authentication) {
        User user = userService.getUserByUsername(authentication.getName());
        Diary diary = user.getDiary();

        Ingredient ingredient =  diary.getIngredients().stream()
                .filter(p -> p.getId().equals(deleteProductDto.id()))
                .findFirst()
                .orElseThrow(() -> new ProductNotFoundException(PRODUCT_NOT_FOUND_MESSAGE));

        diary.removeProduct(ingredient);
        user.getLastlySearchedIngredients().remove(ingredient);
    }
}
