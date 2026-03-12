package es.mpoea.fairmanager.catalog_service.slices;

import es.mpoea.fairmanager.catalog_service.persistence.models.Product;
import es.mpoea.fairmanager.catalog_service.persistence.repositories.ProductRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProductJpaTest {

//    @Container
//    @ServiceConnection
//    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18")
//            .withDatabaseName("testdb")
//            .withUsername("testuser")
//            .withPassword("testpass");
//
//    @Autowired
//    private ProductRepo productRepo;
//
//    @Test
//    void saveAndFindById_ShouldWork() {
//        Product product = new Product("Test Product", "This is a test product", "Test Category");
//
//        Product savedProduct = productRepo.save(product);
//
//        assertNotNull(savedProduct.getId(), "Saved product should have an ID");
//
//        Product foundProduct = productRepo.findById(savedProduct.getId()).orElseThrow();
//
//        assertEquals("Test Product", foundProduct.getLabel());
//        assertEquals("This is a test product", foundProduct.getDescription());
//        assertEquals("Test Category", foundProduct.getCategory());
//
//        assertNotNull(foundProduct.getSku());
//    }
}
