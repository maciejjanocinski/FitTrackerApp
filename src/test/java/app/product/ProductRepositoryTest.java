package app.product;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EnableJpaRepositories
@EntityScan(basePackages = "app.product")
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void deleteNotUserProducts_inputDataOk() {
        //given
        List<Product> productList = buildListOfProductsWithDifferentValues(
                "bread",
                "olive oil",
                false,
                true);
        productRepository.saveAll(productList);

        //when
        productRepository.deleteNotUsedProducts();
        List<Product> products = productRepository.findAll();

        //then
        assertEquals(3, products.size());
        assertAll(
                () -> assertTrue(products.get(0).isUsed()),
                () -> assertTrue(products.get(1).isUsed()),
                () -> assertTrue(products.get(2).isUsed())
        );
    }

    @Test
    void deleteNotUserProducts_allProductsNotUsed() {
        //given
        List<Product> productList = buildListOfProductsWithDifferentValues(
                "bread",
                "olive oil",
                false,
                false);
        productRepository.saveAll(productList);

        //when
        productRepository.deleteNotUsedProducts();
        List<Product> products = productRepository.findAll();

        //then
        assertEquals(0, products.size());
    }

    @Test
    public void testFindProductByProductIdAndName_inputDataOk() {
        //given
        Product product1 = Product.builder()
                .productId("1")
                .name("Product1")
                .build();
        productRepository.save(product1);

        //when
        Optional<Product> product = productRepository.findProductByProductIdAndName("1", "Product1");

        //then
        assertTrue(product.isPresent());
        assertEquals("1", product.get().getProductId());
        assertEquals("Product1", product.get().getName());
    }

    @Test
    public void testFindProductByProductIdAndName_returnsNull() {
        //given

        //when
        Optional<Product> product = productRepository.findProductByProductIdAndName("1", "Product1");

        //then
        assertTrue(product.isEmpty());
    }

    @Test
    public void findAllByQuery_inputDataOk() {
        //given

        List<Product> productsList = buildListOfProductsWithDifferentValues(
                "bread",
                "olive oil",
                false,
                false);
        productRepository.saveAll(productsList);

        //when
        List<Product> products = productRepository.findAllByQuery("bread");

        //then
        assertEquals(2, products.size());
        assertEquals("bread", products.get(0).getQuery());
        assertEquals("bread", products.get(1).getQuery());
        assertNotNull(products);
    }

    @Test
    public void findAllByQuery_returnsNull() {
        //given

        List<Product> productsList = buildListOfProductsWithDifferentValues(
                "bread",
                "bread",
                false,
                false);
        productRepository.saveAll(productsList);

        //when
        List<Product> products = productRepository.findAllByQuery("olive");

        //then
        assertEquals(0, products.size());
    }


    private List<Product> buildListOfProductsWithDifferentValues(String q1, String q2, boolean isUsed1, boolean isUsed2) {
        Product p1 = Product.builder()
                .productId("1")
                .name("bread")
                .query(q1)
                .isUsed(isUsed1)
                .build();

        Product p2 = Product.builder()
                .productId("2")
                .name("garlic bread")
                .query(q1)
                .isUsed(isUsed1)
                .build();

        Product p3 = Product.builder()
                .productId("3")
                .name("olive oil")
                .query(q2)
                .isUsed(isUsed2)
                .build();

        Product p4 = Product.builder()
                .productId("4")
                .name("butter")
                .query(q2)
                .isUsed(isUsed2)
                .build();
        Product p5 = Product.builder()
                .productId("4")
                .name("butter")
                .query(q2)
                .isUsed(isUsed2)
                .build();

        return List.of(p1, p2, p3, p4, p5);
    }

}