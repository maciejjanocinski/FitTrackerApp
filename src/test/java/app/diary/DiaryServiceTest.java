//package app.diary;
//
//import app.diary.dto.*;
//import app.product.Measure;
//import app.user.UserService;
//import app.util.exceptions.ProductNotFoundException;
//import app.product.Product;
//import app.product.IngredientRepository;
//import app.user.User;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.core.Authentication;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.*;
//import static org.springframework.security.oauth2.core.OAuth2TokenIntrospectionClaimNames.USERNAME;
//
//@ExtendWith(MockitoExtension.class)
//class DiaryServiceTest {
//    @Mock
//    private IngredientRepository productsRepository;
//    @Mock
//    private UserService userService;
//    @Mock
//    private ProductsInDiaryRepository productsInDiaryRepository;
//    @Mock
//    private ProductInDiaryMapper productInDiaryMapper;
//    @Mock
//    private DiaryMapper diaryMapper;
//    @Mock
//    private Authentication authentication;
//    @Mock
//    private Diary diary;
//    @InjectMocks
//    private DiaryService diaryService;
//
//    @Test
//    void getDiary_inputDataOk_returnsDiaryDto() {
//        //given
//        DiaryDto expectedResponse = buildDiaryDto();
//        User user = buildUser();
//
//        when(authentication.getName()).thenReturn(USERNAME);
//        when(userService.getUserByUsername(USERNAME)).thenReturn(user);
//        when(diaryMapper.mapDiaryToDiaryDto(diary)).thenReturn(expectedResponse);
//
//        //when
//        DiaryDto diaryDto = diaryService.getDiary(authentication);
//
//        //then
//        assertEquals(expectedResponse, diaryDto);
//
//        verify(authentication).getName();
//        verify(userService).getUserByUsername(USERNAME);
//        verify(diary).calculateNutrientsLeft();
//        verify(diary).calculateNutrientsSum();
//        verify(diaryMapper).mapDiaryToDiaryDto(diary);
//    }
//    @Test
//    void addProductToDiary_inputDataOk_returnsProductInDiaryDto() {
//        //given
//        AddProductToDiaryDto addProductDto = buildAddProductToDiaryDto(1L);
//        User user = buildUser();
//        ProductInDiaryDto expectedResponse = buildProductInDiaryDto();
//
//        when(authentication.getName()).thenReturn(USERNAME);
//        when(userService.getUserByUsername(USERNAME)).thenReturn(user);
//        when(productInDiaryMapper.mapToProductInDiaryDto(any()))
//                .thenReturn(buildProductInDiaryDto());
//
//        //when
//        ProductInDiaryDto productInDiaryDto = diaryService.addProductToDiary(addProductDto, authentication);
//
//        //then
//        assertEquals(expectedResponse, productInDiaryDto);
//
//        verify(authentication).getName();
//        verify(userService).getUserByUsername(USERNAME);
//        verify(diary).addProduct(any());
//    }
//
//    @Test
//    void addProductToDiary_productNotFound_throwsProductNotFoundException() {
//        //given
//        AddProductToDiaryDto addProductDto = buildAddProductToDiaryDto(2L);
//        User user = buildUser();
//
//        when(authentication.getName()).thenReturn(USERNAME);
//        when(userService.getUserByUsername(USERNAME)).thenReturn(user);
//
//        //when
//        Exception ex = assertThrows(ProductNotFoundException.class,
//                () -> diaryService.addProductToDiary(addProductDto, authentication));
//
//        //then
//        assertEquals("Product not found", ex.getMessage());
//
//        verify(authentication).getName();
//        verify(userService).getUserByUsername(USERNAME);
//        verify(diary, never()).addProduct(any(ProductInDiary.class));
//        verify(productInDiaryMapper, never()).mapToProductInDiaryDto(any(ProductInDiary.class));
//    }
//
//    @Test
//    void editProductAmountInDiary_inputDataOk_returnsProductInDiaryDto() {
//        //given
//        Product product = buildProduct(1L);
//        User user = buildUser();
//        ProductInDiaryDto expectedResponse = buildProductInDiaryDto();
//        EditProductInDiaryDto editProductInDiaryDto = buildEditProductInDiaryDto();
//        ProductInDiary productInDiary = buildProductInDiary();
//
//        when(authentication.getName()).thenReturn(USERNAME);
//        when(userService.getUserByUsername(USERNAME)).thenReturn(user);
//        when(productsInDiaryRepository.findById(editProductInDiaryDto.activityid()))
//                .thenReturn(Optional.of(productInDiary));
//        when(productsRepository.findProductById(editProductInDiaryDto.activityid()))
//                .thenReturn(Optional.of(product));
//        when(this.productInDiaryMapper.mapToProductInDiaryDto(any()))
//                .thenReturn(buildProductInDiaryDto());
//
//        //when
//        ProductInDiaryDto productInDiaryDto = diaryService.editProductAmountInDiary(editProductInDiaryDto, authentication);
//
//        //then
//        assertEquals(expectedResponse, productInDiaryDto);
//
//        verify(authentication).getName();
//        verify(userService).getUserByUsername(USERNAME);
//        verify(productsInDiaryRepository).findById(editProductInDiaryDto.activityid());
//        verify(this.productInDiaryMapper).mapToProductInDiary(any(), any());
//        verify(diary).calculateNutrientsLeft();
//        verify(diary).calculateNutrientsSum();
//        verify(this.productInDiaryMapper).mapToProductInDiaryDto(any());
//    }
//
//    @Test
//    void editProductAmountInDiary_productInDiaryNotFound_throwsProductNotFoundException() {
//        //given
//        EditProductInDiaryDto editProductInDiaryDto = buildEditProductInDiaryDto();
//        ProductInDiary productInDiary = buildProductInDiary();
//        User user = buildUser();
//
//        when(authentication.getName()).thenReturn(USERNAME);
//        when(userService.getUserByUsername(USERNAME)).thenReturn(user);
//        when(productsInDiaryRepository.findById(editProductInDiaryDto.activityid()))
//                .thenReturn(Optional.empty());
//
//        //when
//        Exception ex = assertThrows(ProductNotFoundException.class,
//                () -> diaryService.editProductAmountInDiary(editProductInDiaryDto, authentication));
//
//        //then
//        assertEquals("Product not found", ex.getMessage());
//
//        verify(authentication).getName();
//        verify(userService).getUserByUsername(USERNAME);
//        verify(productsInDiaryRepository).findById(editProductInDiaryDto.activityid());
//        verify(productsRepository, never()).findProductById(productInDiary.getId());
//        verify(this.productInDiaryMapper, never()).mapToProductInDiary(any(ProductInDiary.class), any(ProductInDiary.class));
//        verify(diary, never()).calculateNutrientsLeft();
//        verify(diary, never()).calculateNutrientsSum();
//        verify(this.productInDiaryMapper, never()).mapToProductInDiaryDto(any(ProductInDiary.class));
//    }
//
//    @Test
//    void editProductAmountInDiary_productNotFound_throwsProductNotFoundException() {
//        //given
//        EditProductInDiaryDto editProductInDiaryDto = buildEditProductInDiaryDto();
//        ProductInDiary productInDiary = buildProductInDiary();
//        User user = buildUser();
//
//        when(authentication.getName()).thenReturn(USERNAME);
//        when(userService.getUserByUsername(USERNAME)).thenReturn(user);
//        when(productsInDiaryRepository.findById(editProductInDiaryDto.activityid()))
//                .thenReturn(Optional.of(productInDiary));
//        when(productsRepository.findProductById(productInDiary.getId()))
//                .thenReturn(Optional.empty());
//
//        //when
//        Exception ex = assertThrows(ProductNotFoundException.class,
//                () -> diaryService.editProductAmountInDiary(editProductInDiaryDto, authentication));
//
//        //then
//        assertEquals("Product not found", ex.getMessage());
//
//        verify(authentication).getName();
//        verify(userService).getUserByUsername(USERNAME);
//        verify(productsInDiaryRepository).findById(editProductInDiaryDto.activityid());
//        verify(productsRepository).findProductById(productInDiary.getId());
//        verify(this.productInDiaryMapper, never()).mapToProductInDiary(any(ProductInDiary.class), any(ProductInDiary.class));
//        verify(diary, never()).calculateNutrientsLeft();
//        verify(diary, never()).calculateNutrientsSum();
//        verify(this.productInDiaryMapper, never()).mapToProductInDiaryDto(any(ProductInDiary.class));
//    }
//
//    @Test
//    void deleteProductFromDiary_inputDataOk_returnsString() {
//        //given
//        String expectedResponse = "Product deleted from diary successfully";
//        Product product = buildProduct(1L);
//        User user = buildUser();
//        ProductInDiary productInDiary = buildProductInDiary();
//
//        when(authentication.getName()).thenReturn(USERNAME);
//        when(userService.getUserByUsername(USERNAME)).thenReturn(user);
//        when(productsInDiaryRepository.findById(1L))
//                .thenReturn(Optional.of(productInDiary));
//        when(productsRepository.findProductById(productInDiary.getId()))
//                .thenReturn(Optional.of(product));
//
//        //when
//        String response = diaryService.deleteProductFromDiary(buildDeleteProductDto(), authentication);
//
//        //then
//        assertEquals(expectedResponse, response);
//
//        verify(authentication).getName();
//        verify(userService).getUserByUsername(USERNAME);
//        verify(productsInDiaryRepository).findById(1L);
//        verify(productsInDiaryRepository).deleteProductInDiaryById(productInDiary.getId());
//        verify(productsRepository).findProductById(productInDiary.getId());
//    }
//
//    @Test
//    void deleteProductFromDiary_productInDiaryNotFound_throwsProductNotFoundException() {
//        //given
//        String expectedResponse = "Product not found";
//        User user = buildUser();
//        ProductInDiary productInDiary = buildProductInDiary();
//
//        when(authentication.getName()).thenReturn(USERNAME);
//        when(userService.getUserByUsername(USERNAME)).thenReturn(user);
//        when(productsInDiaryRepository.findById(1L))
//                .thenReturn(Optional.empty());
//        //when
//        Exception ex = assertThrows(ProductNotFoundException.class,
//                () -> diaryService.deleteProductFromDiary(buildDeleteProductDto(), authentication));
//
//        //then
//        assertEquals(expectedResponse, ex.getMessage());
//
//        verify(authentication).getName();
//        verify(userService).getUserByUsername(USERNAME);
//        verify(productsInDiaryRepository).findById(1L);
//        verify(productsInDiaryRepository, never()).deleteProductInDiaryById(productInDiary.getId());
//        verify(productsRepository, never()).findProductById(productInDiary.getId());
//    }
//
//    @Test
//    void deleteProductFromDiary_productNotFound_throwsProductNotFoundException() {
//        //given
//        String expectedResponse = "Product not found";
//        User user = buildUser();
//        ProductInDiary productInDiary = buildProductInDiary();
//
//        when(authentication.getName()).thenReturn(USERNAME);
//        when(userService.getUserByUsername(USERNAME)).thenReturn(user);
//        when(productsInDiaryRepository.findById(1L))
//                .thenReturn(Optional.of(productInDiary));
//
//        when(productsRepository.findProductById(productInDiary.getId()))
//                .thenReturn(Optional.empty());
//        //when
//        Exception ex = assertThrows(ProductNotFoundException.class,
//                () -> diaryService.deleteProductFromDiary(buildDeleteProductDto(), authentication));
//
//        //then
//        assertEquals(expectedResponse, ex.getMessage());
//
//        verify(authentication).getName();
//        verify(userService).getUserByUsername(USERNAME);
//        verify(productsInDiaryRepository).findById(1L);
//        verify(productsInDiaryRepository).deleteProductInDiaryById(productInDiary.getId());
//        verify(productsRepository).findProductById(productInDiary.getId());
//    }
//
//    private DiaryDto buildDiaryDto() {
//        return DiaryDto.builder().
//                sumKcal(BigDecimal.valueOf(1000).setScale(2, RoundingMode.HALF_UP))
//                .sumProtein(BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP))
//                .sumCarbohydrates(BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP))
//                .sumFat(BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP))
//                .sumFiber(BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP))
//                .goalKcal(BigDecimal.valueOf(2000).setScale(2, RoundingMode.HALF_UP))
//                .goalProtein(BigDecimal.valueOf(200).setScale(2, RoundingMode.HALF_UP))
//                .goalCarbohydrates(BigDecimal.valueOf(200).setScale(2, RoundingMode.HALF_UP))
//                .goalFat(BigDecimal.valueOf(200).setScale(2, RoundingMode.HALF_UP))
//                .goalFiber(BigDecimal.valueOf(200).setScale(2, RoundingMode.HALF_UP))
//                .leftKcal(BigDecimal.valueOf(1000).setScale(2, RoundingMode.HALF_UP))
//                .leftProtein(BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP))
//                .leftCarbohydrates(BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP))
//                .leftFat(BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP))
//                .leftFiber(BigDecimal.valueOf(100).setScale(2, RoundingMode.HALF_UP))
//                .build();
//    }
//
//    private User buildUser() {
//        return User.builder()
//                .name(USERNAME)
//                .diary(diary)
//                .lastSearchedProducts(List.of(buildProduct(1L)))
//                .build();
//    }
//
//    private ProductInDiaryDto buildProductInDiaryDto() {
//        return ProductInDiaryDto.builder()
//                .productId("foodId")
//                .productName("name")
//                .kcal(100)
//                .protein(100)
//                .carbohydrates(100)
//                .fat(100)
//                .fiber(100)
//                .image("image")
//                .measureLabel("measureLabel")
//                .quantity(100)
//                .build();
//    }
//
//    private ProductInDiary buildProductInDiary() {
//        return ProductInDiary.builder()
//                .activityid(1L)
//                .productId("foodId")
//                .productName("name")
//                .kcal(BigDecimal.valueOf(100))
//                .protein(BigDecimal.valueOf(100))
//                .carbohydrates(BigDecimal.valueOf(100))
//                .fat(BigDecimal.valueOf(100))
//                .fiber(BigDecimal.valueOf(100))
//                .image("image")
//                .measureLabel("measureLabel")
//                .quantity(BigDecimal.valueOf(100))
//                .diary(diary)
//                .build();
//    }
//
//
//    private Product buildProduct(Long activityid) {
//        return Product.builder()
//                .activityid(activityid)
//                .productId("foodId")
//                .name("name")
//                .kcal(BigDecimal.valueOf(100))
//                .protein(BigDecimal.valueOf(100))
//                .carbohydrates(BigDecimal.valueOf(100))
//                .fat(BigDecimal.valueOf(100))
//                .fiber(BigDecimal.valueOf(100))
//                .image("image")
//                .isUsed(false)
//                .query("query")
//                .measures(List.of(Measure.builder()
//                        .name("measureLabel")
//                        .weight(BigDecimal.valueOf(100))
//                        .build()))
//                .build();
//    }
//
//    private AddProductToDiaryDto buildAddProductToDiaryDto(Long activityid) {
//        return AddProductToDiaryDto.builder()
//                .activityid(activityid)
//                .measureLabel("measureLabel")
//                .quantity(BigDecimal.valueOf(100))
//                .build();
//    }
//
//    private EditProductInDiaryDto buildEditProductInDiaryDto() {
//        return EditProductInDiaryDto.builder()
//                .activityid(1L)
//                .measureLabel("measureLabel")
//                .quantity(BigDecimal.valueOf(100))
//                .build();
//    }
//
//    private DeleteProductDto buildDeleteProductDto() {
//        return DeleteProductDto.builder()
//                .activityid(1L)
//                .build();
//    }
//
//}