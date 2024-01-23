package app.user;

import app.product.Product;
import app.product.ProductDto;
import app.user.dto.DeleteUserDto;
import app.user.dto.UpdatePasswordDto;
import app.user.dto.UpdateProfileInfoDto;
import app.user.dto.UserDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@AllArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/user")
class UserController {

    private final UserService userService;

    @GetMapping("/")
    UserDto getProfile(@CurrentSecurityContext(expression = "authentication")
                                         Authentication authentication) {
        return userService.getUser(authentication);
    }

    @GetMapping("/lastly-added-products")
    List<ProductDto> getLastlyUsedProducts(@CurrentSecurityContext(expression = "authentication")
                    Authentication authentication) {
        return userService.getlastlyAddedProducts(authentication);
    }

    @PatchMapping("/")
     String updateProfile(@CurrentSecurityContext(expression = "authentication")
                                                 Authentication authentication,
                                                 @RequestBody
                                                 @Valid
                                                 UpdateProfileInfoDto updates) {
        return userService.updateProfile(authentication, updates);
    }

    @PatchMapping("/password")
     String updatePassword(@CurrentSecurityContext(expression = "authentication")
                                                  Authentication authentication,
                                                  @RequestBody
                                                  @Valid
                                                  UpdatePasswordDto password){
        return userService.updatePassword(authentication,password);
    }

    @DeleteMapping("/")
     String deleteProfile(@CurrentSecurityContext(expression = "authentication")
                                                 Authentication authentication,
                                                 @RequestBody
                                                 @Valid
                                                 DeleteUserDto deleteUserDto) {
        return userService.deleteProfile(authentication, deleteUserDto);
    }

}
