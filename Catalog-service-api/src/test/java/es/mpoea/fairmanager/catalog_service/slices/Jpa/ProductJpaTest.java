package es.mpoea.fairmanager.catalog_service.slices.Jpa;

import org.junit.jupiter.api.Test;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProductJpaTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:18")
            .withDatabaseName("test_db")
            .withUsername("test_user")
            .withPassword("test_password");

    @Test
    void createProduct_shouldCreateProduct() {
//        Category category = new Category("Default")
//        Product product = new Product("Label","Description", category);
    }

    @Test
    void getProductById_shouldReturnProduct() {
//        TODO
    }

    @Test
    void getAllProducts_shouldReturnAllProducts() {
//        TODO
    }

    @Test
    void findByLabel_shouldReturnProduct() {
//        TODO
    }

    @Test
    void findAllWithCategory_shouldReturnAllProducts_withCategories() {
//        TODO
    }

    @Test
    void findByIdWithCategory_shouldReturnProduct_withCategory() {
//        TODO
    }

    @Test
    void updateProduct_shouldUpdateProduct() {
//        TODO
    }

    @Test
    void deleteProduct_shouldDeleteProduct() {
//        TODO
    }

    @Test
    void clearCategoryByCategoryId_shouldClearCategory() {
//        TODO
    }



}
