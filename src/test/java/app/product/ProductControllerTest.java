//package app.product;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.List;
//
//import static app.utils.TestUtils.query;
//import static org.mockito.Mockito.*;
//import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(ProductController.class)
//@ExtendWith(MockitoExtension.class)
//class ProductControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//    @MockBean
//    private ProductService productService;
//    @Autowired
//    private ObjectMapper objectMapper;
//    @Mock
//    Product product;
//
//    @Test
//    void searchProducts_inputDataOk_returns200() throws Exception {
//        List<Product> productsList = List.of(product, product, product, product);
//        when(productService.searchProducts(any(), any())).thenReturn(productsList);
//
//        mockMvc.perform(get("/products/search")
//                        .param("product",query)
//                        .with(jwt().jwt(j -> j.claim("roles", "ROLE_USER"))))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(content().json(objectMapper.writeValueAsString(productsList)))
//                .andDo(print());
//
//        verify(productService).searchProducts(any(), any());
//    }
//
//}