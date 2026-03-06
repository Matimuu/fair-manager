package es.mpoea.fairmanager.product_service.integrated;

import es.mpoea.fairmanager.commondata.DTO.requests.Product.CreateProductRequest;
import es.mpoea.fairmanager.product_service.api.services.ProductService;
import es.mpoea.fairmanager.product_service.persistence.models.Product;
import es.mpoea.fairmanager.product_service.persistence.repositories.ProductRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for ProductService with Repository, JPA and PostgreSQL.
 */

@Testcontainers
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProductServiceIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepo productRepo;

    @Test
    void createProduct_shouldPersistProductAndGenerateTechnicalFields() {
        CreateProductRequest request = new CreateProductRequest("Create Integrated Test Product", "INT description", "INT category");

        Product createdProduct = productService.createProduct(request);

        assertNotNull(createdProduct.getId());
        assertNotNull(createdProduct.getSku());

        assertEquals("Create Integrated Test Product", createdProduct.getLabel());
        assertEquals("INT description", createdProduct.getDescription());
        assertEquals("INT category", createdProduct.getCategory());

        assertNotNull(createdProduct.getCreatedAt());
        assertNotNull(createdProduct.getUpdatedAt());
        assertNotNull(createdProduct.getVersion());

        long id = createdProduct.getId();

        Product sameProductFromDB = productRepo.findById(id).orElseThrow(() -> new AssertionError("Product was not found in database after creation"));

        assertEquals(createdProduct.getId(), sameProductFromDB.getId());
        assertEquals(createdProduct.getSku(), sameProductFromDB.getSku());
        assertEquals(createdProduct.getLabel(), sameProductFromDB.getLabel());
        assertEquals(createdProduct.getDescription(), sameProductFromDB.getDescription());
        assertEquals(createdProduct.getCategory(), sameProductFromDB.getCategory());

        assertNotNull(sameProductFromDB.getCreatedAt());
        assertNotNull(sameProductFromDB.getUpdatedAt());
        assertFalse(sameProductFromDB.getUpdatedAt().isBefore(sameProductFromDB.getCreatedAt()));

        assertEquals(createdProduct.getVersion(), sameProductFromDB.getVersion());
    }
}
