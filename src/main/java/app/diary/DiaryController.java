package app.diary;

import app.diary.dto.AddProductToDiaryDto;
import app.diary.dto.DeleteProductDto;
import app.diary.dto.DiaryDto;
import app.diary.dto.EditProductInDiaryDto;
import app.ingredient.IngredientDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/diary")
class DiaryController {

    private final DiaryService diaryService;

    @GetMapping("/")
    DiaryDto getDiary(@CurrentSecurityContext(expression = "authentication")
                                           Authentication authentication) {
        return diaryService.getDiary(authentication);
    }

    @PostMapping("/product")
    IngredientDto addProductToDiary(@CurrentSecurityContext(expression = "authentication")
                                                                  Authentication authentication,
                                    @RequestBody @Valid AddProductToDiaryDto addProductDto) {
        return diaryService.addProductToDiary(addProductDto, authentication);
    }

    @PatchMapping("/product")
    IngredientDto editProductAmountInDiary(@CurrentSecurityContext(expression = "authentication")
                                                                         Authentication authentication,
                                           @RequestBody @Valid EditProductInDiaryDto editProductDto) {
        return diaryService.editProductAmountInDiary(editProductDto, authentication);
    }

    @DeleteMapping("/product")
     void deleteProductFromDiary(@CurrentSecurityContext(expression = "authentication")
                                                          Authentication authentication,
                                                          @RequestBody DeleteProductDto deleteProductDto) {
        diaryService.deleteProductFromDiary(deleteProductDto, authentication);
    }
}
