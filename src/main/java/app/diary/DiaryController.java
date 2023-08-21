package app.diary;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
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
     ResponseEntity<DiaryDto> getDiary(@CurrentSecurityContext(expression = "authentication")
                                           Authentication authentication) {
        return diaryService.getDiary(authentication);
    }

    @PostMapping("/product")
     ResponseEntity<ProductAddedToDiaryDto> addProductToDiary(@CurrentSecurityContext(expression = "authentication")
                                                                  Authentication authentication,
                                                                  @RequestBody AddProductToDiaryDto addProductDto) {
        return diaryService.addProductToDiary(addProductDto, authentication);
    }

    @PatchMapping("/product")
     ResponseEntity<ProductAddedToDiaryDto> editProductAmountInDiary(@CurrentSecurityContext(expression = "authentication")
                                                                         Authentication authentication,
                                                                         @RequestBody EditProductInDiaryDto editProductDto) {
        return diaryService.editProductAmountInDiary(editProductDto, authentication);
    }

    @DeleteMapping("/product")
     ResponseEntity<String> deleteProductFromDiary(@CurrentSecurityContext(expression = "authentication")
                                                          Authentication authentication,
                                                          @RequestBody Long usersProductsId) {
        return diaryService.deleteProductFromDiary(usersProductsId, authentication);
    }
}
