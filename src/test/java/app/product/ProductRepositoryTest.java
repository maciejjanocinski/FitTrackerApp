package app.product;

import app.authentication.Role;
import app.authentication.RoleRepository;
import app.user.User;
import app.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.annotation.DirtiesContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static app.user.User.setGenderFromString;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@EnableJpaRepositories(basePackageClasses = {
        ProductRepository.class,
        UserRepository.class,
        RoleRepository.class
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    private final Role role = buildRole();
    private final User user = buildUser();


    @Test
    void deleteNotUserProducts_inputDataOk() {
        //given
        roleRepository.save(role);
        userRepository.save(user);
        List<Product> productList = buildListOfProductsWithDifferentValues(
                "bread",
                "olive oil",
                false,
                true,
                user);
        productRepository.saveAll(productList);

        //when
        productRepository.deleteNotUsedProducts(1L);
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
        roleRepository.save(role);
        userRepository.save(user);
        List<Product> productList = buildListOfProductsWithDifferentValues(
                "bread",
                "olive oil",
                false,
                false,
                user);
        productRepository.saveAll(productList);

        //when
        productRepository.deleteNotUsedProducts(user.getId());
        List<Product> products = productRepository.findAll();

        //then
        assertEquals(0, products.size());
    }

    @Test
    public void testFindProductByProductId_inputDataOk() {
        //given

        Product p5 = Product.builder()
                .productId("product1")
                .name("ProductName")
                .query("bread")
                .isUsed(false)
                .build();

        productRepository.save(p5);

        //when
        Optional<Product> product = productRepository.findProductById(1L);

        //then
        assertTrue(product.isPresent());
        assertEquals("product1", product.get().getProductId());
        assertEquals("ProductName", product.get().getName());
    }

    @Test
    public void testFindProductByProductId_returnsNull() {
        //given
        roleRepository.save(role);
        userRepository.save(user);
        //when
        Optional<Product> product = productRepository.findProductById(1L);

        //then
        assertTrue(product.isEmpty());
    }

    @Test
    public void findAllByQuery_inputDataOk() {
        //given
        roleRepository.save(role);
        userRepository.save(user);

        List<Product> productsList = buildListOfProductsWithDifferentValues(
                "bread",
                "olive oil",
                false,
                false,
                user);
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
        roleRepository.save(role);
        userRepository.save(user);

        List<Product> productsList = buildListOfProductsWithDifferentValues(
                "bread",
                "bread",
                false,
                false,
                user);
        productRepository.saveAll(productsList);

        //when
        List<Product> products = productRepository.findAllByQuery("olive");

        //then
        assertEquals(0, products.size());
    }


    private List<Product> buildListOfProductsWithDifferentValues(
            String query1,
            String query2,
            boolean isUsed1,
            boolean isUsed2,
            User user) {
        Product p1 = Product.builder()
                .productId("1")
                .name("bread")
                .query(query1)
                .isUsed(isUsed1)
                .user(user)
                .build();

        Product p2 = Product.builder()
                .productId("2")
                .name("garlic bread")
                .query(query1)
                .isUsed(isUsed1)
                .user(user)
                .build();

        Product p3 = Product.builder()
                .productId("3")
                .name("olive oil")
                .query(query2)
                .isUsed(isUsed2)
                .user(user)
                .build();

        Product p4 = Product.builder()
                .productId("4")
                .name("butter")
                .query(query2)
                .isUsed(isUsed2)
                .user(user)
                .build();
        Product p5 = Product.builder()
                .productId("4")
                .name("butter")
                .query(query2)
                .isUsed(isUsed2)
                .user(user)
                .build();

        return List.of(p1, p2, p3, p4, p5);
    }


    private User buildUser() {
        return User.builder()
                .username("username")
                .password("password124M!")
                .name("name")
                .surname("surname")
                .gender(setGenderFromString("MALE"))
                .email("maciek@gmial.com")
                .phone("123456789")
                .authorities(Set.of(role))
                .lastSearchedProducts(new ArrayList<>())
                .build();
    }

    private Role buildRole() {
        return Role.builder()
                .name("ROLE_USER_STANDARD")
                .build();
    }

}