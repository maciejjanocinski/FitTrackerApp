package app.diary;

import app.diary.dto.AddProductToDiaryDto;
import app.diary.dto.DiaryDto;
import app.diary.dto.EditProductInDiaryDto;
import app.diary.dto.ProductInDiaryDto;
import app.exceptions.ProductNotFoundException;
import app.product.Product;
import app.product.ProductRepository;
import app.user.User;
import app.user.UserRepository;
import app.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiaryServiceTest {
    @Mock
    private ProductRepository productsRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ProductAddedToDiaryRepository productsAddedToDiaryRepository;
    @Mock
    private ProductMapper productMapper;
    @Mock
    private DiaryMapper diaryMapper;
    @Mock
    private Authentication authentication;
    @Mock
    private Diary diary;
    @InjectMocks
    private DiaryService diaryService;
    private final String username = new TestUtils().getUsername();

    @Test
    void getDiary_inputDataOk_returnsDiaryDto() {
        //given
        DiaryDto expectedResponse = buildDiaryDto();
        User user = buildUser();

        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(diaryMapper.mapDiaryToDiaryDto(diary)).thenReturn(expectedResponse);

        //when
        DiaryDto diaryDto = diaryService.getDiary(authentication);

        //then
        assertEquals(expectedResponse, diaryDto);

        verify(authentication).getName();
        verify(userRepository).findByUsername(username);
        verify(diary).calculateNutrientsLeft();
        verify(diary).calculateNutrientsSum();
        verify(diaryMapper).mapDiaryToDiaryDto(diary);
    }

    @Test
    void getDiary_userNotFound_throwsUserNotFoundException() {
        //given

        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        //when
        Exception ex = assertThrows(UsernameNotFoundException.class,
                () -> diaryService.getDiary(authentication));

        //then
        assertEquals("User not found", ex.getMessage());

        verify(authentication).getName();
        verify(userRepository).findByUsername(username);
        verify(diary, never()).calculateNutrientsLeft();
        verify(diary, never()).calculateNutrientsSum();
        verify(diaryMapper, never()).mapDiaryToDiaryDto(diary);
    }


    @Test
    void addProductToDiary_inputDataOk_returnsProductInDiaryDto() {
        //given
        AddProductToDiaryDto addProductDto = buildAddProductToDiaryDto();
        Product product = buildProduct(addProductDto.foodId());
        User user = buildUser();
        ProductInDiaryDto expectedResponse = buildProductInDiaryDto();

        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(productsRepository.findProductByProductIdAndName(addProductDto.foodId(), addProductDto.name()))
                .thenReturn(Optional.of(product));
        when(productMapper.mapToProductInDiaryDto(any(ProductInDiary.class)))
                .thenReturn(buildProductInDiaryDto());

        //when
        ProductInDiaryDto productInDiaryDto = diaryService.addProductToDiary(addProductDto, authentication);

        //then
        assertEquals(expectedResponse, productInDiaryDto);

        verify(authentication).getName();
        verify(userRepository).findByUsername(username);
        verify(productsRepository).findProductByProductIdAndName(addProductDto.foodId(), addProductDto.name());
        verify(diary).addProduct(any(ProductInDiary.class));
    }

    @Test
    void addProductToDiary_userNotFound_throwsUserNotFoundException() {
        //given
        AddProductToDiaryDto addProductDto = buildAddProductToDiaryDto();

        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        //when
        Exception ex = assertThrows(UsernameNotFoundException.class,
                () -> diaryService.addProductToDiary(addProductDto, authentication));

        //then
        assertEquals("User not found", ex.getMessage());

        verify(authentication).getName();
        verify(userRepository).findByUsername(username);
        verify(productsRepository, never()).findProductByProductIdAndName(addProductDto.foodId(), addProductDto.name());
        verify(diary, never()).addProduct(any(ProductInDiary.class));
        verify(diary, never()).calculateNutrientsLeft();
        verify(diary, never()).calculateNutrientsSum();
        verify(productMapper, never()).mapToProductInDiaryDto(any(ProductInDiary.class));
    }

    @Test
    void addProductToDiary_productNotFound_throwsProductNotFoundException() {
        //given
        AddProductToDiaryDto addProductDto = buildAddProductToDiaryDto();
        User user = buildUser();

        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(productsRepository.findProductByProductIdAndName(addProductDto.foodId(), addProductDto.name()))
                .thenReturn(Optional.empty());

        //when
        Exception ex = assertThrows(ProductNotFoundException.class,
                () -> diaryService.addProductToDiary(addProductDto, authentication));

        //then
        assertEquals("Product not found", ex.getMessage());

        verify(authentication).getName();
        verify(userRepository).findByUsername(username);
        verify(productsRepository).findProductByProductIdAndName(addProductDto.foodId(), addProductDto.name());
        verify(diary, never()).addProduct(any(ProductInDiary.class));
        verify(diary, never()).calculateNutrientsLeft();
        verify(diary, never()).calculateNutrientsSum();
        verify(productMapper, never()).mapToProductInDiaryDto(any(ProductInDiary.class));
    }

    @Test
    void editProductAmountInDiary_inputDataOk_returnsProductInDiaryDto() {
        //given
        AddProductToDiaryDto addProductDto = buildAddProductToDiaryDto();
        Product product = buildProduct(addProductDto.foodId());
        User user = buildUser();
        ProductInDiaryDto expectedResponse = buildProductInDiaryDto();
        EditProductInDiaryDto editProductInDiaryDto = buildEditProductInDiaryDto();
        ProductInDiary productInDiary = buildProductInDiary();

        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(productsAddedToDiaryRepository.findById(editProductInDiaryDto.id()))
                .thenReturn(Optional.of(productInDiary));
        when(productsRepository.findProductByProductIdAndName(productInDiary.getProductId(), productInDiary.getProductName()))
                .thenReturn(Optional.of(product));
        when(productMapper.mapToProductInDiaryDto(any(ProductInDiary.class)))
                .thenReturn(buildProductInDiaryDto());

        //when
        ProductInDiaryDto productInDiaryDto = diaryService.editProductAmountInDiary(editProductInDiaryDto, authentication);

        //then
        assertEquals(expectedResponse, productInDiaryDto);

        verify(authentication).getName();
        verify(userRepository).findByUsername(username);
        verify(productsAddedToDiaryRepository).findById(editProductInDiaryDto.id());
        verify(productsRepository).findProductByProductIdAndName(productInDiary.getProductId(), productInDiary.getProductName());
        verify(productMapper).mapToProductInDiary(any(ProductInDiary.class), any(ProductInDiary.class));
        verify(diary).calculateNutrientsLeft();
        verify(diary).calculateNutrientsSum();
        verify(productMapper).mapToProductInDiaryDto(any(ProductInDiary.class));
    }

    @Test
    void editProductAmountInDiary_userNotFound_throwsUserNotFoundException() {
        //given
        //TODO Exception message to constant variable
        EditProductInDiaryDto editProductInDiaryDto = buildEditProductInDiaryDto();
        ProductInDiary productInDiary = buildProductInDiary();

        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        //when
        Exception ex = assertThrows(UsernameNotFoundException.class,
                () -> diaryService.editProductAmountInDiary(editProductInDiaryDto, authentication));

        //then
        assertEquals("User not found", ex.getMessage());

        verify(authentication).getName();
        verify(userRepository).findByUsername(username);
        verify(productsAddedToDiaryRepository, never()).findById(editProductInDiaryDto.id());
        verify(productsRepository, never()).findProductByProductIdAndName(productInDiary.getProductId(), productInDiary.getProductName());
        verify(productMapper, never()).mapToProductInDiary(any(ProductInDiary.class), any(ProductInDiary.class));
        verify(diary, never()).calculateNutrientsLeft();
        verify(diary, never()).calculateNutrientsSum();
        verify(productMapper, never()).mapToProductInDiaryDto(any(ProductInDiary.class));
    }

    @Test
    void editProductAmountInDiary_productInDiaryNotFound_throwsProductNotFoundException() {
        //given
        EditProductInDiaryDto editProductInDiaryDto = buildEditProductInDiaryDto();
        ProductInDiary productInDiary = buildProductInDiary();
        User user = buildUser();

        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(productsAddedToDiaryRepository.findById(editProductInDiaryDto.id()))
                .thenReturn(Optional.empty());

        //when
        Exception ex = assertThrows(ProductNotFoundException.class,
                () -> diaryService.editProductAmountInDiary(editProductInDiaryDto, authentication));

        //then
        assertEquals("Product not found", ex.getMessage());

        verify(authentication).getName();
        verify(userRepository).findByUsername(username);
        verify(productsAddedToDiaryRepository).findById(editProductInDiaryDto.id());
        verify(productsRepository, never()).findProductByProductIdAndName(productInDiary.getProductId(), productInDiary.getProductName());
        verify(productMapper, never()).mapToProductInDiary(any(ProductInDiary.class), any(ProductInDiary.class));
        verify(diary, never()).calculateNutrientsLeft();
        verify(diary, never()).calculateNutrientsSum();
        verify(productMapper, never()).mapToProductInDiaryDto(any(ProductInDiary.class));
    }

    @Test
    void editProductAmountInDiary_productNotFound_throwsProductNotFoundException() {
        //given
        EditProductInDiaryDto editProductInDiaryDto = buildEditProductInDiaryDto();
        ProductInDiary productInDiary = buildProductInDiary();
        User user = buildUser();

        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(productsAddedToDiaryRepository.findById(editProductInDiaryDto.id()))
                .thenReturn(Optional.of(productInDiary));
        when(productsRepository.findProductByProductIdAndName(productInDiary.getProductId(), productInDiary.getProductName()))
                .thenReturn(Optional.empty());

        //when
        Exception ex = assertThrows(ProductNotFoundException.class,
                () -> diaryService.editProductAmountInDiary(editProductInDiaryDto, authentication));

        //then
        assertEquals("Product not found", ex.getMessage());

        verify(authentication).getName();
        verify(userRepository).findByUsername(username);
        verify(productsAddedToDiaryRepository).findById(editProductInDiaryDto.id());
        verify(productsRepository).findProductByProductIdAndName(productInDiary.getProductId(), productInDiary.getProductName());
        verify(productMapper, never()).mapToProductInDiary(any(ProductInDiary.class), any(ProductInDiary.class));
        verify(diary, never()).calculateNutrientsLeft();
        verify(diary, never()).calculateNutrientsSum();
        verify(productMapper, never()).mapToProductInDiaryDto(any(ProductInDiary.class));
    }

    @Test
    void deleteProductFromDiary_inputDataOk_returnsString() {
        //given
        String expectedResponse = "Product deleted from diary successfully";
        AddProductToDiaryDto addProductDto = buildAddProductToDiaryDto();
        Product product = buildProduct(addProductDto.foodId());
        User user = buildUser();
        EditProductInDiaryDto editProductInDiaryDto = buildEditProductInDiaryDto();
        ProductInDiary productInDiary = buildProductInDiary();

        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(productsAddedToDiaryRepository.findById(1L))
                .thenReturn(Optional.of(productInDiary));
        when(productsRepository.findProductByProductIdAndName(
                productInDiary.getProductId(),
                productInDiary.getProductName())
        )
                .thenReturn(Optional.of(product));

        //when
        String response = diaryService.deleteProductFromDiary(1L, authentication);

        //then
        assertEquals(expectedResponse, response);

        verify(authentication).getName();
        verify(userRepository).findByUsername(username);
        verify(productsAddedToDiaryRepository).findById(1L);
        verify(productsAddedToDiaryRepository).delete(productInDiary);
        verify(productsRepository).findProductByProductIdAndName(productInDiary.getProductId(), productInDiary.getProductName());
        verify(diary).calculateNutrientsLeft();
        verify(diary).calculateNutrientsSum();
    }

    @Test
    void deleteProductFromDiary_userNotFound_throwsUserNotFoundException() {
        //given
        String expectedResponse = "User not found";
        ProductInDiary productInDiary = buildProductInDiary();

        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        //when
        Exception ex = assertThrows(UsernameNotFoundException.class,
                () -> diaryService.deleteProductFromDiary(1L, authentication));

        //then
        assertEquals(expectedResponse, ex.getMessage());

        verify(authentication).getName();
        verify(userRepository).findByUsername(username);
        verify(productsAddedToDiaryRepository, never()).findById(1L);
        verify(productsAddedToDiaryRepository, never()).delete(productInDiary);
        verify(productsRepository, never()).findProductByProductIdAndName(productInDiary.getProductId(), productInDiary.getProductName());
        verify(diary, never()).calculateNutrientsLeft();
        verify(diary, never()).calculateNutrientsSum();
    }

    @Test
    void deleteProductFromDiary_productInDiaryNotFound_throwsProductNotFoundException() {
        //given
        String expectedResponse = "Product not found";
        User user = buildUser();
        ProductInDiary productInDiary = buildProductInDiary();

        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(productsAddedToDiaryRepository.findById(1L))
                .thenReturn(Optional.empty());
        //when
        Exception ex = assertThrows(ProductNotFoundException.class,
                () -> diaryService.deleteProductFromDiary(1L, authentication));

        //then
        assertEquals(expectedResponse, ex.getMessage());

        verify(authentication).getName();
        verify(userRepository).findByUsername(username);
        verify(productsAddedToDiaryRepository).findById(1L);
        verify(productsAddedToDiaryRepository, never()).delete(productInDiary);
        verify(productsRepository, never()).findProductByProductIdAndName(productInDiary.getProductId(), productInDiary.getProductName());
        verify(diary, never()).calculateNutrientsLeft();
        verify(diary, never()).calculateNutrientsSum();
    }

    @Test
    void deleteProductFromDiary_productNotFound_throwsProductNotFoundException() {
        //given
        String expectedResponse = "Product not found";
        User user = buildUser();
        ProductInDiary productInDiary = buildProductInDiary();

        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(productsAddedToDiaryRepository.findById(1L))
                .thenReturn(Optional.of(productInDiary));
        when(productsRepository.findProductByProductIdAndName
                (
                        productInDiary.getProductId(),
                        productInDiary.getProductName())
        )
                .thenReturn(Optional.empty());
        //when
        Exception ex = assertThrows(ProductNotFoundException.class,
                () -> diaryService.deleteProductFromDiary(1L, authentication));

        //then
        assertEquals(expectedResponse, ex.getMessage());

        verify(authentication).getName();
        verify(userRepository).findByUsername(username);
        verify(productsAddedToDiaryRepository).findById(1L);
        verify(productsAddedToDiaryRepository).delete(productInDiary);
        verify(productsRepository).findProductByProductIdAndName(productInDiary.getProductId(), productInDiary.getProductName());
        verify(diary, never()).calculateNutrientsLeft();
        verify(diary, never()).calculateNutrientsSum();
    }

    private DiaryDto buildDiaryDto() {
        return DiaryDto.builder().
                sumKcal(BigDecimal.valueOf(1000).setScale(2, RoundingMode.HALF_UP))
                .sumProtein(BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP))
                .sumCarbohydrates(BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP))
                .sumFat(BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP))
                .sumFiber(BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP))
                .goalKcal(BigDecimal.valueOf(2000).setScale(2, RoundingMode.HALF_UP))
                .goalProtein(BigDecimal.valueOf(200).setScale(2, RoundingMode.HALF_UP))
                .goalCarbohydrates(BigDecimal.valueOf(200).setScale(2, RoundingMode.HALF_UP))
                .goalFat(BigDecimal.valueOf(200).setScale(2, RoundingMode.HALF_UP))
                .goalFiber(BigDecimal.valueOf(200).setScale(2, RoundingMode.HALF_UP))
                .leftKcal(BigDecimal.valueOf(1000).setScale(2, RoundingMode.HALF_UP))
                .leftProtein(BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP))
                .leftCarbohydrates(BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP))
                .leftFat(BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP))
                .leftFiber(BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP))
                .build();
    }

    private User buildUser() {
        return User.builder()
                .username(username)
                .diary(diary)
                .build();
    }

    private ProductInDiaryDto buildProductInDiaryDto() {
        return ProductInDiaryDto.builder()
                .productId("foodId")
                .productName("name")
                .kcal(100)
                .protein(100)
                .carbohydrates(100)
                .fat(100)
                .fiber(100)
                .image("image")
                .measureLabel("measureLabel")
                .quantity(100)
                .build();
    }

    private ProductInDiary buildProductInDiary() {
        return ProductInDiary.builder()
                .id(1L)
                .productId("foodId")
                .productName("name")
                .kcal(BigDecimal.valueOf(100))
                .protein(BigDecimal.valueOf(100))
                .carbohydrates(BigDecimal.valueOf(100))
                .fat(BigDecimal.valueOf(100))
                .fiber(BigDecimal.valueOf(100))
                .image("image")
                .measureLabel("measureLabel")
                .quantity(BigDecimal.valueOf(100))
                .diary(diary)
                .build();
    }


    private Product buildProduct(String foodId) {
        return new Product(
                1L,
                foodId,
                "name",
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(100),
                "image",
                false,
                "query",
                Map.of("measureLabel", BigDecimal.valueOf(100))
        );
    }

    private AddProductToDiaryDto buildAddProductToDiaryDto() {
        return AddProductToDiaryDto.builder()
                .foodId("foodId")
                .name("name")
                .measureLabel("measureLabel")
                .quantity(BigDecimal.valueOf(100))
                .build();
    }

    private EditProductInDiaryDto buildEditProductInDiaryDto() {
        return EditProductInDiaryDto.builder()
                .id(1L)
                .measureLabel("measureLabel")
                .quantity(BigDecimal.valueOf(100))
                .build();
    }
}