package es.mpoea.fairmanager.catalog_service.integrated;

import es.mpoea.fairmanager.catalog_service.api.commands.CreateProductCommand;
import es.mpoea.fairmanager.catalog_service.api.commands.UpdateProductCommand;
import es.mpoea.fairmanager.catalog_service.api.exceptions.ProductNotFoundException;
import es.mpoea.fairmanager.catalog_service.api.services.ProductService;
import es.mpoea.fairmanager.catalog_service.persistence.models.Product;
import es.mpoea.fairmanager.catalog_service.persistence.repositories.ProductRepo;
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
        CreateProductCommand command = new CreateProductCommand("Create Integrated Test Product", "INT description", "INT category");


        Product createdProduct = productService.createProduct(command);

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
        CreateProductCommand createCommand = new CreateProductCommand("Create Integrated Test Product", "INT description", "INT category");
        UpdateProductCommand updateCommand = new UpdateProductCommand("updated label", "updated description", "updated category");



        Product created = productService.createProduct(createCommand);

        Long productId = created.getId();
        UUID originalSku = created.getSku();
        Instant originalCreatedAt = created.getCreatedAt();
        Instant originalUpdatedAt = created.getUpdatedAt();
        long originalVersion = created.getVersion();

        Product updated = productService.updateProduct(productId, updateCommand);
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
        CreateProductCommand createCommand = new CreateProductCommand("init label", "init description", "init category");
        UpdateProductCommand updateCommand = new UpdateProductCommand("updated label", null, "updated category");

        Product created = productService.createProduct(createCommand);

        long orgID = created.getId();
        UUID originalSku = created.getSku();

        String orgDescription = created.getDescription();

        Instant orgCreatedAt = created.getCreatedAt();
        Instant orgUpdatedAt = created.getUpdatedAt();

        long originalVersion = created.getVersion();

        Product updated = productService.updateProduct(orgID, updateCommand);
        Product fromDB = productRepo.findById(orgID).orElseThrow(() -> new AssertionError("Product was not found in database after update"));

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

    @Test
    void updateProduct_shouldThrowExceptionIfProductNotExists() {
       UpdateProductCommand command = new UpdateProductCommand("updated label", "updated description", "updated category");

       assertThrows(ProductNotFoundException.class, () -> productService.updateProduct(9999999L, command));
    }

    @Test
    void deleteProduct_shouldRemoveProductFromDatabase() {
        CreateProductCommand command = new CreateProductCommand("init label", "init description", "init category");
        Product created = productService.createProduct(command);

        long id = created.getId();

        Product fromDB = productRepo.findById(id).orElseThrow(() -> new AssertionError("Product was not found in database after creation"));

        assertEquals(id, fromDB.getId());

        productService.deleteProduct(id);

        assertFalse(productRepo.existsById(id));
        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(id));
    }

    @Test
    void deleteProduct_shouldThrowExceptionIfProductNotExists() {
        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(9999999L));
    }

    @Test
    void getProduct_shouldThrowExceptionIfProductNotExists() {
        assertThrows(ProductNotFoundException.class, () -> productService.getProductById(9999999L));
    }
}
