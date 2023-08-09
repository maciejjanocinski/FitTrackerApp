package app.controllers.diary;

import app.dto.AddProductDto;
import app.dto.EditProductDto;
import app.models.Diary;
import app.models.ProductAddedToDiary;
import app.services.DiaryService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/diary")
public class DiaryController {

    private final DiaryService diaryService;

    @GetMapping("/")
    public ResponseEntity<Diary> getDiary(@CurrentSecurityContext(expression = "authentication")
                                          Authentication authentication) {
        return diaryService.getDiary(authentication);
    }

    @PostMapping("/addProduct")
    public ResponseEntity<ProductAddedToDiary> addProductToDiary(@CurrentSecurityContext(expression = "authentication")
                                                                 Authentication authentication,
                                                                 @RequestBody AddProductDto addProductDto) {
        return diaryService.addProductToDiary(addProductDto, authentication);
    }

    @PatchMapping("/editProduct")
    public ResponseEntity<ProductAddedToDiary> editProductAmountInDiary(@RequestBody EditProductDto editProductDto) {
        return diaryService.editProductAmountInDiary(editProductDto);
    }

    @DeleteMapping("/deleteProduct")
    public ResponseEntity<String> deleteProductFromDiary(@RequestBody Long usersProductsId) {
        return diaryService.deleteProductFromDiary(usersProductsId);
    }
}
