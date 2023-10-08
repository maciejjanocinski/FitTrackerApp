package app.diary;

import app.diary.dto.AddProductToDiaryDto;
import app.diary.dto.DiaryDto;
import app.diary.dto.EditProductInDiaryDto;
import app.diary.dto.ProductInDiaryDto;
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
    ProductInDiaryDto addProductToDiary(@CurrentSecurityContext(expression = "authentication")
                                                                  Authentication authentication,
                                        @RequestBody AddProductToDiaryDto addProductDto) {
        return diaryService.addProductToDiary(addProductDto, authentication);
    }

    @PatchMapping("/product")
    ProductInDiaryDto editProductAmountInDiary(@CurrentSecurityContext(expression = "authentication")
                                                                         Authentication authentication,
                                               @RequestBody EditProductInDiaryDto editProductDto) {
        return diaryService.editProductAmountInDiary(editProductDto, authentication);
    }

    @DeleteMapping("/product")
     String deleteProductFromDiary(@CurrentSecurityContext(expression = "authentication")
                                                          Authentication authentication,
                                                          @RequestBody Long usersProductsId) {
        return diaryService.deleteProductFromDiary(usersProductsId, authentication);
    }
}
