package app.product;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private RestTemplate restTemplate;


//    @Test
//    public void testSearchProducts() {
//        String query = "your_query_here";
//        lenient().when(foodApiManager.getLastQuery()).thenReturn("null");
//        String apiUrl = "your_mocked_api_url";
//        ResponseDTO mockResponseDTO = new ResponseDTO();
//        ResponseEntity<ResponseDTO> mockResponseEntity = ResponseEntity.ok(mockResponseDTO);
//
//        lenient().when(restTemplate.getForEntity(apiUrl, ResponseDTO.class)).thenReturn(mockResponseEntity);
//
//        List<Product> result = productService.searchProducts(query);
//        assertEquals(0, result.size());
//
//        verify(productRepository).saveAll(anyList());
//
//    }


}


