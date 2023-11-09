package app.diary;

import app.authentication.Role;
import app.authentication.TokenService;
import app.diary.dto.AddProductToDiaryDto;
import app.diary.dto.DeleteProductDto;
import app.diary.dto.DiaryDto;
import app.diary.dto.EditProductInDiaryDto;
import app.product.Product;
import app.product.ProductRepository;
import app.user.User;
import app.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Optional;

import static app.utils.TestUtils.buildUser;
import static app.utils.TestUtils.generateAuthorizationHeader;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.http.MediaType.parseMediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@NoArgsConstructor
@AutoConfigureMockMvc
public class DiaryIntegrationTest {

    @LocalServerPort
    @Value("${app.test.port}")
    private int port;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void getDiary_addProductToDiary_editProductAmountInDiary_deleteProductFromDiary() throws Exception {
        //given
        Role role = new Role(1L, Role.roleType.ROLE_USER_STANDARD.toString());

        AddProductToDiaryDto addProductToDiaryDto = buildAddProductToDiaryDto();
        EditProductInDiaryDto editProductInDiaryDto = buildEditProductInDiaryDto();
        DeleteProductDto deleteProductDto = buildDeleteProductDto();

        User user = buildUser(role, passwordEncoder);
        userRepository.save(user);
        productRepository.save(buildProduct());

        //when
        mockMvc.perform(get("http://localhost:" + port + "/diary/")
                        .header("Authorization", generateAuthorizationHeader(
                                tokenService,
                                user.getAuthorities(),
                                user.getUsername()))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.sumKcal")
                        .value(BigDecimal.ZERO.setScale(1, RoundingMode.HALF_UP)))
                .andDo(print());

        Diary diaryAfterGet = userRepository.findByUsername(user.getUsername()).get().getDiary();

        mockMvc.perform(post("http://localhost:" + port + "/diary/product")
                        .header("Authorization", generateAuthorizationHeader(
                                tokenService,
                                user.getAuthorities(),
                                user.getUsername())
                        )
                        .content(objectMapper.writeValueAsString(addProductToDiaryDto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.quantity")
                        .value(BigDecimal.valueOf(1).setScale(1, RoundingMode.HALF_UP)))
                .andDo(print());

        Diary diaryAfterPost = userRepository.findByUsername(user.getUsername()).get().getDiary();

        mockMvc.perform(patch("http://localhost:" + port + "/diary/product")
                        .header("Authorization", generateAuthorizationHeader(
                                tokenService,
                                user.getAuthorities(),
                                user.getUsername())
                        )
                        .content(objectMapper.writeValueAsString(editProductInDiaryDto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.quantity")
                        .value(BigDecimal.valueOf(200).setScale(1, RoundingMode.HALF_UP)))
                .andDo(print());

        Diary diaryAfterPatch = userRepository.findByUsername(user.getUsername()).get().getDiary();

        mockMvc.perform(delete("http://localhost:" + port + "/diary/product")
                        .header("Authorization", generateAuthorizationHeader(
                                tokenService,
                                user.getAuthorities(),
                                user.getUsername())
                        )
                        .content(objectMapper.writeValueAsString(deleteProductDto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(parseMediaType("text/plain;charset=UTF-8")))
                .andExpect(content().string("Product deleted from diary successfully"))
                .andDo(print());

        Diary diaryAfterDelete = userRepository.findByUsername(user.getUsername()).get().getDiary();

        //then
        assertEquals(diaryAfterGet.getDiaryId(), user.getDiary().getDiaryId());
        assertEquals(1, diaryAfterPost.getProducts().size());
        assertEquals(
                diaryAfterPatch.getProducts().get(0).getQuantity(),
                BigDecimal.valueOf(200).setScale(2, RoundingMode.HALF_UP)
        );
        assertEquals(0, diaryAfterDelete.getProducts().size());

    }

    private Product buildProduct() {
        return Product.builder()
                .productId("foodId")
                .name("name")
                .kcal(BigDecimal.valueOf(1))
                .protein(BigDecimal.valueOf(1))
                .fat(BigDecimal.valueOf(1))
                .carbohydrates(BigDecimal.valueOf(1))
                .fiber(BigDecimal.valueOf(1))
                .image("image")
                .isUsed(false)
                .query("query")
                .measures(Map.of("Gram", BigDecimal.valueOf(1)))
                .build();
    }

    private AddProductToDiaryDto buildAddProductToDiaryDto() {
        return AddProductToDiaryDto.builder()
                .foodId("foodId")
                .name("name")
                .measureLabel("Gram")
                .quantity(BigDecimal.valueOf(1))
                .build();
    }

    private EditProductInDiaryDto buildEditProductInDiaryDto() {
        return EditProductInDiaryDto.builder()
                .id(1L)
                .measureLabel("Gram")
                .quantity(BigDecimal.valueOf(200))
                .build();
    }

    private DeleteProductDto buildDeleteProductDto() {
        return DeleteProductDto.builder()
                .id(1L)
                .build();
    }
}
