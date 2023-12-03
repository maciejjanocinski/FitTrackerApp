package app.diary;

import app.diary.dto.*;
import app.product.ProductDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
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
    ProductDto addProductToDiary(@CurrentSecurityContext(expression = "authentication")
                                                                  Authentication authentication,
                                 @RequestBody AddProductToDiaryDto addProductDto) {
        return diaryService.addProductToDiary(addProductDto, authentication);
    }

    @PatchMapping("/product")
    ProductDto editProductAmountInDiary(@CurrentSecurityContext(expression = "authentication")
                                                                         Authentication authentication,
                                               @RequestBody EditProductInDiaryDto editProductDto) {
        return diaryService.editProductAmountInDiary(editProductDto, authentication);
    }

    @DeleteMapping("/product")
     String deleteProductFromDiary(@CurrentSecurityContext(expression = "authentication")
                                                          Authentication authentication,
                                                          @RequestBody DeleteProductDto deleteProductDto) {
        return diaryService.deleteProductFromDiary(deleteProductDto, authentication);
    }
}
