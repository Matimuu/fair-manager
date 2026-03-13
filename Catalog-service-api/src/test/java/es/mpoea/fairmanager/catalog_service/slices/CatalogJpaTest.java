package es.mpoea.fairmanager.catalog_service.slices;

import es.mpoea.fairmanager.catalog_service.persistence.models.Category;
import es.mpoea.fairmanager.catalog_service.persistence.models.Product;
import es.mpoea.fairmanager.catalog_service.persistence.repositories.CategoryRepo;
import es.mpoea.fairmanager.catalog_service.persistence.repositories.ProductRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CatalogJpaTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:18")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @Autowired
    private ProductRepo productRepo;

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void saveAndFindProductWithCategoryById_ShouldWork() {
        Category category = new Category("Test Category");
        Category savedCategory = categoryRepo.saveAndFlush(category);

        Product product = new Product("Test Product", "This is a test product", savedCategory);
        Product savedProduct = productRepo.saveAndFlush(product);
        testEntityManager.clear();

        Product foundProduct = productRepo.findById(savedProduct.getId()).orElseThrow();

        assertEquals("Test Product", foundProduct.getLabel());
        assertEquals("This is a test product", foundProduct.getDescription());

        assertNotNull(foundProduct.getCategory());
        assertNotNull(foundProduct.getCategory().getId());
        assertEquals("Test Category", foundProduct.getCategory().getName());
        assertNotNull(foundProduct.getCategory().getCreatedAt());
        assertNotNull(foundProduct.getCategory().getUpdatedAt());
        assertNotNull(foundProduct.getCategory().getVersion());

        assertNotNull(foundProduct.getCreatedAt(), "Saved product should have an Created At");
        assertNotNull(foundProduct.getUpdatedAt(), "Saved product should have an Updated At");
        assertNotNull(foundProduct.getVersion(), "Saved product should have a version");
    }

    @Test
    void updateProduct_ShouldUpdateTimestampsAndVersion() {
        Category category = new Category("Test Category");
        Category initCategory = categoryRepo.saveAndFlush(category);

        Category anotherCategory = new Category("Another Category");
        Category updatedCategory = categoryRepo.saveAndFlush(anotherCategory);

        Product product = new Product("Test Product", "This is a test product", initCategory);
        Product savedProduct = productRepo.saveAndFlush(product);
        Instant initUpdatedAt = savedProduct.getUpdatedAt();
        Long initVersion = savedProduct.getVersion();

        savedProduct.setLabel("Updated Product");
        savedProduct.setCategory(updatedCategory);

        productRepo.flush();

        testEntityManager.clear();

        Product fromDb = productRepo.findById(savedProduct.getId()).orElseThrow();

        assertEquals("Updated Product", fromDb.getLabel());
        assertEquals("This is a test product", fromDb.getDescription());
        assertNotNull(fromDb.getCategory());
        assertEquals("Another Category", fromDb.getCategory().getName());

        assertEquals(savedProduct.getCreatedAt(), fromDb.getCreatedAt());
        assertFalse(fromDb.getUpdatedAt().isBefore(initUpdatedAt));

        assertTrue(initVersion < fromDb.getVersion());
    }

    @Test
    void deleteProduct_ShouldRemoveItFromDatabase() {
        Product productToDelete = new Product("Product to Delete", "This product will be deleted", null);
        Product savedProduct = productRepo.saveAndFlush(productToDelete);
        Long productId = savedProduct.getId();

        assertNotNull(savedProduct);

        productRepo.deleteById(productId);

        productRepo.flush();
        testEntityManager.clear();

        assertTrue(productRepo.findById(productId).isEmpty());
    }

    @Test
    void saveAndFindCategory_ShouldWork() {
        Category categoryToSave = new Category("Test Category");

        Category savedCat = categoryRepo.saveAndFlush(categoryToSave);

        testEntityManager.clear();

        Category foundCategory = categoryRepo.findById(savedCat.getId()).orElseThrow();

        assertNotNull(foundCategory);
        assertNotNull(foundCategory.getId());
        assertEquals("Test Category", foundCategory.getName());
        assertNotNull(foundCategory.getCreatedAt(), "Saved category should have an Created At");
        assertNotNull(foundCategory.getUpdatedAt(), "Saved category should have an Updated At");
        assertNotNull(foundCategory.getVersion(), "Saved category should have a version");
    }

    @Test
    void updateCategory_ShouldUpdateTimestampsAndVersion() {
        Category categoryToSave = new Category("Test Category");
        Category savedCat = categoryRepo.saveAndFlush(categoryToSave);

        Instant initUpdatedAt = savedCat.getUpdatedAt();
        Long initVersion = savedCat.getVersion();


        savedCat.setName("Updated Category");
        categoryRepo.flush();
        testEntityManager.clear();

        Category fromDb = categoryRepo.findById(savedCat.getId()).orElseThrow();

        assertEquals("Updated Category", fromDb.getName());
        assertEquals(savedCat.getCreatedAt(), fromDb.getCreatedAt());
        assertFalse(fromDb.getUpdatedAt().isBefore(initUpdatedAt));
        assertTrue(initVersion < fromDb.getVersion());
    }

     @Test
    void deleteCategory_ShouldRemoveItFromDatabase() {
        Category cat = new Category("Should be deleted");
        Category savedCat = categoryRepo.saveAndFlush(cat);
        Long catId = savedCat.getId();

        categoryRepo.delete(savedCat);

        categoryRepo.flush();
        testEntityManager.clear();

        assertTrue(categoryRepo.findById(catId).isEmpty());
     }
}
