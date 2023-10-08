package app.diary;

import app.diary.dto.AddProductToDiaryDto;
import app.diary.dto.DiaryDto;
import app.diary.dto.ProductInDiaryDto;
import app.product.Product;
import app.product.ProductRepository;
import app.user.User;
import app.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiaryServiceTest {
    @InjectMocks
    private DiaryService diaryService;
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
    Authentication authentication;
    @Mock
    Diary diary = mock(Diary.class);
    @Mock
    ProductInDiary productInDiary = mock(ProductInDiary.class);

    @Test
    void getDiary_inputDataOk() {
        //given
        String username = "username";
        DiaryDto expectedResponse = buildDiaryDto();

        User user = User.builder()
                .username(username)
                .diary(diary)
                .build();

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
    void getDiary_throwsException() {
        //given
        String username = "username";

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
    void addProductToDiary() {
        //given
        String username = "username";
        AddProductToDiaryDto addProductDto = AddProductToDiaryDto.builder()
                .foodId("foodId")
                .name("name")
                .measureLabel("measureLabel")
                .quantity(BigDecimal.valueOf(100))
                .build();
        Product product = buildProduct(addProductDto.foodId());


        User user = User.builder()
                .username(username)
                .diary(diary)
                .build();

        ProductInDiaryDto expectedResponse = buildProductInDiaryDto();

        when(authentication.getName()).thenReturn(username);
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(productsRepository.findProductEntityByProductIdAndName(addProductDto.foodId(), addProductDto.name()))
                .thenReturn(Optional.of(product));
        when(productMapper.mapToProductInDiaryDto(any(ProductInDiary.class)))
                .thenReturn(buildProductInDiaryDto());

        //when
        ProductInDiaryDto productInDiaryDto = diaryService.addProductToDiary(addProductDto, authentication);

        //then
        assertEquals(expectedResponse, productInDiaryDto);

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
                "query",
                false,
                Map.of("measureLabel", BigDecimal.valueOf(100))
        );
    }
}