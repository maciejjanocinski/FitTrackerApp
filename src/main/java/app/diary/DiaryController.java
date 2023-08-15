package app.diary;

import app.productAddedToDiary.ProductAddedToDiary;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/diary")
class DiaryController {

    private final DiaryService diaryService;

    @GetMapping("/")
    private ResponseEntity<Diary> getDiary(@CurrentSecurityContext(expression = "authentication")
                                           Authentication authentication) {
        return diaryService.getDiary(authentication);
    }

    @PostMapping("/addProduct")
    private ResponseEntity<ProductAddedToDiary> addProductToDiary(@CurrentSecurityContext(expression = "authentication")
                                                                  Authentication authentication,
                                                                  @RequestBody AddProductToDiaryDto addProductDto) {
        return diaryService.addProductToDiary(addProductDto, authentication);
    }

    @PatchMapping("/editProduct")
    private ResponseEntity<ProductAddedToDiary> editProductAmountInDiary(@CurrentSecurityContext(expression = "authentication")
                                                                         Authentication authentication,
                                                                         @RequestBody EditProductInDiaryDto editProductDto) {
        return diaryService.editProductAmountInDiary(editProductDto, authentication);
    }

    @DeleteMapping("/deleteProduct")
    private ResponseEntity<String> deleteProductFromDiary(@CurrentSecurityContext(expression = "authentication")
                                                          Authentication authentication,
                                                          @RequestBody Long usersProductsId) {
        return diaryService.deleteProductFromDiary(usersProductsId, authentication);
    }
}
