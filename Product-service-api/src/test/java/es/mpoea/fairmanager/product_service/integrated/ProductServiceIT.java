package es.mpoea.fairmanager.product_service.integrated;

import es.mpoea.fairmanager.commondata.DTO.requests.Product.CreateProductRequest;
import es.mpoea.fairmanager.commondata.DTO.requests.Product.UpdateProductRequest;
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

import java.time.Instant;
import java.util.UUID;

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

    @Test
    void updateProduct_shouldUpdateAllFields() {
        CreateProductRequest createProduct = new CreateProductRequest("Create Integrated Test Product", "INT description", "INT category");
        UpdateProductRequest request = new UpdateProductRequest("updated label", "updated description", "updated category");

        Product created = productService.createProduct(createProduct);

        Long productId = created.getId();
        UUID originalSku = created.getSku();
        Instant originalCreatedAt = created.getCreatedAt();
        Instant originalUpdatedAt = created.getUpdatedAt();
        long originalVersion = created.getVersion();

        Product updated = productService.updateProduct(productId, request);
        Product fromDB = productRepo.findById(productId).orElseThrow();

        assertNotNull(fromDB.getId());
        assertNotNull(fromDB.getSku());

        assertEquals(productId, fromDB.getId());
        assertEquals(originalSku, fromDB.getSku());

        assertEquals("updated label", fromDB.getLabel());
        assertEquals("updated description", fromDB.getDescription());
        assertEquals("updated category", fromDB.getCategory());

        assertNotNull(fromDB.getCreatedAt());
        assertNotNull(fromDB.getUpdatedAt());

        assertEquals(originalCreatedAt, fromDB.getCreatedAt());
        assertNotEquals(fromDB.getUpdatedAt(), originalUpdatedAt);

        assertNotEquals(originalVersion, fromDB.getVersion());

        assertEquals(updated.getId(), fromDB.getId());
        assertEquals(updated.getSku(), fromDB.getSku());
        assertEquals(updated.getLabel(), fromDB.getLabel());
        assertEquals(updated.getDescription(), fromDB.getDescription());
        assertEquals(updated.getCategory(), fromDB.getCategory());
        assertEquals(updated.getCreatedAt(), fromDB.getCreatedAt());
        assertEquals(updated.getUpdatedAt(), fromDB.getUpdatedAt());
        assertEquals(updated.getVersion(), fromDB.getVersion());

        assertFalse(fromDB.getUpdatedAt().isBefore(fromDB.getCreatedAt()));
    }

    @Test
    void updateProduct_shouldUpdateOnlyNotNullFields() {
        CreateProductRequest create = new CreateProductRequest("init label", "init description", "init category");
        UpdateProductRequest request = new UpdateProductRequest("updated label", null, "updated category");

        Product created = productService.createProduct(create);

        long orgID = created.getId();
        UUID originalSku = created.getSku();

        String orgDescription = created.getDescription();

        Instant orgCreatedAt = created.getCreatedAt();
        Instant orgUpdatedAt = created.getUpdatedAt();

        long originalVersion = created.getVersion();

        Product updated = productService.updateProduct(orgID, request);
        Product fromDB = productRepo.findById(orgID).orElseThrow(() -> new AssertionError("Product was not found in database after creation"));

        assertNotNull(fromDB.getId());
        assertNotNull(fromDB.getSku());

        assertEquals(orgID, fromDB.getId());
        assertEquals(originalSku, fromDB.getSku());

        assertEquals("updated label", fromDB.getLabel());
        assertEquals(orgDescription, fromDB.getDescription());
        assertEquals("updated category", fromDB.getCategory());

        assertNotNull(fromDB.getCreatedAt());
        assertNotNull(fromDB.getUpdatedAt());

        assertEquals(orgCreatedAt, fromDB.getCreatedAt());
        assertNotEquals(orgUpdatedAt, fromDB.getUpdatedAt());

        assertNotEquals(originalVersion, fromDB.getVersion());

        assertEquals(updated.getId(), fromDB.getId());
        assertEquals(updated.getSku(), fromDB.getSku());
        assertEquals(updated.getLabel(), fromDB.getLabel());
        assertEquals(updated.getDescription(), fromDB.getDescription());
        assertEquals(updated.getCategory(), fromDB.getCategory());
        assertEquals(updated.getCreatedAt(), fromDB.getCreatedAt());
        assertEquals(updated.getUpdatedAt(), fromDB.getUpdatedAt());
        assertEquals(updated.getVersion(), fromDB.getVersion());

        assertFalse(fromDB.getUpdatedAt().isBefore(fromDB.getCreatedAt()));
    }
}
