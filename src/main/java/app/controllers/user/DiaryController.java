package app.controllers.user;

import app.dto.AddProductDto;
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

    DiaryService diaryService;

    @PostMapping("/addProduct")
    public ResponseEntity<?> getDiaryMainPage(@CurrentSecurityContext(expression = "authentication")
                                                  Authentication authentication,
                                              @RequestBody AddProductDto addProductDto) {

        return diaryService.addProductToDiary(addProductDto, authentication);
    }
}
