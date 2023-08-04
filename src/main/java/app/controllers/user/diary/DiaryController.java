package app.controllers.user.diary;

import app.dto.AddProductDto;
import app.dto.EditProductDto;
import app.models.UserDiary;
import app.models.UsersProductsEntity;
import app.services.DiaryService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/user/diary")
public class DiaryController {

    private final DiaryService diaryService;

    @GetMapping("/")
    public ResponseEntity<UserDiary> getDiary() {
        return diaryService.getDiary();
    }

    @PostMapping("/addProduct")
    public ResponseEntity<UsersProductsEntity> addProductToDiary(@CurrentSecurityContext(expression = "authentication")
                                                    Authentication authentication,
                                                                 @RequestBody AddProductDto addProductDto) {
        return diaryService.addProductToDiary(addProductDto, authentication);
    }

    @PatchMapping("/editProduct")
    public ResponseEntity<UsersProductsEntity> editProductAmountInDiary(@RequestBody EditProductDto editProductDto) {
        return diaryService.editProductAmountInDiary(editProductDto);
    }

    @DeleteMapping("/deleteProduct")
    public ResponseEntity<String> deleteProductFromDiary(@RequestBody Long usersProductsId) {
        return diaryService.deleteProductFromDiary(usersProductsId);
    }
}
