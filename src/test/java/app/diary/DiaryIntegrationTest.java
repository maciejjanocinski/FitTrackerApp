package app.diary;

import app.authentication.Role;
import app.authentication.TokenService;
import app.diary.dto.AddProductToDiaryDto;
import app.product.Product;
import app.product.ProductRepository;
import app.user.User;
import app.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.util.Map;
import static app.utils.TestUtils.buildUser;
import static app.utils.TestUtils.generateAuthorizationHeader;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@NoArgsConstructor
@AutoConfigureMockMvc
public class DiaryIntegrationTest {

    @LocalServerPort
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
    void getDiary() throws Exception {
        //given
        Role role = new Role(1L, Role.roleType.ROLE_USER_STANDARD.toString());

        User user = buildUser(role, passwordEncoder);
        passwordEncoder.encode("Password123!");
        userRepository.save(user);

        //when
        mockMvc.perform(get("http://localhost:" + port + "/diary/")
                        .header("Authorization", generateAuthorizationHeader(tokenService, user))
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    @Test
    void addProductToDiary() throws Exception {
        //given
        Role role = new Role(1L, Role.roleType.ROLE_USER_STANDARD.toString());
        AddProductToDiaryDto addProductToDiaryDto = buildAddProductToDiaryDto();
        User user = buildUser(role, passwordEncoder);
        passwordEncoder.encode("Password123!");
        userRepository.save(user);
        productRepository.save(buildProduct());

        //when
        mockMvc.perform(post("http://localhost:" + port + "/diary/product")
                        .header("Authorization", generateAuthorizationHeader(tokenService, user))
                        .content(objectMapper.writeValueAsString(addProductToDiaryDto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print());
    }

    private AddProductToDiaryDto buildAddProductToDiaryDto() {
        return AddProductToDiaryDto.builder()
                .foodId("foodId")
                .name("name")
                .measureLabel("Gram")
                .quantity(BigDecimal.valueOf(1))
                .build();
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
}
